package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.AddressAdapter.AddressAdapter;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAccountDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AddressAdapter adapter;
    RoundedImageView profileImage;
    TextView emailTv,mobileNoTv,nameTv;
    Button btnAddNewAddress;
    ImageButton btnBack;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_details);

        profileImage=findViewById(R.id.imageProfileAccountDetails);
        emailTv=findViewById(R.id.emailAccountDetails);
        mobileNoTv=findViewById(R.id.mobileNoAccountDetails);
        nameTv=findViewById(R.id.nameAccountDetails);
        btnAddNewAddress=findViewById(R.id.btnAddNewAddress);
        btnBack=findViewById(R.id.btnBackCartMyAccount);

        swipeRefreshLayout=findViewById(R.id.swipeContainerViewAccountDetailsCustomer);



        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        String mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();

        byte [] bytes= Base64.decode(image,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);

        profileImage.setImageBitmap(bitmap);
        nameTv.setText(name);
        emailTv.setText(email);
        mobileNoTv.setText(mobileNo);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList(mobileNo);
            }
        });
        createExampleList(mobileNo);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),AddNewAddressCustomer.class);
                intent1.putExtra("mobileNo",mobileNo);
                startActivity(intent1);
            }
        });

    }

    public void createExampleList(String mobileNo)
    {
        ArrayList<String>stringAddress=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Address").child(mobileNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    String p = dataSnapshot1.getValue(String.class);
                    stringAddress.add(p);
                }
                recyclerView = findViewById(R.id.recyclerViewMyAccountDetails);

                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getApplicationContext());

                adapter = new AddressAdapter(stringAddress);

                adapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
                    @Override
                    public void deleteAddress(int position) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Address").child(mobileNo);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    String p = dataSnapshot1.getValue(String.class);

                                    if (p.equals(stringAddress.get(position)))
                                    {
                                        dataSnapshot1.getRef().removeValue();
                                        Toast.makeText(MyAccountDetails.this, "Deleted "+position, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccountDetails.this, "HI", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
    }
}