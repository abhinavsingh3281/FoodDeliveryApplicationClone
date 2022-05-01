package com.developer.fooddeliveryapp.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.developer.fooddeliveryapp.AddressModel;
import com.developer.fooddeliveryapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddNewAddressCustomer extends AppCompatActivity {

    private TextInputEditText address;
    private Button btnProceedToCartCustomer;
    String mobileNumber,restaurantName,restaurantMob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address_customer);

        address=findViewById(R.id.newAddressCustomer);
        btnProceedToCartCustomer=findViewById(R.id.btnProceedToCartCustomer);



        Intent intent=getIntent();
        mobileNumber=intent.getStringExtra("mobileNo");
        restaurantName=intent.getStringExtra("restaurantName");
        restaurantMob=intent.getStringExtra("restaurantMob");

        btnProceedToCartCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressModel addressModel=new AddressModel();
                addressModel.setAddress(address.getText().toString());

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Address").child(mobileNumber).child(UUID.randomUUID().toString());
                databaseReference.setValue(addressModel.getAddress());
                Intent intent1=new Intent(getApplicationContext(),ViewOrder.class);
                intent1.putExtra("restaurantName",restaurantName);
                intent1.putExtra("restaurantMob",restaurantMob);
                startActivity(intent1);
            }
        });
    }
}