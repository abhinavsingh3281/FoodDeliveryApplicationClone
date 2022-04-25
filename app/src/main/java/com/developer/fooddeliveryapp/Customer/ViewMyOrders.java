package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleAdapterCustomer;
import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleItemCustomer;
import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.ExampleAdapterOrdersCustomer;
import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewMyOrders extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ExampleAdapterOrdersCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String mobileNo;
    ArrayList<OrderModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_orders);
        SessionManager session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();

        createExampleList(mobileNo);

    }
    public void createExampleList(String mobileNo) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(mobileNo);

        mRecyclerView = findViewById(R.id.recyclerViewOrdersCustomer);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    OrderModel p = dataSnapshot1.getValue(OrderModel.class);
                    list.add(p);
                }
                mAdapter = new ExampleAdapterOrdersCustomer(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}