package com.developer.fooddeliveryapp.SignInAndUp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.developer.fooddeliveryapp.R;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends Activity {

    GifImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        imageView = findViewById(R.id.img);

        imageView.setImageResource(R.drawable.gifstart);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        }, 4000);
    }
}