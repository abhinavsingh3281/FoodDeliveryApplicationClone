package com.developer.fooddeliveryapp.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInAndUp.SignUp.SignUpRestaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ViewOrderInDetailedView extends AppCompatActivity {

    TextView yourLocation,restaurantLocation,yourLocationAtRestaurant,customerLocation,distanceFromCurrentToRestaurant;
    FusedLocationProviderClient fusedLocationProviderClient;

    Button acceptRequest;
    String mobileNo,pincode;

    List<Address> addresses=new ArrayList<>();
    ArrayList<DeliveryHomePageModel> listPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_in_detailed_view);

//        yourLocation=findViewById(R.id.yourLocationDelivery);
//        restaurantLocation=findViewById(R.id.deliveryRestaurantLocation);
//        yourLocationAtRestaurant=findViewById(R.id.yourLocationFromRestaurant);
//        customerLocation=findViewById(R.id.customerLocationDelivery);
//        distanceFromCurrentToRestaurant=findViewById(R.id.distanceFromCurrentToRestaurant);

        acceptRequest=findViewById(R.id.btnAcceptDelivery);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        mobileNo=user.get("mobileNo").toString();
        pincode=user.get("pincode").toString();

        listPrivate = new ArrayList<>();

        Type type = new TypeToken<List<DeliveryHomePageModel>>() {
        }.getType();
        listPrivate = new Gson().fromJson(getIntent().getStringExtra("private_list"), type);

        acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child("accepted").child(mobileNo).child(listPrivate.get(0).getOrderId());
                databaseReference.setValue(listPrivate.get(0));


                DatabaseReference db=FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child(pincode).child(listPrivate.get(0).getOrderId());
                db.removeValue();

                Intent intent1=new Intent(getApplicationContext(),TrackDeliveryStatus.class);
                intent1.putExtra("private_list", new Gson().toJson(listPrivate));
                startActivity(intent1);
            }
        });

    }

//    void getCurrentLocation() {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLocation();
//        } else {
//            ActivityCompat.requestPermissions(ViewOrderInDetailedView.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//        }
//    }
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
//                    Geocoder geocoder = new Geocoder(ViewOrderInDetailedView.this, Locale.getDefault());
//
//                    try {
//                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        Toast.makeText(ViewOrderInDetailedView.this, addresses.get(0).getFeatureName()+" ,"+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality() +" ,"+addresses.get(0).getAdminArea()+" ,"+addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
//                        distanceFromCurrentToRestaurant.setText(String.valueOf(distance(addresses.get(0).getLatitude(),Double.parseDouble(listPrivate.get(0).getRestaurantLatitude()),addresses.get(0).getLongitude(),Double.parseDouble(listPrivate.get(0).getRestaurantLongitude()))).substring(0,3)+" Km");
////                        etLongitude.setText(String.valueOf(addresses.get(0).getLongitude()));
////                        etLatitude.setText(String.valueOf(addresses.get(0).getLatitude()));
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//    }

    public static double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}