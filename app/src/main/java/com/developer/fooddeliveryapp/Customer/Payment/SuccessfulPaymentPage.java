package com.developer.fooddeliveryapp.Customer.Payment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Customer.ViewDetailedOrdersCustomer;
import com.developer.fooddeliveryapp.Customer.ViewMyOrders;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SuccessfulPaymentPage extends AppCompatActivity {

    TextView textView;
    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_payment_page);

        textView=findViewById(R.id.transactionIdCustomer);
        timer=findViewById(R.id.textView);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        String mobNo = user.get("mobileNo").toString();

        Intent intent=getIntent();
        String transactionId=intent.getStringExtra("id");

        String textViewText=textView.getText().toString();
        textView.setText(textViewText+transactionId);

        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timer.setText(f.format(min) + ":" + f.format(sec));
            }
            // When the task is over it will print 00:00:00 there
            public void onFinish() {
                timer.setText("00:00");
            }
        }.start();

        new Timer().schedule(new TimerTask(){
            public void run() {
                SuccessfulPaymentPage.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent1=new Intent(SuccessfulPaymentPage.this, ViewMyOrders.class);
                        startActivity(intent1);
                    }
                });
            }
        }, 5000);
    }
    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(), CustomerHomePage.class));
    }
}