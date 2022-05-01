package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

    ImageButton backButton;

    String mobileNo;
    ArrayList<OrderModel> list = new ArrayList<>();

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_orders);

        SessionManager session = new SessionManager(getApplicationContext());

        backButton=findViewById(R.id.btn_back_view_my_orders);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();

        createExampleList(mobileNo);

        swipeRefreshLayout=findViewById(R.id.swipeContainerViewMyOrdersCustomer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList(mobileNo);
            }
        });

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

                mAdapter.setOnItemClickListener(new ExampleAdapterOrdersCustomer.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        OrderModel orderModel=list.get(position);
                        Intent intent1=new Intent(getApplicationContext(),ViewDetailedOrdersCustomer.class);
                        intent1.putExtra("orderId",orderModel.getOrderId());
                        intent1.putExtra("mobileNo",orderModel.getMobileNo());
                        startActivity(intent1);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
    }
}