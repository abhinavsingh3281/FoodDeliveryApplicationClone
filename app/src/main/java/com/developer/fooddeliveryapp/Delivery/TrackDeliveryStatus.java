package com.developer.fooddeliveryapp.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Customer.CustomerHomePage;
import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageModel;
import com.developer.fooddeliveryapp.Notification.APIService;
import com.developer.fooddeliveryapp.Notification.Client;
import com.developer.fooddeliveryapp.Notification.Data;
import com.developer.fooddeliveryapp.Notification.MyResponse;
import com.developer.fooddeliveryapp.Notification.NotificationSender;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackDeliveryStatus extends AppCompatActivity {
    TextView yourLocation,restaurantLocation,yourLocationAtRestaurant,customerLocation,distanceFromCurrentToRestaurant,addressCustomerInDeliverySection,restaurantNameInDeliverySection,distanceFromRestaurantToCustomer;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<DeliveryHomePageModel>listPrivate;
    Button btnNavigateDelivery,btnNavigateDeliveryToCustomer,btnArrivedAtRestaurantDeliveryPage,btnDeliveredOrderDeliveryPage;
    List<Address>addresses;
    SwipeRefreshLayout swipeRefreshLayout;
    String mobileNo;

    private APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_delivery_status);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        mobileNo=user.get("mobileNo").toString();

        yourLocation=findViewById(R.id.yourLocationDelivery);
        restaurantLocation=findViewById(R.id.deliveryRestaurantLocation);
        yourLocationAtRestaurant=findViewById(R.id.yourLocationFromRestaurant);
        customerLocation=findViewById(R.id.customerLocationDelivery);
        distanceFromCurrentToRestaurant=findViewById(R.id.distanceFromCurrentToRestaurant);
        addressCustomerInDeliverySection=findViewById(R.id.addressCustomerInDeliverySection);
        restaurantNameInDeliverySection=findViewById(R.id.restaurantNameInDeliverySection);
        distanceFromRestaurantToCustomer=findViewById(R.id.distanceFromRestaurantToCustomer);


        btnNavigateDelivery=findViewById(R.id.btnNavigateDelivery);
        btnNavigateDeliveryToCustomer=findViewById(R.id.btnNavigateDeliveryToCustomer);
        btnArrivedAtRestaurantDeliveryPage=findViewById(R.id.btnArrivedAtRestaurantDeliveryPage);
        btnDeliveredOrderDeliveryPage=findViewById(R.id.btnDeliveredOrderDeliveryPage);

        swipeRefreshLayout=findViewById(R.id.swipeContainerViewMyDetailedOrdersDelivery);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getCurrentLocation();
            }
        });

        listPrivate = new ArrayList<>();

        Type type = new TypeToken<List<DeliveryHomePageModel>>() {
        }.getType();
        listPrivate = new Gson().fromJson(getIntent().getStringExtra("private_list"), type);

        restaurantLocation.setText(listPrivate.get(0).getRestaurantName() +"\n"+listPrivate.get(0).getRestaurantAddress());
        customerLocation.setText(listPrivate.get(0).getUserAddress());
        addressCustomerInDeliverySection.setText(listPrivate.get(0).getName() +"\n"+listPrivate.get(0).getUserAddress());
        restaurantNameInDeliverySection.setText(listPrivate.get(0).getRestaurantName() +"\n"+listPrivate.get(0).getRestaurantAddress());
        yourLocationAtRestaurant.setText(listPrivate.get(0).getRestaurantName() +"\n"+listPrivate.get(0).getRestaurantAddress());

        btnNavigateDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://maps.google.com?q=@"+listPrivate.get(0).getRestaurantLatitude()+","+listPrivate.get(0).getRestaurantLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        btnNavigateDeliveryToCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://maps.google.com?q=@"+listPrivate.get(0).getCustomerLatitude()+","+listPrivate.get(0).getCustomerLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        btnArrivedAtRestaurantDeliveryPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyGifDialog.Builder(TrackDeliveryStatus.this)
                        .setTitle("Have you arrived at Restaurant?") // You can also send title like R.string.from_resources
                        .setMessage("By Pressing Ok customer will be notified") // or pass like R.string.description_from_resources
                        .setTitleTextColor(R.color.titleText)
                        .setDescriptionTextColor(R.color.descriptionText)
                        .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                        .setPositiveBtnBackground(R.color.positiveButton)
                        .setPositiveBtnText("Okay") // or pass it like android.R.string.ok
                        .setNegativeBtnBackground(R.color.negativeButton)
                        .setGifResource(R.drawable.giflogoutmessage)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                arrivedAtRestaurant();
                            }
                        })
                        .build();
            }
        });

        btnDeliveredOrderDeliveryPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyGifDialog.Builder(TrackDeliveryStatus.this)
                        .setTitle("Have you delivered the Order?") // You can also send title like R.string.from_resources
                        .setMessage("By Pressing Ok customer will be notified") // or pass like R.string.description_from_resources
                        .setTitleTextColor(R.color.titleText)
                        .setDescriptionTextColor(R.color.descriptionText)
                        .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                        .setPositiveBtnBackground(R.color.positiveButton)
                        .setPositiveBtnText("Okay") // or pass it like android.R.string.ok
                        .setNegativeBtnBackground(R.color.negativeButton)
                        .setGifResource(R.drawable.giflogoutmessage)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                orderDelivered();
                                startActivity(new Intent(getApplicationContext(),DeliveryPartnerHomePage.class));
                            }
                        })
                        .build();
            }
        });
        getCurrentLocation();


    }

    private void arrivedAtRestaurant()
    {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child(listPrivate.get(0).getCustomerMobileNo());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=snapshot.child("uid").getValue(String.class);

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Tokens").child(uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String token=snapshot.child("token").getValue(String.class);
                        sendNotifications(token,"Your Order is on the way","It will arrive soon");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(listPrivate.get(0).getCustomerMobileNo()).child(listPrivate.get(0).getOrderId()).child("status");
        databaseReference.setValue("Order on the Way");
    }

    private void orderDelivered()
    {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child(listPrivate.get(0).getCustomerMobileNo());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uid=snapshot.child("uid").getValue(String.class);

                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Tokens").child(uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String token=snapshot.child("token").getValue(String.class);
                        sendNotifications(token,"Your Order is Delivered","Thank you for using our services");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(listPrivate.get(0).getCustomerMobileNo()).child(listPrivate.get(0).getOrderId()).child("status");
        databaseReference.setValue("Delivered");

        DatabaseReference databaseReference1= FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child("accepted").child(mobileNo);
        databaseReference1.removeValue();

        DatabaseReference databaseReference2= FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child("completed").child(mobileNo).child(listPrivate.get(0).getOrderId());
        databaseReference2.setValue(listPrivate.get(0));

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),ViewAllPendingRequest.class));
    }

    void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(TrackDeliveryStatus.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(TrackDeliveryStatus.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Toast.makeText(TrackDeliveryStatus.this, addresses.get(0).getFeatureName()+" ,"+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality() +" ,"+addresses.get(0).getAdminArea()+" ,"+addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
                        yourLocation.setText("Your Location"+"\n"+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality());
                        distanceFromCurrentToRestaurant.setText(String.valueOf(distance(addresses.get(0).getLatitude(),Double.parseDouble(listPrivate.get(0).getRestaurantLatitude()),addresses.get(0).getLongitude(),Double.parseDouble(listPrivate.get(0).getRestaurantLongitude()))).substring(0,3)+" Km");
                        distanceFromRestaurantToCustomer.setText(String.valueOf(distance(addresses.get(0).getLatitude(),Double.parseDouble(listPrivate.get(0).getCustomerLatitude()),addresses.get(0).getLongitude(),Double.parseDouble(listPrivate.get(0).getCustomerLongitude()))).substring(0,3)+" Km");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

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

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }


}