package com.developer.fooddeliveryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.ItemsInRestaurantAdapter.ExampleAdapterListCustomer;
import com.developer.fooddeliveryapp.Customer.ItemsInRestaurantAdapter.ExampleItemCustomerList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefList {
    public static void writeListInPref(Context context, List<ExampleItemCustomerListCart> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CART", jsonString);
        editor.apply();
    }

    public static List<ExampleItemCustomerListCart> readListFromPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = pref.getString("CART", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ExampleItemCustomerListCart>>() {}.getType();
        List<ExampleItemCustomerListCart> list = gson.fromJson(jsonString, type);
        return list;
    }

    public static void deleteInPref(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }
}
