package com.developer.fooddeliveryapp.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageAdapter;
import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAllPendingRequest extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DeliveryHomePageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String mobileNo;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noRequestDeliveryPartnerText;
    ImageButton btnBack;

    ArrayList<DeliveryHomePageModel>list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_pending_request);
        noRequestDeliveryPartnerText=findViewById(R.id.noRequestDeliveryPartnerText);
        btnBack=findViewById(R.id.btn_Back_DeliveryPendingRequest);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        mobileNo=user.get("mobileNo").toString();

        createExampleList();

        swipeRefreshLayout=findViewById(R.id.swipeContainerDeliveryViewAllPendingOrders);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void createExampleList() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child("accepted").child(mobileNo);

        mRecyclerView = findViewById(R.id.recyclerViewOrdersPendingDelivery);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    DeliveryHomePageModel p = dataSnapshot1.getValue(DeliveryHomePageModel.class);
                    list.add(p);
                }
                mAdapter = new DeliveryHomePageAdapter(list);

                if (!list.isEmpty()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    noRequestDeliveryPartnerText.setVisibility(View.GONE);
                }
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new DeliveryHomePageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        DeliveryHomePageModel exampleItemCustomer=list.get(position);

                        ArrayList<DeliveryHomePageModel> arrayList= new ArrayList<>();
                        arrayList.add(exampleItemCustomer);

                        Intent intent1=new Intent(getApplicationContext(), TrackDeliveryStatus.class);

                        intent1.putExtra("private_list", new Gson().toJson(arrayList));

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
        startActivity(new Intent(getApplicationContext(),DeliveryPartnerHomePage.class));
    }
}