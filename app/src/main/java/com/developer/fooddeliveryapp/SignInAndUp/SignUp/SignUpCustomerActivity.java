package com.developer.fooddeliveryapp.SignInAndUp.SignUp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.AddressModel;
import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.User;
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
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class SignUpCustomerActivity extends AppCompatActivity {
    TextInputEditText Email, Pass,name,mobNo,confirmPass,etPinCode,etAddress,etGstNo;
    Button LoginB;
    FirebaseAuth firebaseAuth;

    private TextView textAddImage;

    private String encodedImage;
    private RoundedImageView imageProfile;


    private DatabaseReference databaseReferenceCustomer;
    private DatabaseReference databaseReferenceAllUsers;


    private DatabaseReference databaseReferenceRestaurant;
    private DatabaseReference databaseReferencePinCodeRestaurant;
    
    String strName, strMob,strEmail,strPassword,userType,pinCode,address,gstNo;

    FrameLayout frameLayoutImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_customer);

        Email = findViewById(R.id.emailSignUpCustomer);
        Pass = findViewById(R.id.passSignUpCustomer);
        LoginB = findViewById(R.id.SignUp);
        name=findViewById(R.id.nameSignUpCustomer);
        mobNo=findViewById(R.id.mobileNumberSignUpCustomer);
        confirmPass=findViewById(R.id.ConfirmPasswordSignUpCustomer);

        etPinCode=findViewById(R.id.pinCodeSignUpCustomer);
        etAddress=findViewById(R.id.addressSignUpCustomer);

        etGstNo=findViewById(R.id.gstNoSignUpCustomer);

        imageProfile=findViewById(R.id.imageProfile);
        textAddImage=findViewById(R.id.textAddImage);

        frameLayoutImage=findViewById(R.id.layoutImage);



        databaseReferenceCustomer = FirebaseDatabase.getInstance().getReference("users").child("Customer");
        databaseReferenceAllUsers=FirebaseDatabase.getInstance().getReference("users").child("all").child("allUsersInDatabase");


        databaseReferenceRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
        databaseReferencePinCodeRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
        firebaseAuth= FirebaseAuth.getInstance();

        frameLayoutImage.setOnClickListener(v->{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });


        LoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterDatabase();
//                Register();
            }
        });


    }

    private void Register() {
        Intent intent=getIntent();
        userType=intent.getStringExtra("userType");

        strName=name.getText().toString();
        strEmail= Email.getText().toString();
        strMob=mobNo.getText().toString();
        strPassword= Pass.getText().toString();
        userType="Customer";
        pinCode=etPinCode.getText().toString();
        address=etAddress.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.createLoginSession(user.getUid(),userType,strMob,encodedImage,strName,strEmail,address,pinCode);

                            writeNewUserCustomer(strName, strEmail, strMob, strPassword, userType, address, pinCode, encodedImage,user.getUid());
                            writeAllUsers(strName, strEmail, strMob, strPassword, userType, address, pinCode, encodedImage);

                            AddressModel addressModel=new AddressModel();
                            addressModel.setAddress(address);

                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Address").child(strMob).child(UUID.randomUUID().toString());
                            databaseReference.setValue(addressModel.getAddress());

                            if (userType.equals("Customer"))
                            {
                                Intent intent=new Intent(getApplicationContext(), CustomerHomePage.class);
                                startActivity(intent);
                            }

                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpCustomerActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    private String encodeImage(Bitmap bitmap)
    {
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result ->  {
                if (result.getResultCode()==RESULT_OK)
                {
                    if(result.getData()!=null)
                    {
                        Uri imageUri=result.getData().getData();
                        try {
                            InputStream inputStream=getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                            imageProfile.setImageBitmap(bitmap);
                            textAddImage.setVisibility(View.GONE);
                            encodedImage=encodeImage(bitmap);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void RegisterDatabase() {

        strName=name.getText().toString();
        strEmail= Email.getText().toString();
        strMob=mobNo.getText().toString();
        strPassword= Pass.getText().toString();
        userType="Customer";
        pinCode=etPinCode.getText().toString();
        address=etAddress.getText().toString();
        Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_SHORT).show();

        if (strName.isEmpty() || strMob.isEmpty() || strPassword.isEmpty() || strEmail.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpCustomerActivity.this);
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
//                if (userType.equals("Customer")) {

                    databaseReferenceCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Register();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//                } else if (userType.equals("Restaurant")){
//                   databaseReferenceRestaurant.child(userType).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            writeNewUserRestaurant(strName, strEmail, strMob, strPassword,userType,address,pinCode,encodedImage);
//                            writeNewUser(strName, strEmail, strMob, strPassword,userType,address,pinCode,encodedImage);
//                            Register();
////                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "No value", Toast.LENGTH_SHORT).show();
//                }
            }
            else {
                Toast.makeText(SignUpCustomerActivity.this, "Enter Valid E-Mail Id", Toast.LENGTH_SHORT).show();
            }
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void writeNewUser(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image) {
        User user = new User(name, email, mobileNo, password,role,address,pinCode,image);
        databaseReferenceRestaurant.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();
    }


    /////////////////////////////////////////////////////---------------------------------


    public void writeNewUserCustomer(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image,String uid) {
        User user = new User(name, email, mobileNo, password,role,address,pinCode,image,uid);
        databaseReferenceCustomer.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }
    public void writeAllUsers(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image) {
        User user = new User(name, email, mobileNo, password,role,address,pinCode,image);
        databaseReferenceAllUsers.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }

    private boolean getData(String mobileNo) {
        final boolean[] b = new boolean[1];
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child("all").child("allUsersInDatabase");
        ref.orderByChild("mobileNo").equalTo(strMob).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                b[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return b[0];
    }


    public void writeNewUserRestaurant(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image) {
        User user = new User(name, email, mobileNo, password,role,address,pinCode,image);
        databaseReferencePinCodeRestaurant.child("Delivery").child(pinCode).child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }


}
