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
import com.developer.fooddeliveryapp.DeliveryPartnerModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
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

public class SignUpDeliveryBoy extends AppCompatActivity {

    TextInputEditText Email, Pass,name,mobNo,confirmPass,etPinCode,etAddress,etGstNo;
    Button LoginB;
    FirebaseAuth firebaseAuth;

    private TextView textAddImage;

    private String encodedImage;
    private RoundedImageView imageProfile;


    private DatabaseReference databaseReferenceDeliveryPartnerWithPinCode,databaseReferenceDeliveryPartner;
    private DatabaseReference databaseReferenceAllUsers;


//    private DatabaseReference databaseReferenceRestaurant;
//    private DatabaseReference databaseReferencePinCodeRestaurant;

    String strName, strMob,strEmail,strPassword,userType,pinCode;

    FrameLayout frameLayoutImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_delivery_boy);


        Email = findViewById(R.id.emailSignUpDelivery);
        Pass = findViewById(R.id.passSignUpDelivery);
        LoginB = findViewById(R.id.SignUpDelivery);
        name=findViewById(R.id.nameSignUpDelivery);
        mobNo=findViewById(R.id.mobileNumberSignUpDelivery);
        confirmPass=findViewById(R.id.ConfirmPasswordSignUpDelivery);

        etPinCode=findViewById(R.id.pinCodeSignUpDelivery);

        imageProfile=findViewById(R.id.imageProfileDelivery);
        textAddImage=findViewById(R.id.textAddImageDelivery);

        frameLayoutImage=findViewById(R.id.layoutImageDelivery);



        databaseReferenceDeliveryPartnerWithPinCode = FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("pincode");
        databaseReferenceDeliveryPartner = FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("all");
        databaseReferenceAllUsers=FirebaseDatabase.getInstance().getReference("users").child("all").child("allUsersInDatabase");

//
//        databaseReferenceRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
//        databaseReferencePinCodeRestaurant = FirebaseDatabase.getInstance().getReference("users").child("Restaurant");
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
        strName=name.getText().toString();
        strEmail= Email.getText().toString();
        strMob=mobNo.getText().toString();
        strPassword= Pass.getText().toString();
        userType="DeliveryPartner";
        pinCode=etPinCode.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            SessionManager session = new SessionManager(getApplicationContext());
                            session.createLoginSession(user.getUid(),userType,strMob,encodedImage,strName,strEmail,pinCode);

                            writeNewUserDeliveryWithPinCode(strName, strEmail, strMob, strPassword, userType, pinCode, encodedImage,user.getUid());
                            writeNewUserDelivery(strName, strEmail, strMob, strPassword, userType, pinCode, encodedImage,user.getUid());
                            writeAllUsersDelivery(strName, strEmail, strMob, strPassword, userType, pinCode, encodedImage);


                            if (userType.equals("DeliveryPartner"))
                            {
                                Intent intent=new Intent(getApplicationContext(), CustomerHomePage.class);
                                startActivity(intent);
                            }

                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();

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
        userType="DeliveryPartner";
        pinCode=etPinCode.getText().toString();

        Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_SHORT).show();

        if (strName.isEmpty() || strMob.isEmpty() || strPassword.isEmpty() || strEmail.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
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
                databaseReferenceDeliveryPartnerWithPinCode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Register();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            else {
                Toast.makeText(getApplicationContext(), "Enter Valid E-Mail Id", Toast.LENGTH_SHORT).show();
            }
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void writeNewUserDeliveryWithPinCode(String name, String email, String mobileNo, String password, String role, String pinCode,String image,String uid) {
        DeliveryPartnerModel user = new DeliveryPartnerModel(name, email, mobileNo, password,role,pinCode,image,uid);
        databaseReferenceDeliveryPartnerWithPinCode.child(pinCode).child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }
    public void writeNewUserDelivery(String name, String email, String mobileNo, String password, String role, String pinCode,String image,String uid) {
        DeliveryPartnerModel user = new DeliveryPartnerModel(name, email, mobileNo, password,role,pinCode,image,uid);
        databaseReferenceDeliveryPartner.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }
    public void writeAllUsersDelivery(String name, String email, String mobileNo, String password, String role, String pinCode,String image) {
        DeliveryPartnerModel user = new DeliveryPartnerModel(name, email, mobileNo, password,role,pinCode,image);
        databaseReferenceAllUsers.child(mobileNo).setValue(user);
        Toast.makeText(getApplicationContext(), "Data Send Successfully", Toast.LENGTH_SHORT).show();

    }




}
