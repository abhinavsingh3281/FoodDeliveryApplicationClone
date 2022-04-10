package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.ItemsInRestaurantAdapter.ExampleAdapterListCustomer;
import com.developer.fooddeliveryapp.Customer.ItemsInRestaurantAdapter.ExampleItemCustomerList;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SharedPrefList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RestaurantItems extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private static ExampleAdapterListCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ImageButton buttonBack;
    ImageButton order;
    TextView restaurantNameTv;

    ArrayList<ExampleItemCustomerList> list = new ArrayList<>();

    private List<ExampleItemCustomerListCart> taskList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_items);
        order=findViewById(R.id.btnOrder);
        buttonBack=findViewById(R.id.btnBack);
        restaurantNameTv=findViewById(R.id.restaurantName);


        taskList = SharedPrefList.readListFromPref(this);
        if (taskList == null)
            taskList = new ArrayList<>();


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
        String restaurantName=intent.getStringExtra("restaurantName");

        restaurantNameTv.setText(restaurantName);


//        Toast.makeText(getApplicationContext(), restaurantName, Toast.LENGTH_SHORT).show();
//        String mobNoCustomer=intent.getStringExtra("mobileNumberCustomer");

        createExampleList(mobileNo);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefList.deleteInPref(getApplicationContext());
                onBackPressed();
            }
        });


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefList.writeListInPref(getApplicationContext(), taskList);
                Intent intent1=new Intent(getApplicationContext(),ViewOrder.class);
                intent1.putExtra("restaurantName",restaurantName);
                startActivity(intent1);
            }
        });
        Toast.makeText(getApplicationContext(), mobNo, Toast.LENGTH_SHORT).show();
    }


    public void createExampleList(String mobNoCustomer) {
        Intent intent = getIntent();
        String mobNo = intent.getStringExtra("mobNo");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mobNo).child("items");
        DatabaseReference add=FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNoCustomer);


        mRecyclerView = findViewById(R.id.recyclerViewListItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                ExampleItemCustomerListCart exampleItemCustomerListCart=new ExampleItemCustomerListCart();
                                exampleItemCustomerListCart.setText1(exampleItemCustomerList.getText1());
                                exampleItemCustomerListCart.setText2(exampleItemCustomerList.getText2());
                                exampleItemCustomerListCart.setQuantity(exampleItemCustomerList.getQuantity());

                                taskList.add(exampleItemCustomerListCart);
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

    @Override
    public void onBackPressed() {
        new FancyGifDialog.Builder(this)
                .setTitle("Are you sure you want to go back ?") // You can also send title like R.string.from_resources
                .setMessage("By Pressing OK your cart data will be removed.") // or pass like R.string.description_from_resources
                .setTitleTextColor(R.color.titleText)
                .setDescriptionTextColor(R.color.descriptionText)
                .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.positiveButton)
                .setPositiveBtnText("Ok") // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.negativeButton)
                .setGifResource(R.drawable.giflogout)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
                        Toast.makeText(RestaurantItems.this,"Ok",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(RestaurantItems.this,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

}