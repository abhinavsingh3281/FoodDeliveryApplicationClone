package com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;


public class ExampleAdapterOrdersCustomer extends RecyclerView.Adapter<ExampleAdapterOrdersCustomer.ExampleViewHolder> {
    private ArrayList<OrderModel> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView orderId;
        public TextView date;
        public TextView total;
        public TextView status;
        public ImageView mDeleteImage;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.txtRestaurantName);
            orderId = itemView.findViewById(R.id.txtOrderId);
            date = itemView.findViewById(R.id.txtDate);
            total = itemView.findViewById(R.id.txtTotal);
            status=itemView.findViewById(R.id.txtStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public ExampleAdapterOrdersCustomer(ArrayList<OrderModel> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_view_all_orders, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        OrderModel currentItem = mExampleList.get(position);

        holder.restaurantName.setText(currentItem.getRestaurantName());
        holder.total.setText(currentItem.getPrice());
        holder.orderId.setText(currentItem.getOrderId());
        holder.date.setText(currentItem.getDate());
        holder.status.setText(currentItem.getStatus());
    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}