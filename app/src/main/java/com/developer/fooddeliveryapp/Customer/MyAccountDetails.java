package com.developer.fooddeliveryapp.Customer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

public class MyAccountDetails extends AppCompatActivity {

    RoundedImageView profileImage;
    TextView emailTv,mobileNoTv,nameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_details);

        profileImage=findViewById(R.id.imageProfileAccountDetails);
        emailTv=findViewById(R.id.emailAccountDetails);
        mobileNoTv=findViewById(R.id.mobileNoAccountDetails);
        nameTv=findViewById(R.id.nameAccountDetails);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        String mobileNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();
        String pincode=user.get("pincode").toString();

        byte [] bytes= Base64.decode(image,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);

        profileImage.setImageBitmap(bitmap);
        nameTv.setText(name);
        emailTv.setText(email);
        mobileNoTv.setText(mobileNo);

    }
}