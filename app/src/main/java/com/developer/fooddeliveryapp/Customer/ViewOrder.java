package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleAdapterListCustomerCart;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ViewOrder extends AppCompatActivity implements PaymentResultListener {
    private RecyclerView mRecyclerView;
    private ExampleAdapterListCustomerCart mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<ExampleItemCustomerListCart> list = new ArrayList<>();

    ImageButton buttonBack;
    TextView orderTotal;
    Button payment;

    int total;
    String mobNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        list = SharedPrefList.readListFromPref(this);
        if (list == null)
            list = new ArrayList<>();


        orderTotal = findViewById(R.id.orderTotal);
        payment = findViewById(R.id.payOrder);
        buttonBack = findViewById(R.id.btnBackCart);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPrefList.deleteInPref(getApplicationContext());
//                list = new ArrayList<>();
                onBackPressed();
            }
        });

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobNo = user.get("mobileNo").toString();
        String image = user.get("image").toString();
        String email = user.get("email").toString();
        String name = user.get("name").toString();


        createExampleList();

        Toast.makeText(getApplicationContext(), mobNo, Toast.LENGTH_SHORT).show();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepayment();
            }
        });


    }

    public void createExampleList() {

        mRecyclerView = findViewById(R.id.recyclerViewCartListItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

                if (!list.isEmpty())
                {
                    for (int i=0;i<list.size();i++)
                    {
                        int quantity=Integer.parseInt(list.get(i).getQuantity());
                        int price=Integer.parseInt(list.get(i).getText2());

                        Log.d("TAG", String.valueOf(quantity));
                        total+=price*quantity;

                    }
                    orderTotal.setText(String.valueOf(total));

                }
        mAdapter = new ExampleAdapterListCustomerCart((ArrayList<ExampleItemCustomerListCart>) list);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ExampleAdapterListCustomerCart.OnItemClickListener() {
                    @Override
                    public void addQuantityCart(int position) {
                        ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);
                        int price=Integer.parseInt(list.get(position).getText2());
                        total+=price;
                        orderTotal.setText(String.valueOf(total));
                        int quantity=Integer.parseInt(exampleItemCustomerList.getQuantity());

                        Toast.makeText(getApplicationContext(), String.valueOf(quantity), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void removeQuantityCart(int position) {
                      ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);

                      int price=Integer.parseInt(list.get(position).getText2());
                      total-=price;
                      orderTotal.setText(String.valueOf(total));
                    }

                });
    }

    private void makepayment()
    {
        Intent intent = getIntent();
        String mobNo = intent.getStringExtra("mobileNo");
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_ovElVtiJzGjxYO");

        checkout.setImage(R.drawable.rzp_name_logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Food Delivery ");
            options.put("description", "Reference No. "+ UUID.randomUUID().toString().substring(0,7));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(orderTotal.getText().toString())*100);//300 X 100
            options.put("prefill.email", "food.delivery.app@gmail.com");
            options.put("prefill.contact",mobNo);
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s)
    {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNo);
        orderTotal.setText("Successful payment ID :"+s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        orderTotal.setText("Failed and cause is :"+s);
    }
//
////    private void writeItemDetails(String itemName,String itemPrice,String quantity) {
////        DatabaseReference db=FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNo);
////        ExampleItemCustomerListCart item = new ExampleItemCustomerListCart(itemName,itemPrice,quantity);
////        db.child(itemName).setValue(item);
////    }
}