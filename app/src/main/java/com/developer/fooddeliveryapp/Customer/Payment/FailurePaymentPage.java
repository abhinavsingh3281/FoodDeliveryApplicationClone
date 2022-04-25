package com.developer.fooddeliveryapp.Customer.Payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.R;

public class FailurePaymentPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure_payment_page);
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), CustomerHomePage.class));
    }
}