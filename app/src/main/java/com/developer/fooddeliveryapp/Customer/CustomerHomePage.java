package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleAdapterCustomer;
import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleItemCustomer;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInAndUp.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageButton;

public class CustomerHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView mRecyclerView;
    private ExampleAdapterCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextInputEditText pinCode;
    private Button checkPin;

    String uid;

    ArrayList<ExampleItemCustomer> list = new ArrayList<>();


    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        pinCode=findViewById(R.id.customerPinCode);
        checkPin=findViewById(R.id.btnCustomerPinCode);


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        String mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();

        pinCode.setText(pincode);

        createExampleList(pincode,mobileNo);

        byte [] bytes= Base64.decode(image,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);


        Toolbar toolbar = findViewById(R.id.toolbar_customer);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_customer);
        NavigationView navigationView = findViewById(R.id.nav_view_customer);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.header_email);
        RoundedImageView imageView=(RoundedImageView) headerView.findViewById(R.id.header_image);
        imageView.setImageBitmap(bitmap);
        navUsername.setText(name);
        navEmail.setText(email);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        checkPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExampleList(pinCode.getText().toString(),mobileNo);

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                new FancyGifDialog.Builder(this)
                        .setTitle("Do you want to Sign-Out?") // You can also send title like R.string.from_resources
                        .setMessage("By Pressing SIGN-OUT you will not be able to use our services.") // or pass like R.string.description_from_resources
                        .setTitleTextColor(R.color.titleText)
                        .setDescriptionTextColor(R.color.descriptionText)
                        .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                        .setPositiveBtnBackground(R.color.positiveButton)
                        .setPositiveBtnText("SIGN OUT") // or pass it like android.R.string.ok
                        .setNegativeBtnBackground(R.color.negativeButton)
                        .setGifResource(R.drawable.giflogoutmessage)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                LogOut();
                                Toast.makeText(CustomerHomePage.this,"Ok",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(CustomerHomePage.this,"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
                break;
            case R.id.nav_orders:
                startActivity(new Intent(getApplicationContext(),ViewMyOrders.class));
                break;
//            case R.id.nav_share:
//                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.nav_send:
//                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
//                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }

    public void createExampleList(String pin,String mobileNo) {
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
                        intent1.putExtra("restaurantName",exampleItemCustomer.getRestaurantName());
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