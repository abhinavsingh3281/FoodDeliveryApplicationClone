package com.developer.fooddeliveryapp.Customer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.fooddeliveryapp.AddressModel;
import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleAdapterListCustomerCart;
import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleAdapterCustomer;
import com.developer.fooddeliveryapp.Customer.HomePageAdapter.ExampleItemCustomer;
import com.developer.fooddeliveryapp.Customer.Payment.FailurePaymentPage;
import com.developer.fooddeliveryapp.Customer.Payment.SuccessfulPaymentPage;
import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.Notification.APIService;
import com.developer.fooddeliveryapp.Notification.Client;
import com.developer.fooddeliveryapp.Notification.Data;
import com.developer.fooddeliveryapp.Notification.MyResponse;
import com.developer.fooddeliveryapp.Notification.NotificationSender;
import com.developer.fooddeliveryapp.Notification.SendNotif;
import com.developer.fooddeliveryapp.R;
import com.developer.fooddeliveryapp.SessionManager;
import com.developer.fooddeliveryapp.SharedPrefList;
import com.developer.fooddeliveryapp.SignInAndUp.SignUp.SignUpMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrder extends AppCompatActivity implements PaymentResultListener {
    private RecyclerView mRecyclerView;
    private ExampleAdapterListCustomerCart mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<ExampleItemCustomerListCart> list = new ArrayList<>();

    String name,email,address;
//    List<ExampleItemCustomerListOrders>listOrders=new ArrayList<>();

    ImageButton buttonBack;
    TextView orderTotal;
    Button payment;

    GifImageView viewOrdersBasket;

    LinearLayout cartLinearLayout;

    private APIService apiService;


    AutoCompleteTextView spinner;
    LinearLayout addressLayout;

    String restaurantName,restaurantMobileNo;
    int total;
    String mobNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        spinner = findViewById(R.id.spinnerAddressCustomer);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        orderTotal = findViewById(R.id.orderTotal);
        payment = findViewById(R.id.payOrder);
        buttonBack = findViewById(R.id.btnBackCart);
        cartLinearLayout=findViewById(R.id.cartValue);
        viewOrdersBasket=findViewById(R.id.viewOrdersBasket);
        addressLayout=findViewById(R.id.addressCustomerLinearLayout);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String userId = user.get("userId").toString();
        String categoryId = user.get("role").toString();
        mobNo = user.get("mobileNo").toString();
        String image = user.get("image").toString();
        email = user.get("email").toString();
        name = user.get("name").toString();


        ArrayList<String>stringAddress=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Address").child(mobNo);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringAddress.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    String p = dataSnapshot1.getValue(String.class);
                    stringAddress.add(p);
                }
                stringAddress.add("Add New Address");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Create adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, stringAddress);

        //Set adapter
        spinner.setAdapter(adapter);

        adapter.notifyDataSetChanged();


        Intent intent=getIntent();
        restaurantName=intent.getStringExtra("restaurantName");
        restaurantMobileNo=intent.getStringExtra("restaurantMob");

        Toast.makeText(this, restaurantMobileNo, Toast.LENGTH_SHORT).show();

        createExampleList();

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int a=arg0.getSelectedItemPosition();
                if (arg0.getItemAtPosition(arg2).equals("Add New Address")) {
                    Toast.makeText(ViewOrder.this, "HELO", Toast.LENGTH_SHORT).show();
                    Intent intent1=new Intent(getApplicationContext(),AddNewAddressCustomer.class);
                    intent1.putExtra("mobileNo",mobNo);
                    intent1.putExtra("restaurantName",restaurantName);
                    intent1.putExtra("restaurantMob",restaurantMobileNo);
                    startActivity(intent1);
                }else
                    Toast.makeText(ViewOrder.this, "BYE", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(getApplicationContext(), mobNo, Toast.LENGTH_SHORT).show();

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepayment();
            }
        });

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

    public void createExampleList() {
        list = SharedPrefList.readListFromPref(this);
        if (list == null)
            list = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerViewCartListItems);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

                if (!list.isEmpty())
                {
                    cartLinearLayout.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    viewOrdersBasket.setVisibility(View.GONE);
                    payment.setVisibility(View.VISIBLE);
                    addressLayout.setVisibility(View.VISIBLE);

                    for (int i=0;i<list.size();i++)
                    {
                        int quantity=Integer.parseInt(list.get(i).getQuantity());
                        int price=Integer.parseInt(list.get(i).getText2());

                        Log.d("TAG", String.valueOf(quantity));
                        total+=price*quantity;

                    }
                    orderTotal.setText(String.valueOf(total));

                }
        mAdapter = new ExampleAdapterListCustomerCart((ArrayList<ExampleItemCustomerListCart>) list);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ExampleAdapterListCustomerCart.OnItemClickListener() {
                    @Override
                    public void addQuantityCart(int position) {
                        ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);
                        int price=Integer.parseInt(list.get(position).getText2());
                        total+=price;
                        orderTotal.setText(String.valueOf(total));
                        int quantity=Integer.parseInt(exampleItemCustomerList.getQuantity());

                        Toast.makeText(getApplicationContext(), String.valueOf(quantity), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void removeQuantityCart(int position) {
                      ExampleItemCustomerListCart exampleItemCustomerList = list.get(position);

                      int price=Integer.parseInt(list.get(position).getText2());
                      total-=price;
                      orderTotal.setText(String.valueOf(total));
                    }

                });
    }

    private void makepayment()
    {
        Intent intent = getIntent();
        String mobNo = intent.getStringExtra("mobileNo");
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_ovElVtiJzGjxYO");

        checkout.setImage(R.drawable.rzp_name_logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Food Delivery ");
            options.put("description", "Reference No. "+ UUID.randomUUID().toString().substring(0,7));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", Integer.parseInt(orderTotal.getText().toString())*100);//300 X 100
            options.put("prefill.email", email);
            options.put("prefill.contact",mobNo);
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPaymentSuccess(String s)
    {
        address=spinner.getText().toString();

        String date= LocalDate.now().toString();
        String orderId=String.valueOf(System.currentTimeMillis());
        OrderModel orderModel=new OrderModel();
        orderModel.setEmail(email);
        orderModel.setAddress(address);
        orderModel.setFood(list);
        orderModel.setMobileNo(mobNo);
        orderModel.setName(name);
        orderModel.setRestaurantName(restaurantName);
        orderModel.setDate(date);
        orderModel.setOrderId(orderId);
        orderModel.setStatus("Request Sent to Restaurant");
        orderModel.setPrice(String.valueOf(total));

        DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(mobNo).child(orderId);
        db.setValue(orderModel);

        DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("users").child("Restaurant").child("Orders").child(restaurantMobileNo).child("pending").child(orderId);
        dbRef.setValue(orderModel);
//        writeOrdersDetails(orderModel);
//        SendNotif sendNotif=new SendNotif();
        sendNotifications("dapL0QBkTzWi13xt67NkKV:APA91bG6wf5GRtHzomRveTfCcLHDMD4LKvezJj_9Pg0N2lsw8auB4UjAvD-al6azjfQGoJEKsdu4fW8iPeQSYnsRYG-eJG8n3GzwcFTtrKCRL5_p0eWK8RR02kQ5gby6SmMm5-h0Gzku","NEW ORDER","You got a new Order");
//        sendNotif.sendNotifications("e-bAVoMUQ8CgMb6fShk8hz:APA91bEWQQLTXrQeJQeHNgrvDC2WyPZg7cD_o73-C5MnWRh44JUNJiGHvXQntY9FaFFEN_fSMlwYNeosBmJ5zFQ5X7os1ibp9d7SE5k1R5cWfWKPMvhApopfVKZLo1KrlznhfoIJuZ1g","Hi "+restaurantName,"You have a new order Request Have a Look");
        SharedPrefList.deleteInPref(getApplicationContext());
        Intent intent=new Intent(getApplicationContext(), SuccessfulPaymentPage.class);
        intent.putExtra("id",s);
        startActivity(intent);
    }

    @Override
    public void onPaymentError(int i, String s) {
        SharedPrefList.deleteInPref(getApplicationContext());
        Intent intent=new Intent(getApplicationContext(), FailurePaymentPage.class);
        intent.putExtra("id",s);
        startActivity(intent);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void writeOrdersDetails(OrderModel orderModel) {
//        DatabaseReference db= FirebaseDatabase.getInstance().getReference("users").child("Customer").child("Orders").child(mobNo).child(String.valueOf(System.currentTimeMillis()));
//        db.setValue(orderModel);
//    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

}