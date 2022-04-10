package com.developer.fooddeliveryapp.Customer.Payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.R;

public class SuccessfulPaymentPage extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_payment_page);

        textView=findViewById(R.id.transactionIdCustomer);

        Intent intent=getIntent();
        String transactionId=intent.getStringExtra("id");

        String textViewText=textView.getText().toString();
        textView.setText(textViewText+transactionId);
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), CustomerHomePage.class));
    }
}