package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restraunt.ExampleAdapter;
import com.developer.fooddeliveryapp.Restraunt.ExampleItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerHomePage extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ExampleAdapterCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText pinCode;
    private Button checkPin;

    DatabaseReference getUserDetails;
    String email;
    String password, mobileNo;

    ArrayList<ExampleItemCustomer> list = new ArrayList<>();

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        pinCode=findViewById(R.id.customerPinCode);
        checkPin=findViewById(R.id.btnCustomerPinCode);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserDetails = FirebaseDatabase.getInstance().getReference("users").child("all").child("uid").child(uid);

        getUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
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
        checkPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExampleList(pinCode.getText().toString());
            }
        });

    }

    public void createExampleList(String pin) {

        Toast.makeText(getApplicationContext(), "InIt", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        String mob = intent.getStringExtra("mobileNo");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Delivery").child(pin);

        mRecyclerView = findViewById(R.id.recyclerViewCustomer);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    ExampleItemCustomer p = dataSnapshot1.getValue(ExampleItemCustomer.class);
                    list.add(p);
                }
                mAdapter = new ExampleAdapterCustomer(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new ExampleAdapterCustomer.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ExampleItemCustomer exampleItemCustomer=list.get(position);
                        Intent intent1=new Intent(getApplicationContext(),RestaurantItems.class);
                        intent1.putExtra("mobNo",exampleItemCustomer.getMobileNo());
                        intent1.putExtra("mobileNumberCustomer",mob);
                        startActivity(intent1);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}