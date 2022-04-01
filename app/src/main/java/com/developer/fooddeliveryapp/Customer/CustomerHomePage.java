package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restraunt.ExampleAdapter;
import com.developer.fooddeliveryapp.Restraunt.ExampleItem;
import com.developer.fooddeliveryapp.Restraunt.RestaurantHomePage;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView mRecyclerView;
    private ExampleAdapterCustomer mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextInputEditText pinCode;
    private Button checkPin;

    DatabaseReference getUserDetails;
    String email;
    String password, mobileNo;
    String uid;

    ArrayList<ExampleItemCustomer> list = new ArrayList<>();

    ImageButton viewCart;

    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        pinCode=findViewById(R.id.customerPinCode);
        checkPin=findViewById(R.id.btnCustomerPinCode);

        viewCart=findViewById(R.id.toolbarViewCart);

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerHomePage.this,ViewOrder.class));
            }
        });

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
//        drawerLayout.setFitsSystemWindows(true);
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomerHomePage.this);
                builder1.setMessage("Do you want to Log Out");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LogOut();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
//            case R.id.nav_chat:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ChatFragment()).commit();
//                break;
//            case R.id.nav_profile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();
//                break;
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
                        intent1.putExtra("mobileNumberCustomer",mobileNo);
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