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

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Notification.Token;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.Restaurant;
import com.developer.fooddeliveryapp.Restraunt.RestaurantHomePage;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignUpRestaurant extends AppCompatActivity {
    TextInputEditText Email, Pass,name,mobNo,confirmPass,etPinCode,etAddress,etGstNo,etRestaurantName;
    Button LoginB;
    FirebaseAuth firebaseAuth;

    private TextView textAddImage;

    private String encodedImage;
    private RoundedImageView imageProfile;

    private DatabaseReference databaseReferenceAllUsers;


    private DatabaseReference databaseReferenceRestaurant;
    private DatabaseReference databaseReferencePinCodeRestaurant;

    String strName, strMob,strEmail,strPassword,userType,pinCode,address,restaurantName,gstNo;

    FrameLayout frameLayoutImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_restaurant);

        Email = findViewById(R.id.emailSignUpRestaurant);
        Pass = findViewById(R.id.passSignUpRestaurant);
        LoginB = findViewById(R.id.SignUpRestaurant);
        name=findViewById(R.id.nameSignUpRestaurant);
        mobNo=findViewById(R.id.mobileNumberSignUpRestaurant);
        confirmPass=findViewById(R.id.ConfirmPasswordSignUpRestaurant);

        etPinCode=findViewById(R.id.pinCodeSignUpRestaurant);
        etAddress=findViewById(R.id.addressSignUpRestaurant);

        etGstNo=findViewById(R.id.gstNoSignUpRestaurant);
        etRestaurantName=findViewById(R.id.restaurantNameSignUpRestaurant);

        imageProfile=findViewById(R.id.imageProfileRestaurant);
        textAddImage=findViewById(R.id.textAddImageRestaurant);

        frameLayoutImage=findViewById(R.id.layoutImageSignUpRestaurant);



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
            }
        });


    }
    private void SetToken(String uid) {
        final String[] refreshToken = {null};
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {
                    refreshToken[0] = task.getResult();
                    Toast.makeText(getApplicationContext(), "Token" + refreshToken[0], Toast.LENGTH_LONG).show();
                    Token token = new Token(refreshToken[0]);
                    FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid).setValue(token);
                    Log.e("AppConstants", "onComplete: new Token got: " + refreshToken[0]);
                }
            }
        });
    }

    private void Register() {
        strName=name.getText().toString();
        strEmail= Email.getText().toString();
        strMob=mobNo.getText().toString();
        strPassword= Pass.getText().toString();
        restaurantName=etRestaurantName.getText().toString();
        gstNo=etGstNo.getText().toString();
        userType="Restaurant";
        pinCode=etPinCode.getText().toString();
        address=etAddress.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            writeNewUser(strName, strEmail, strMob, strPassword, userType, address, pinCode, encodedImage,restaurantName,gstNo,user.getUid());
                            writeNewUserRestaurant(strName, strEmail, strMob, strPassword, userType, address, pinCode, encodedImage,restaurantName,gstNo,user.getUid());

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.createLoginSession(user.getUid(),userType,strMob,encodedImage,strName,strEmail,address,pinCode,restaurantName);

                            SetToken(user.getUid());

                            Intent intent=new Intent(getApplicationContext(), RestaurantHomePage.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpRestaurant.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

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
        userType="Restaurant";
        restaurantName=etRestaurantName.getText().toString();
        gstNo=etGstNo.getText().toString();
        pinCode=etPinCode.getText().toString();
        address=etAddress.getText().toString();
        Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_SHORT).show();

        if (strName.isEmpty() || strMob.isEmpty() || strPassword.isEmpty() || strEmail.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SignUpRestaurant.this);
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
                databaseReferenceRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        writeAllUsers(strName, strEmail, strMob, strPassword, userType, address, pinCode, encodedImage);
                        Register();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                Toast.makeText(SignUpRestaurant.this, "Enter Valid E-Mail Id", Toast.LENGTH_SHORT).show();
            }
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void writeNewUser(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image,String restaurantName,String gstNo,String uid) {
        Restaurant user = new Restaurant(image,name, email, mobileNo, password,role,restaurantName,address,pinCode,gstNo,uid);
        databaseReferenceRestaurant.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();
    }

    public void writeAllUsers(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image) {
        User user = new User(name, email, mobileNo, password,role,address,pinCode,image);
        databaseReferenceAllUsers.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }

    public void writeNewUserRestaurant(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image,String restaurantName,String gstNo,String uid) {
        Restaurant user = new Restaurant(image,name, email, mobileNo, password,role,restaurantName,address,pinCode,gstNo,uid);
        databaseReferencePinCodeRestaurant.child("Delivery").child(pinCode).child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }


}
