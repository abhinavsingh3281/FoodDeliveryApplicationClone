package com.developer.fooddeliveryapp.Restraunt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RestaurantHomePage extends AppCompatActivity {
    private ArrayList<ExampleItem> mExampleList;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference deleteDatabaseReference;


    DatabaseReference getUserDetails;
    String email;
    String password,mobileNo;

    ArrayList<ExampleItem>list=new ArrayList<>();

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt_home_page);

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserDetails=FirebaseDatabase.getInstance().getReference("users").child("all").child("uid").child(uid);

        getUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email= snapshot.child("email").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);
                mobileNo=snapshot.child("mobileNo").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        createExampleList();
        setButtons();
    }

    public void removeItem(int position) {
        Intent intent=getIntent();
        String mob=intent.getStringExtra("mobileNo");
        firebaseDatabase = FirebaseDatabase.getInstance();

        String s=list.get(position).getText1();

        deleteDatabaseReference = firebaseDatabase.getReference("users").child("Restaurant").child(mob).child("items").child(s);

        deleteDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text) {
        list.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    public void createExampleList() {
//        mExampleList = new ArrayList<>();
//        mExampleList.add(new ExampleItem( "Line 1", "Line 2"));

        Intent intent=getIntent();
        String mob=intent.getStringExtra("mobileNo");

        DatabaseReference db=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mob).child("items");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                {
                    ExampleItem p = dataSnapshot1.getValue(ExampleItem.class);
                    list.add(p);
                }
                mAdapter = new ExampleAdapter(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        changeItem(position, "Clicked");
                    }
                    @Override
                    public void onDeleteClick(int position) {
                        removeItem(position);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Toast.makeText(getApplicationContext(), mob, Toast.LENGTH_SHORT).show();

    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void setButtons() {
        buttonInsert = findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });

    }
    public void onButtonShowPopupWindowClick(View view) {
        firebaseDatabase = FirebaseDatabase.getInstance();

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched

        EditText itemName=popupView.findViewById(R.id.setItemName);
        EditText itemPrice=popupView.findViewById(R.id.setItemPrice);

        Button add=popupView.findViewById(R.id.saveItems);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=getIntent();
                String mob=intent.getStringExtra("mobileNo");

                reference=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mob);

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                createExampleList();
                                writeItemDetails(itemName.getText().toString(),itemPrice.getText().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        popupWindow.dismiss();
                    }
                });


    }
    public void writeItemDetails(String itemName,String itemPrice) {

        ExampleItem item = new ExampleItem(R.drawable.ic_launcher_foreground,itemName, itemPrice);
        reference.child("items").child(itemName).setValue(item);
    }
}