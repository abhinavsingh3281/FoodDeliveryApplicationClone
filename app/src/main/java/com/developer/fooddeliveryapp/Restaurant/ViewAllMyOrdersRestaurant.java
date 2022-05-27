package com.developer.fooddeliveryapp.Restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restaurant.ViewOrdersAdapter.ExampleAdapterOrdersRestaurant;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAllMyOrdersRestaurant extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ExampleAdapterOrdersRestaurant mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String restaurantName,restaurantMob;

    ImageButton imageButton;
    ArrayList<OrderModel> list = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_my_orders_restaurant);

        imageButton=findViewById(R.id.btn_Back_RestaurantMyRequest);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        restaurantName=user.get("restaurantName").toString();
        restaurantMob=user.get("mobileNo").toString();

        Toast.makeText(this, restaurantName, Toast.LENGTH_SHORT).show();

        createExampleList(restaurantMob);

        swipeRefreshLayout=findViewById(R.id.swipeContainerRestaurantViewAllMyOrders);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList(restaurantMob);
            }
        });
    }

    public void createExampleList(String restaurantMob) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Orders").child(restaurantMob).child("accept");

        mRecyclerView = findViewById(R.id.recyclerViewMyOrdersRestaurant);
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
                mAdapter = new ExampleAdapterOrdersRestaurant(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new ExampleAdapterOrdersRestaurant.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(ViewAllMyOrdersRestaurant.this, "HELLO", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(getApplicationContext(),RestaurantHomePage.class));
    }
}