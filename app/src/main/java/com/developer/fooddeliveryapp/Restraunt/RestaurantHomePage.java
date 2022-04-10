package com.developer.fooddeliveryapp.Restraunt;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RestaurantHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<ExampleItem> mExampleList;

    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;

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
    public ActionBarDrawerToggle actionBarDrawerToggle;


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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        createExampleList();
        setButtons();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RestaurantHomePage.this);
                builder1.setMessage("Do you want to Log Out");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LogOut();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
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
}