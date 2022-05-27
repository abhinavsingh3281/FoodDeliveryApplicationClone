package com.developer.fooddeliveryapp.Delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

public class DeliveryPartnerViewAccountDetails extends AppCompatActivity {

    RoundedImageView profileImage;
    TextView emailTv,mobileNoTv,nameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_partner_view_account_details);

        profileImage=findViewById(R.id.imageProfileAccountDetailsDP);
        emailTv=findViewById(R.id.emailAccountDetailsDP);
        mobileNoTv=findViewById(R.id.mobileNoAccountDetailsDP);
        nameTv=findViewById(R.id.nameAccountDetailsDP);

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