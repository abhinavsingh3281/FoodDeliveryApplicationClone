package com.developer.fooddeliveryapp.Restaurant;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SignInAndUp.SignInActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageButton buttonInsert;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference db;
    String mobNo;

    DatabaseReference deleteDatabaseReference;

    ArrayList<ExampleItem>list=new ArrayList<>();

    RoundedImageView imageView;
    TextView textAddImage;
    private String encodedImage;

    DatabaseReference reference;
    DrawerLayout drawerLayout;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restraunt_home_page);
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobNo=user.get("mobileNo").toString();
        String image=user.get("image").toString();
        String email=user.get("email").toString();
        String name=user.get("name").toString();

        byte [] bytes= Base64.decode(image,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.header_name);
        RoundedImageView imageView=(RoundedImageView) headerView.findViewById(R.id.header_image);
        imageView.setImageBitmap(bitmap);
        navUsername.setText(name);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        swipeRefreshLayout=findViewById(R.id.swipeContainerRestaurantHomePage);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                createExampleList();
            }
        });
        createExampleList();
        setButtons();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                new FancyGifDialog.Builder(this)
                        .setTitle("Do you want to Sign-Out?") // You can also send title like R.string.from_resources
                        .setMessage("By Pressing SIGN-OUT you will not be able to use our services.") // or pass like R.string.description_from_resources
                        .setTitleTextColor(R.color.titleText)
                        .setDescriptionTextColor(R.color.descriptionText)
                        .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                        .setPositiveBtnBackground(R.color.positiveButton)
                        .setPositiveBtnText("SIGN OUT") // or pass it like android.R.string.ok
                        .setNegativeBtnBackground(R.color.negativeButton)
                        .setGifResource(R.drawable.giflogoutmessage)   //Pass your Gif here
                        .isCancellable(true)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                LogOut();
                                Toast.makeText(getApplicationContext(),"Ok",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
                break;
            case R.id.nav_orders:
                startActivity(new Intent(getApplicationContext(), ViewAllMyOrdersRestaurant.class));
                Toast.makeText(this, "TAP", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.nav_pending_orders:
                Toast.makeText(this, "TAP PENDING", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ViewAllPendingRequestRestaurant.class));
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }

    public void removeItem(int position) {
        firebaseDatabase = FirebaseDatabase.getInstance();

        String s=list.get(position).getText1();

        deleteDatabaseReference = firebaseDatabase.getReference("users").child("Restaurant").child(mobNo).child("items").child(s);

        deleteDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();

                }
                list.remove(position);
                startActivity(new Intent(getApplicationContext(), RestaurantHomePage.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });



    }

    public void createExampleList() {
//        Intent intent=getIntent();
//        String mob=intent.getStringExtra("mobileNo");

        db=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mobNo).child("items");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot1: snapshot.getChildren())
                {
                    ExampleItem p = dataSnapshot1.getValue(ExampleItem.class);
                    list.add(p);
                }
                mAdapter = new ExampleAdapter(list);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
//                        changeItem(position, "Clicked");
                    }
                    @Override
                    public void onDeleteClick(int position) {
                        removeItem(position);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Toast.makeText(getApplicationContext(), mob, Toast.LENGTH_SHORT).show();

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


    public void setButtons() {
        buttonInsert = findViewById(R.id.button_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(v);
            }
        });

    }
    public void onButtonShowPopupWindowClick(View view) {
        firebaseDatabase = FirebaseDatabase.getInstance();

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched

        TextInputEditText itemName=popupView.findViewById(R.id.setItemName);
        imageView=popupView.findViewById(R.id.imageFoodRestaurant);
        textAddImage=popupView.findViewById(R.id.textAddImage);
        FrameLayout frameLayoutImage=popupView.findViewById(R.id.layoutImageRestaurant);

        frameLayoutImage.setOnClickListener(v->{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        TextInputEditText itemPrice=popupView.findViewById(R.id.setItemPrice);

        Button add=popupView.findViewById(R.id.saveItems);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference=FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child(mobNo);

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                BigInteger bigIntegerStr=new BigInteger(encodedImage);
                               writeItemDetails(encodedImage,itemName.getText().toString(),itemPrice.getText().toString());

                                startActivity(new Intent(getApplicationContext(),RestaurantHomePage.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        popupWindow.dismiss();
                    }
                });

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
                            imageView.setImageBitmap(bitmap);
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
    public void writeItemDetails(String image,String itemName,String itemPrice) {

        ExampleItem item = new ExampleItem(image,itemName, itemPrice);
        reference.child("items").child(itemName).setValue(item);
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}