package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleAdapterListCustomer;
import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleItemCustomerList;
import com.developer.fooddeliveryapp.MainActivity;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInActivity;
import com.developer.fooddeliveryapp.SignUpActivity;
import com.developer.fooddeliveryapp.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantItems extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ExampleAdapterListCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference GetUid;
    String email;
    String password, mobileNo;
    ImageButton buttonBack;
    ImageButton order;

    ArrayList<ExampleItemCustomerList> list = new ArrayList<>();

    DatabaseReference add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_items);
        order=findViewById(R.id.btnOrder);
        buttonBack=findViewById(R.id.btnBack);
        createExampleList();

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        String mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();
        String location=user.get("address").toString();

        Intent intent=getIntent();
        String mobNo=intent.getStringExtra("mobNo");

        String mobNoCustomer=intent.getStringExtra("mobileNumberCustomer");



        add=FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNoCustomer);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),ViewOrder.class);
                intent1.putExtra("mobileNo",mobNoCustomer);
                startActivity(intent1);
            }
        });
        Toast.makeText(getApplicationContext(), mobNo, Toast.LENGTH_SHORT).show();
    }
    public void createExampleList() {
        Intent intent = getIntent();
        String mobNo = intent.getStringExtra("mobNo");

//        Toast.makeText(getApplicationContext(), "InIt", Toast.LENGTH_SHORT).show();

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        GetUid = FirebaseDatabase.getInstance().getReference("users").child("all").child("uid").child(uid);
//
//        GetUid.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                email = snapshot.child("email").getValue(String.class);
//                password = snapshot.child("password").getValue(String.class);
//                mobileNo = snapshot.child("mobileNo").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mobNo).child("items");

        mRecyclerView = findViewById(R.id.recyclerViewListItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    ExampleItemCustomerList p = dataSnapshot1.getValue(ExampleItemCustomerList.class);
                    list.add(p);
                }

                mAdapter = new ExampleAdapterListCustomer(list);


                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ExampleAdapterListCustomer.OnItemClickListener() {

                    @Override
                    public void addToCart(int position) {
                        ExampleItemCustomerList exampleItemCustomerList=list.get(position);

                        add.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                writeNewCart(exampleItemCustomerList.getText1(),exampleItemCustomerList.getText2(),"1");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writeNewCart(String itemName, String itemPrice,String quantity) {
        ExampleItemCustomerList customerList = new ExampleItemCustomerList(R.drawable.paneer,itemName,itemPrice,quantity);
        add.child(itemName).setValue(customerList);
        Toast.makeText(getApplicationContext(), "Data Send Successfully IN rESTRAUNT", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}