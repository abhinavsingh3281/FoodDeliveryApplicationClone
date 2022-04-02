package com.developer.fooddeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Restraunt.RestaurantHomePage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText mobileNo, PassTB;
    Button LoginB;
    Button signup;
    String role=null;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    DatabaseReference reference;
    DatabaseReference getReference;

    AutoCompleteTextView spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mobileNo = findViewById(R.id.mobileSignIn);
        PassTB = findViewById(R.id.passwordSignIn);
        signup=findViewById(R.id.btnSignUpCustomer);

        spinner = findViewById(R.id.spinnerTypeSignIn);

        initUI();

        reference=FirebaseDatabase.getInstance().getReference("users").child("all");

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
                }
            });
            LoginB = findViewById(R.id.login);
            if (FirebaseAuth.getInstance().getCurrentUser()!= null) {
                SessionManager session = new SessionManager(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                String userId = user.get("userId").toString();
                String categoryId = user.get("role").toString();
                String mobileNo=user.get("mobileNo").toString();

                if (categoryId.equals("Customer")){
                    startActivity(new Intent(this, CustomerHomePage.class));
                }
                else {
                    Intent intent =new Intent(getApplicationContext(),RestaurantHomePage.class);
                    intent.putExtra("mobileNo",mobileNo);
                    startActivity(intent);
                }
            }

            else {
                LoginB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getData(mobileNo.getText().toString());
                    }
                });
            }

        }
    private void getData(String mobileNo) {
        String s=spinner.getText().toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(s).child(mobileNo);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String email = snapshot.child("email").getValue(String.class);
                String password = snapshot.child("password").getValue(String.class);
                String name=snapshot.child("name").getValue(String.class);
                String image=snapshot.child("image").getValue(String.class);
                String address=snapshot.child("address").getValue(String.class);
                String pincode=snapshot.child("pinCode").getValue(String.class);

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        writeNewUserWithUid(name,email,mobileNo,password,FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),s);

                        SessionManager session = new SessionManager(getApplicationContext());
                        session.createLoginSession(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),s.trim().toString(),mobileNo,image,name,email,address,pincode);
                        if (s.equals("Customer"))
                        {
                            Intent intent=new Intent(getApplicationContext(), CustomerHomePage.class);
                            intent.putExtra("mobileNo",mobileNo);
                            intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            startActivity(intent);
                        }
                        else{
                            Intent intent=new Intent(getApplicationContext(),RestaurantHomePage.class);
                            intent.putExtra("mobileNo",mobileNo);
                            intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void writeNewUserWithUid(String name, String email, String mobileno,String password,String uid,String role) {
        User user = new User(name, email, mobileno, password,role);
        reference.child("uid").child(uid).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }
    private void initUI() {

        ArrayList<String> customerList = getCustomerList();

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignInActivity.this, android.R.layout.simple_spinner_dropdown_item, customerList);

        //Set adapter
        spinner.setAdapter(adapter);
    }
    

    private ArrayList<String> getCustomerList()
    {
        ArrayList<String> users = new ArrayList<>();
        users.add("Customer");
        users.add("Restaurant");

        return users;
    }
}