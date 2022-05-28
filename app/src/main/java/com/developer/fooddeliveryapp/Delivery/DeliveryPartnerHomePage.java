package com.developer.fooddeliveryapp.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageAdapter;
import com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap.DeliveryHomePageModel;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInAndUp.SignInActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DeliveryPartnerHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private DeliveryHomePageAdapter mAdapter;

    TextView noDeliveryPartnerText;

    ArrayList<DeliveryHomePageModel> list = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;

    String pincode;
    DrawerLayout drawerLayout;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_partner_home_page);
        noDeliveryPartnerText=(TextView) findViewById(R.id.noRequestDeliveryPartnerText);

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        String mobileNo = user.get("mobileNo").toString();
        String image = user.get("image").toString();
        String email = user.get("email").toString();
        String name = user.get("name").toString();
        pincode = user.get("pincode").toString();

        createExampleList(pincode);
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        getCurrentLocation();


        Toolbar toolbar = findViewById(R.id.toolbar_delivery);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_delivery);
        NavigationView navigationView = findViewById(R.id.nav_view_delivery);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_name);

        RoundedImageView imageView = (RoundedImageView) headerView.findViewById(R.id.header_image);
        imageView.setImageBitmap(bitmap);
        navUsername.setText(name);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        swipeRefreshLayout=findViewById(R.id.swipeContainerDeliveryPartner);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList(pincode);
                getLocation();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                new FancyGifDialog.Builder(this)
                        .setTitle("Are you sure you want to LogOut?") // You can also send title like R.string.from_resources
                        .setMessage("By Pressing LogOut you will not be able to access our services.") // or pass like R.string.description_from_resources
                        .setTitleTextColor(R.color.titleText)
                        .setDescriptionTextColor(R.color.descriptionText)
                        .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                        .setPositiveBtnBackground(R.color.positiveButton)
                        .setPositiveBtnText("LogOut") // or pass it like android.R.string.ok
                        .setNegativeBtnBackground(R.color.negativeButton)
                        .setGifResource(R.drawable.giflogoutmessage)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                LogOut();
                                Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
                break;
            case R.id.nav_pending_orders:
                startActivity(new Intent(getApplicationContext(), ViewAllPendingRequest.class));
                break;
            case R.id.nav_account:
                startActivity(new Intent(getApplicationContext(), DeliveryPartnerViewAccountDetails.class));
                break;

            case R.id.nav_orders:
                startActivity(new Intent(getApplicationContext(), DeliveryPartnerMyOrderPage.class));
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
    }

    public void createExampleList(String pincode) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("DeliveryPartner").child("orders").child(pincode);

        mRecyclerView = findViewById(R.id.recyclerViewDeliveryPartner);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

//        if(list.isEmpty())
//        {
//            mRecyclerView.setVisibility(View.VISIBLE);
//            noDeliveryPartnerText.setVisibility(View.GONE);
//        }
//        else {
//            mRecyclerView.setVisibility(View.GONE);
//            noDeliveryPartnerText.setVisibility(View.VISIBLE);
//        }


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    DeliveryHomePageModel p = dataSnapshot1.getValue(DeliveryHomePageModel.class);
                    list.add(p);
                }
                mAdapter = new DeliveryHomePageAdapter(list);

                if (!list.isEmpty()){
                    mRecyclerView.setVisibility(View.VISIBLE);
                    noDeliveryPartnerText.setVisibility(View.GONE);
                }


                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new DeliveryHomePageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        DeliveryHomePageModel exampleItemCustomer=list.get(position);

                        ArrayList<DeliveryHomePageModel> arrayList= new ArrayList<>();
                        arrayList.add(exampleItemCustomer);

                        Intent intent1=new Intent(getApplicationContext(),ViewOrderInDetailedView.class);

                        intent1.putExtra("private_list", new Gson().toJson(arrayList));
                        startActivity(intent1);

//                        Toast.makeText(DeliveryPartnerHomePage.this, String.valueOf(arrayList.get(0).getRestaurantLatitude()), Toast.LENGTH_SHORT).show();
//                        String uri = "https://maps.google.com?q=@"+arrayList.get(0).getRestaurantLatitude()+","+arrayList.get(0).getRestaurantLongitude();
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    void getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(DeliveryPartnerHomePage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
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
                    Geocoder geocoder = new Geocoder(DeliveryPartnerHomePage.this, Locale.getDefault());

                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Toast.makeText(DeliveryPartnerHomePage.this, String.valueOf(distance(29.1661401,addresses.get(0).getLatitude(),75.71953299999996,addresses.get(0).getLongitude())), Toast.LENGTH_SHORT).show();
                        Toast.makeText(DeliveryPartnerHomePage.this, addresses.get(0).getFeatureName()+" ,"+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getLocality() +" ,"+addresses.get(0).getAdminArea()+" ,"+addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}