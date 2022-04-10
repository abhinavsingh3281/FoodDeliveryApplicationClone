package com.developer.fooddeliveryapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPrefer;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context context;

    // Shared Pref mode
    int PRIVATE_MODE = 0;

    // Shared Pref file name
    private static final String PREF_NAME = "MySession";

    // SHARED PREF KEYS FOR ALL DATA

    // User's UserId
    public static final String KEY_USERID = "userId";

    // User's categoryId
    public static final String KEY_ROLE = "role";

    public static final String KEY_MOBILE="mobileNo";

    public static final String KEY_IMAGE="image";

    public static final String KEY_NAME="name";

    public static final String KEY_ADDRESS="address";

    public static final String KEY_PINCODE="pincode";

    public static final String KEY_EMAIL="email";

    public static final String KEY_RESTAURANT_NAME="restaurantName";



    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        sharedPrefer = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPrefer.edit();
    }

    /**
     * Call this method on/after login to store the details in session
     * */

    public void createLoginSession(String userId, String role,String mobileNo) {

        // Storing userId in pref
        editor.putString(KEY_USERID, userId);

        // Storing catId in pref
        editor.putString(KEY_ROLE, role);

        editor.putString(KEY_MOBILE,mobileNo);

        // commit changes
        editor.commit();
    }
    public void createLoginSession(String userId, String role,String mobileNo,String image,String name,String email,String address,String pinCode) {

        // Storing userId in pref
        editor.putString(KEY_USERID, userId);

        // Storing catId in pref
        editor.putString(KEY_ROLE, role);

        editor.putString(KEY_MOBILE,mobileNo);

        editor.putString(KEY_IMAGE,image);

        editor.putString(KEY_NAME, name);

        editor.putString(KEY_EMAIL,email);

        editor.putString(KEY_ADDRESS,address);

        editor.putString(KEY_PINCODE,pinCode);

        // commit changes
        editor.commit();
    }

    public void createLoginSession(String userId, String role,String mobileNo,String image,String name,String email,String address,String pinCode,String restaurantName) {

        // Storing userId in pref
        editor.putString(KEY_USERID, userId);

        // Storing catId in pref
        editor.putString(KEY_ROLE, role);

        editor.putString(KEY_MOBILE,mobileNo);

        editor.putString(KEY_IMAGE,image);

        editor.putString(KEY_NAME, name);

        editor.putString(KEY_EMAIL,email);

        editor.putString(KEY_ADDRESS,address);

        editor.putString(KEY_PINCODE,pinCode);

        editor.putString(KEY_RESTAURANT_NAME,restaurantName);

        // commit changes
        editor.commit();
    }
    /**
     * Call this method anywhere in the project to Get the stored session data
     * */
    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<String, String>();
        user.put("userId",sharedPrefer.getString(KEY_USERID, null));
        user.put("role",sharedPrefer.getString(KEY_ROLE, null));
        user.put("mobileNo",sharedPrefer.getString(KEY_MOBILE,null));
        user.put("image",sharedPrefer.getString(KEY_IMAGE,null));

        user.put("email",sharedPrefer.getString(KEY_EMAIL,null));
        user.put("name",sharedPrefer.getString(KEY_NAME,null));
        user.put("address",sharedPrefer.getString(KEY_ADDRESS,null));
        user.put("pincode",sharedPrefer.getString(KEY_PINCODE,null));
        user.put("restaurantName",sharedPrefer.getString(KEY_PINCODE,null));

        return user;
    }
}