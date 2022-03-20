package com.developer.fooddeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    TextInputEditText Email, Pass,name,mobNo,confirmPass,etPinCode;
    Button LoginB;
    FirebaseAuth firebaseAuth;


    private DatabaseReference databaseReferenceCustomer;
    private DatabaseReference databaseReferenceRestaurant;
    private DatabaseReference databaseReferencePinCodeRestaurant;
    
    String strName, strMob,strEmail,strPassword,userType,pinCode;
     AutoCompleteTextView spinner;
//    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Email = findViewById(R.id.emailSignUpCustomer);
        Pass = findViewById(R.id.passSignUpCustomer);
        LoginB = findViewById(R.id.SignUp);
        name=findViewById(R.id.nameSignUpCustomer);
        mobNo=findViewById(R.id.mobileNumberSignUpCustomer);
        confirmPass=findViewById(R.id.ConfirmPasswordSignUpCustomer);
//        spinner=findViewById(R.id.spinnerTypeSignUp);
        etPinCode=findViewById(R.id.pinCodeSignUpCustomer);

        spinner = findViewById(R.id.spinnerTypeSignUp);

        initUI();
        databaseReferenceCustomer = FirebaseDatabase.getInstance().getReference("users").child("Customer");
        databaseReferenceRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
        databaseReferencePinCodeRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
        firebaseAuth= FirebaseAuth.getInstance();

        LoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
                RegisterDatabase();
              
            }
        });


    }

    private void Register() {

        firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
    private void RegisterDatabase() {
        strName=name.getText().toString();
        strEmail= Email.getText().toString();
        strMob=mobNo.getText().toString();
        strPassword= Pass.getText().toString();
        userType=spinner.getText().toString();
        pinCode=etPinCode.getText().toString();
        Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_SHORT).show();

        if (strName.isEmpty() || strMob.isEmpty() || strPassword.isEmpty() || strEmail.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpActivity.this);
            builder1.setMessage("Note : All Entries are compulsory , Please enter all the details.");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else {
            if (strEmail.contains("@") && strEmail.contains(".com")) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (userType.equals("Customer")) {
                    databaseReferenceCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            writeNewUserCustomer(strName, strEmail, strMob, strPassword);
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (userType.equals("Restaurant")){
                   databaseReferenceRestaurant.child(userType).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            writeNewUserRestaurant(strName, strEmail, strMob, strPassword,pinCode);
                            writeNewUser(strName, strEmail, strMob, strPassword);

                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "No value", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(SignUpActivity.this, "Enter Valid E-Mail Id", Toast.LENGTH_SHORT).show();
            }
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void writeNewUser(String name, String email, String mobileno,String password) {
        User user = new User(name, email, mobileno, password);
        databaseReferenceRestaurant.child(mobileno).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();
    }
    public void writeNewUserCustomer(String name, String email, String mobileno,String password) {
        User user = new User(name, email, mobileno, password);
        databaseReferenceCustomer.child(mobileno).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }
    public void writeNewUserRestaurant(String name, String email, String mobileno,String password,String pinCode) {

        User user = new User(name, email, mobileno, password);
        databaseReferencePinCodeRestaurant.child("Delivery").child(pinCode).child(mobileno).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }

    private void initUI()
    {
        //UI reference of textView


        // create list of customer
        ArrayList<String> customerList = getCustomerList();

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_spinner_item, customerList);

        //Set adapter
        spinner.setAdapter(adapter);

        //submit button click event registration
//        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Toast.makeText(SignUpActivity.this, customerAutoTV.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private ArrayList<String> getCustomerList()
    {
        ArrayList<String> users = new ArrayList<>();
        users.add("Customer");
        users.add("Restaurant");

        return users;
    }


}
