package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleAdapterListCustomer;
import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleItemCustomerList;
import com.developer.fooddeliveryapp.MainActivity;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SignInActivity;
import com.developer.fooddeliveryapp.SignUpActivity;
import com.developer.fooddeliveryapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantItems extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ExampleAdapterListCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference GetUid;
    String email;
    String password, mobileNo;

    Button order;

    ArrayList<ExampleItemCustomerList> list = new ArrayList<>();

    DatabaseReference reference,add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_items);
        order=findViewById(R.id.btnOrder);

        Intent intent=getIntent();
        String mobNo=intent.getStringExtra("mobNo");

        String mobNoCustomer=intent.getStringExtra("mobileNumberCustomer");

        reference=FirebaseDatabase.getInstance().getReference("users").child("Customer");

        add=FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNoCustomer);
        createExampleList();
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

        Toast.makeText(getApplicationContext(), "InIt", Toast.LENGTH_SHORT).show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GetUid = FirebaseDatabase.getInstance().getReference("users").child("all").child("uid").child(uid);

        GetUid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("email").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);
                mobileNo = snapshot.child("mobileNo").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

//                    @Override
//                    public void onItemClick(int position) {
//                        ExampleItemCustomerList exampleItemCustomerList=list.get(position);
//                        Intent intent1=new Intent(getApplicationContext(),ViewOrder.class);
//                        String itemName= exampleItemCustomerList.getText1();
//                        String itemPrice=exampleItemCustomerList.getText2();
//
//                        intent1.putExtra("itemName",itemName);
//                        intent1.putExtra("itemPrice",itemPrice);
//
//                        startActivity(intent1);
//                    }
//
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void writeNewCart(String itemName, String itemPrice,String quantity) {
        ExampleItemCustomerList customerList = new ExampleItemCustomerList(R.drawable.ic_launcher_foreground,itemName,itemPrice,quantity);
        add.child(itemName).setValue(customerList);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();
    }
}