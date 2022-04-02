package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleAdapterListCustomerCart;
import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleAdapterListCustomer;
import com.developer.fooddeliveryapp.Customer.ItemsAdapter.ExampleItemCustomerList;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restraunt.ExampleItem;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ViewOrder extends AppCompatActivity implements PaymentResultListener {
    private RecyclerView mRecyclerView;
    private ExampleAdapterListCustomerCart mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<ExampleItemCustomerListCart> list = new ArrayList<>();
    DatabaseReference db;

    DatabaseReference reference;

    ImageButton buttonBack;
    TextView orderTotal;
    Button payment;
    int total=0;

    boolean b=false;
    String mobNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
//        Checkout.preload(getApplicationContext());

        orderTotal=findViewById(R.id.orderTotal);
        payment=findViewById(R.id.payOrder);
        buttonBack=findViewById(R.id.btnBackCart);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();

        Toast.makeText(getApplicationContext(),mobNo, Toast.LENGTH_SHORT).show();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepayment();
            }
        });

        createExampleList();

    }
    public void createExampleList() {
//        Intent intent = getIntent();
//        String mobNo = intent.getStringExtra("mobileNo");

        db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNo);
        reference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Cart").child(mobNo);

        mRecyclerView = findViewById(R.id.recyclerViewCartListItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    ExampleItemCustomerListCart p = dataSnapshot1.getValue(ExampleItemCustomerListCart.class);
                    list.add(p);
                }
                if (!list.isEmpty() && b==false)
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
                mAdapter = new ExampleAdapterListCustomerCart(list);


                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ExampleAdapterListCustomerCart.OnItemClickListener() {
                    @Override
                    public void addQuantityCart(int position) {
                        ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);
                        db.child(exampleItemCustomerList.getText1()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int price=Integer.parseInt(list.get(position).getText2());
                                total+=price;
                                orderTotal.setText(String.valueOf(total));
                                int category=Integer.parseInt(exampleItemCustomerList.getQuantity());
                                String cat=Integer.toString(category);
                                Toast.makeText(getApplicationContext(), cat, Toast.LENGTH_SHORT).show();
                                //add mien error h
                                writeItemDetails(exampleItemCustomerList.getText1(), exampleItemCustomerList.getText2(), cat);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
//                        Toast.makeText(getApplicationContext(), exampleItemCustomerList.getQuantity(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void removeQuantityCart(int position) {
                        ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);
                        db.child(exampleItemCustomerList.getText1()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int price=Integer.parseInt(list.get(position).getText2());
                                total-=price;
                                orderTotal.setText(String.valueOf(total));
                                if(exampleItemCustomerList.getQuantity().equals("0"))
                                {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("users").child("Customer").child("Cart").child(mobNo).child(exampleItemCustomerList.getText1()).removeValue();
                                    b=true;
                                    list.remove(position);
                                    //reload activity
//                                    finish();
                                    startActivity(new Intent(getApplicationContext(),ViewOrder.class));

                                }
                                else
                                {
                                    writeItemDetails(exampleItemCustomerList.getText1(), exampleItemCustomerList.getText2(), exampleItemCustomerList.getQuantity().toString());
                                }

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
        orderTotal.setText("Successful payment ID :"+s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        orderTotal.setText("Failed and cause is :"+s);
    }

    public void writeItemDetails(String itemName,String itemPrice,String quantity) {
        ExampleItemCustomerListCart item = new ExampleItemCustomerListCart(R.drawable.paneer,itemName,itemPrice,quantity);
        db.child(itemName).setValue(item);
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
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("users").child("Customer").child("Cart").child(mobNo).removeValue();
                        startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
                        Toast.makeText(ViewOrder.this,"Ok",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(ViewOrder.this,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }
}