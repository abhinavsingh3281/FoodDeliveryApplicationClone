package com.developer.fooddeliveryapp.Restraunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RestaurantHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<ExampleItem> mExampleList;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference deleteDatabaseReference;

    ArrayList<ExampleItem>list=new ArrayList<>();

    DatabaseReference reference;
    DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restraunt_home_page);

//        drawerLayout = findViewById(R.id.drawer_layout);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//
//        // pass the Open and Close toggle for the drawer layout listener
//        // to toggle the button
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//
//        // to make the Navigation drawer icon always appear on the action bar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        createExampleList();
        setButtons();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RestaurantHomePage.this);
                builder1.setMessage("Do you want to Log Out");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LogOut();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    public void removeItem(int position) {
        Intent intent=getIntent();
        String mob=intent.getStringExtra("mobileNo");
        firebaseDatabase = FirebaseDatabase.getInstance();

        String s=list.get(position).getText1();

        deleteDatabaseReference = firebaseDatabase.getReference("users").child("Restaurant").child(mob).child("items").child(s);

        deleteDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();

                }
                list.remove(position);
                mAdapter.notifyItemRemoved(position);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });



    }

    public void createExampleList() {
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
//                        changeItem(position, "Clicked");
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
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched

        TextInputEditText itemName=popupView.findViewById(R.id.setItemName);
        TextInputEditText itemPrice=popupView.findViewById(R.id.setItemPrice);

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
                               writeItemDetails(itemName.getText().toString(),itemPrice.getText().toString());

                                mAdapter.notifyDataSetChanged();
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

        ExampleItem item = new ExampleItem(R.drawable.paneer,itemName, itemPrice);
        reference.child("items").child(itemName).setValue(item);
        list.add(item);
    }
}