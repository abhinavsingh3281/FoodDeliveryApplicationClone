package com.developer.fooddeliveryapp.SignInAndUp.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;

public class SignUpMainActivity extends AppCompatActivity {

    AutoCompleteTextView spinner;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_main);

        spinner = findViewById(R.id.spinnerTypeSignUp);
        signUp=findViewById(R.id.btnProceedSignUp);

        ArrayList<String> users = new ArrayList<>();
        users.add("Customer");
        users.add("Restaurant");

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpMainActivity.this, android.R.layout.simple_spinner_dropdown_item, users);

        //Set adapter
        spinner.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getText().toString().equals("Customer"))
                {
                    Intent intent=new Intent(getApplicationContext(),SignUpCustomerActivity.class);
                    intent.putExtra("userType",spinner.getText());
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),SignUpRestaurant.class);
                    intent.putExtra("userType",spinner.getText());
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "REST", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}