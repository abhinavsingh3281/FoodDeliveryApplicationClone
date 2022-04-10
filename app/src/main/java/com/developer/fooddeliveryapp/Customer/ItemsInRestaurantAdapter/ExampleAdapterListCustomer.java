package com.developer.fooddeliveryapp.Customer.ItemsInRestaurantAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;


public class ExampleAdapterListCustomer extends RecyclerView.Adapter<ExampleAdapterListCustomer.ExampleViewHolderItemsCustomer> {
    private static ArrayList<ExampleItemCustomerList> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void addToCart(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public ExampleAdapterListCustomer(ArrayList<ExampleItemCustomerList> exampleList) {
        mExampleList = exampleList;
    }


    public static class ExampleViewHolderItemsCustomer extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        private Button addToCart;
        private ImageView imageView;


        public ExampleViewHolderItemsCustomer(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            addToCart=itemView.findViewById(R.id.addToCart);
            imageView=itemView.findViewById(R.id.doneBtnCustomer);



            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.addToCart(position);
                        }
                        ExampleItemCustomerList currentItem = mExampleList.get(position);
                        currentItem.setQuantity("1");
                        addToCart.setClickable(false);
                        addToCart.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }


    @Override
    public ExampleViewHolderItemsCustomer onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_view_items_in_list, parent, false);
        ExampleViewHolderItemsCustomer evh = new ExampleViewHolderItemsCustomer(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolderItemsCustomer holder, int position) {
        ExampleItemCustomerList currentItem = mExampleList.get(position);

        holder.mImageView.setImageBitmap(StringToBitMap(currentItem.getImageResource()));
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}