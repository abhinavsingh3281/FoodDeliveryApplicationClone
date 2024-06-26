package com.developer.fooddeliveryapp.SignInAndUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Delivery.DeliveryPartnerHomePage;
import com.developer.fooddeliveryapp.Notification.Token;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restaurant.RestaurantHomePage;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInAndUp.SignUp.SignUpMainActivity;
import com.developer.fooddeliveryapp.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText mobileNo, PassTB;
    Button LoginB;
    Button signup;

    FusedLocationProviderClient fusedLocationProviderClient;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    DatabaseReference reference;
    AutoCompleteTextView spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mobileNo = findViewById(R.id.mobileSignIn);
        PassTB = findViewById(R.id.passwordSignIn);
        signup=findViewById(R.id.btnSignUpCustomer);

        getCurrentLocation();

//        spinner = findViewById(R.id.spinnerTypeSignIn);

//        initUI();

        reference=FirebaseDatabase.getInstance().getReference("users").child("all");

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignInActivity.this, SignUpMainActivity.class));
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
                else if(categoryId.equals("Restaurant")){
                    Intent intent =new Intent(getApplicationContext(),RestaurantHomePage.class);
                    intent.putExtra("mobileNo",mobileNo);
                    startActivity(intent);
                }
                else {
                    Intent intent =new Intent(getApplicationContext(), DeliveryPartnerHomePage.class);
                    intent.putExtra("mobileNo",mobileNo);
                    startActivity(intent);
                }
            }

            else {
                LoginB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getData(mobileNo.getText().toString());
//                        SetToken();
                    }
                });
            }

        }

    private void SetToken(String uid){
        final String[] refreshToken = {null};
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    refreshToken[0] = task.getResult();
                    Toast.makeText(getApplicationContext(),"Token"+ refreshToken[0], Toast.LENGTH_LONG).show();
                    Token token=new Token(refreshToken[0]);
                    FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid).setValue(token);
                    Log.e("AppConstants", "onComplete: new Token got: "+refreshToken[0] );
                }
            }
        });
//        Toast.makeText(getApplicationContext(),"Token"+ refreshToken[0], Toast.LENGTH_LONG).show();
    }
    private void getData(String mobileNo) {
//        String s=spinner.getText().toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child("all").child("allUsersInDatabase").child(mobileNo);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String email = snapshot.child("email").getValue(String.class);
                String password = snapshot.child("password").getValue(String.class);
                String name=snapshot.child("name").getValue(String.class);
                String image=snapshot.child("image").getValue(String.class);
                String address=snapshot.child("address").getValue(String.class);
                String pincode=snapshot.child("pinCode").getValue(String.class);
                String role=snapshot.child("role").getValue(String.class);

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        writeNewUserWithUid(name,email,mobileNo,password,FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),role);
                        SetToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        SessionManager session = new SessionManager(getApplicationContext());
                        assert role != null;
                        if (role.equals("Customer"))
                        {
                            Intent intent=new Intent(getApplicationContext(), CustomerHomePage.class);
                            intent.putExtra("mobileNo",mobileNo);
                            session.createLoginSession(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),role,mobileNo,image,name,email,address,pincode);
                            intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
//                            SetToken();
                            startActivity(intent);
                        }
                        else if (role.equals("Restaurant")){
                            Intent intent=new Intent(getApplicationContext(),RestaurantHomePage.class);
                            String restaurantName=snapshot.child("restaurantName").getValue(String.class);
                            intent.putExtra("mobileNo",mobileNo);
                            session.createLoginSession(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),role,mobileNo,image,name,email,address,pincode,restaurantName);
                            intent.putExtra("uid",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
//                            SetToken();
                            startActivity(intent);
                        }
                        else {
                            Intent intent=new Intent(getApplicationContext(),DeliveryPartnerHomePage.class);
                            intent.putExtra("mobileNo",mobileNo);
                            session.createLoginSession(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),role,mobileNo,image,name,email,pincode);
//                            SetToken();
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
//    private void initUI() {
//
//        ArrayList<String> customerList = getCustomerList();
//
//        //Create adapter
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(SignInActivity.this, android.R.layout.simple_spinner_dropdown_item, customerList);
//
//        //Set adapter
//        spinner.setAdapter(adapter);
//    }
    

    private ArrayList<String> getCustomerList()
    {
        ArrayList<String> users = new ArrayList<>();
        users.add("Customer");
        users.add("Restaurant");

        return users;
    }


    void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLocation();
        } else {
            ActivityCompat.requestPermissions(SignInActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

//    private void getLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                Location location = task.getResult();
//                if (location != null) {
//                    Geocoder geocoder = new Geocoder(SignInActivity.this, Locale.getDefault());
//
//                    try {
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
////                        Toast.makeText(SignInActivity.this, addresses.get(0).getFeatureName()+" ,"+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality() +" ,"+addresses.get(0).getAdminArea()+" ,"+addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }

}