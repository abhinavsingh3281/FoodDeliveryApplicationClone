package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.fooddeliveryapp.Customer.ViewDetailedPendingRequestAdapterCustomer.DetailedPendingRequestModelCustomer;
import com.developer.fooddeliveryapp.Customer.ViewDetailedPendingRequestAdapterCustomer.ViewDetailedPendingRequestAdapterCustomer;
import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewDetailedOrdersCustomer extends AppCompatActivity {

    String orderId,mobileNo;
    ArrayList<OrderModel> list = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ViewDetailedPendingRequestAdapterCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ImageButton btnBack;

    SwipeRefreshLayout swipeRefreshLayout;


    ImageView image1_detailedOrder,image2_detailedOrder,image3_detailedOrder,image4_detailedOrder;
    TextView orderIdCustomerDetailedOrder,orderTotalDetailedOrders,amountInDetailedOrders,addressViewDetailedOrders,restaurantNameViewDetailedOrders,text1_detailedOrder,text2_detailedOrder,text3_detailedOrder,text4_detailedOrder,dash_1,dash_2,dash_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detailed_orders_customer);

        btnBack=findViewById(R.id.btnBackDetailedOrder);
        swipeRefreshLayout=findViewById(R.id.swipeContainerViewMyDetailedOrdersCustomer);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        image1_detailedOrder=findViewById(R.id.image1_detailedOrder);
        image2_detailedOrder=findViewById(R.id.image2_detailedOrder);
        image3_detailedOrder=findViewById(R.id.image3_detailedOrder);
        image4_detailedOrder=findViewById(R.id.image4_detailedOrder);

        orderIdCustomerDetailedOrder=findViewById(R.id.orderIdCustomerDetailedOrder);
        amountInDetailedOrders=findViewById(R.id.amountInDetailedOrders);
        addressViewDetailedOrders=findViewById(R.id.addressViewDetailedOrders);
        restaurantNameViewDetailedOrders=findViewById(R.id.restaurantNameViewDetailedOrders);
        orderTotalDetailedOrders=findViewById(R.id.orderTotalDetailedOrders);

        text1_detailedOrder=findViewById(R.id.text1_detailedOrder);
        text2_detailedOrder=findViewById(R.id.text2_detailedOrder);
        text3_detailedOrder=findViewById(R.id.text3_detailedOrder);
        text4_detailedOrder=findViewById(R.id.text4_detailedOrder);

        dash_1=findViewById(R.id.dash_1);
        dash_2=findViewById(R.id.dash_2);
        dash_3=findViewById(R.id.dash_3);

        Intent intent=getIntent();
        orderId=intent.getStringExtra("orderId");
        mobileNo=intent.getStringExtra("mobileNo");

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList(mobileNo,orderId);
            }
        });

        createExampleList(mobileNo,orderId);

    }

    public void createExampleList(String mobileNo,String orderId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(mobileNo).child(orderId);

        mRecyclerView = findViewById(R.id.recyclerViewDetailedOrdersCustomer);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                OrderModel p = snapshot.getValue(OrderModel.class);
                list.add(p);

                ArrayList<DetailedPendingRequestModelCustomer> detailedPendingRequestModel=new ArrayList<>();
                for (int i=0;i<list.get(0).getFood().size();i++)
                {
                    DetailedPendingRequestModelCustomer model=new DetailedPendingRequestModelCustomer();
                    model.setText1(list.get(0).getFood().get(i).getText1());
                    model.setQuantity(list.get(0).getFood().get(i).getQuantity());

                    detailedPendingRequestModel.add(model);
                }
                mAdapter = new ViewDetailedPendingRequestAdapterCustomer(detailedPendingRequestModel);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                orderIdCustomerDetailedOrder.setText(list.get(0).getOrderId());
                amountInDetailedOrders.setText(list.get(0).getPrice());
                addressViewDetailedOrders.setText(list.get(0).getAddress());
                restaurantNameViewDetailedOrders.setText(list.get(0).getRestaurantName());
                orderTotalDetailedOrders.setText(list.get(0).getPrice());
                if (list.get(0).getStatus().equals("Order Accepted By The Restaurant"))
                {
                    dash_1.setTextColor(Color.parseColor("#FFFD4A08"));
                    image2_detailedOrder.setImageResource(R.drawable.ic_kitchen_red);
                    text2_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));
                }
                if (list.get(0).getStatus().equals("Order on the Way"))
                {
                    dash_1.setTextColor(Color.parseColor("#FFFD4A08"));
                    image2_detailedOrder.setImageResource(R.drawable.ic_kitchen_red);
                    text2_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));

                    dash_2.setTextColor(Color.parseColor("#FFFD4A08"));
                    image3_detailedOrder.setImageResource(R.drawable.ic_order_red);
                    text3_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));
                }
                if (list.get(0).getStatus().equals("Delivered"))
                {
                    dash_1.setTextColor(Color.parseColor("#FFFD4A08"));
                    image2_detailedOrder.setImageResource(R.drawable.ic_kitchen_red);
                    text2_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));

                    dash_2.setTextColor(Color.parseColor("#FFFD4A08"));
                    image3_detailedOrder.setImageResource(R.drawable.ic_order_red);
                    text3_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));

                    dash_3.setTextColor(Color.parseColor("#FFFD4A08"));
                    image4_detailedOrder.setImageResource(R.drawable.ic_delivered_red);
                    text4_detailedOrder.setTextColor(Color.parseColor("#FFFD4A08"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}