package com.developer.fooddeliveryapp.Restraunt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Customer.RestaurantItems;
import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restraunt.ViewDetailedPendingRequestAdapter.DetailedPendingRequestModel;
import com.developer.fooddeliveryapp.Restraunt.ViewDetailedPendingRequestAdapter.ViewDetailedPendingRequestAdapter;
import com.developer.fooddeliveryapp.Restraunt.ViewOrdersAdapter.ExampleAdapterOrdersRestaurant;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewDetailedPendingRequest extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ViewDetailedPendingRequestAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String restaurantName,restaurantMob;

    Button btnAcceptRequest,btnRejectRequest;

    ImageButton backButton;
    ArrayList<DetailedPendingRequestModel>list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detailed_pending_request);

        btnAcceptRequest=findViewById(R.id.btnAcceptRequest);
        btnRejectRequest=findViewById(R.id.btnRejectRequest);

        backButton=findViewById(R.id.btn_Back_RestaurantViewDetailedPendingRequest);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        String orderId=intent.getStringExtra("orderId");

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        restaurantName=user.get("restaurantName").toString();
        restaurantMob=user.get("mobileNo").toString();

        ArrayList<OrderModel> listPrivate = new ArrayList<>();

        Type type = new TypeToken<List<OrderModel>>() {
        }.getType();
        listPrivate = new Gson().fromJson(getIntent().getStringExtra("private_list"), type);

        Log.d("HELLO",listPrivate.get(0).getEmail());

        createExampleList(orderId,restaurantMob);

        listPrivate.get(0).setStatus("Order Accepted By The Restaurant");
        ArrayList<OrderModel> finalListPrivate = listPrivate;
        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Orders").child(restaurantMob).child("accept").child(orderId);
                databaseReference.setValue(finalListPrivate.get(0));


                DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(finalListPrivate.get(0).getMobileNo()).child(orderId);
                databaseReference1.setValue(finalListPrivate.get(0));
                deletePending(orderId);

                startActivity(new Intent(getApplicationContext(),ViewAllPendingRequestRestaurant.class));
            }
        });

        btnRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(orderId);
            }
        });

        Toast.makeText(this, orderId, Toast.LENGTH_SHORT).show();
    }

    public void deletePending(String orderId)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Orders").child(restaurantMob).child("pending").child(orderId);
        databaseReference.removeValue();
    }

    public void createExampleList(String orderId,String restaurantMob) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Orders").child(restaurantMob).child("pending").child(orderId).child("food");

        mRecyclerView = findViewById(R.id.recyclerViewOrdersDetailedPendingRestaurant);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    DetailedPendingRequestModel p = dataSnapshot1.getValue(DetailedPendingRequestModel.class);
                    list.add(p);
                }
                mAdapter = new ViewDetailedPendingRequestAdapter(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

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