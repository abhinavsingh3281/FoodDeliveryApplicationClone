package com.developer.fooddeliveryapp.Restraunt.ViewOrdersAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;


public class ExampleAdapterOrdersRestaurant extends RecyclerView.Adapter<ExampleAdapterOrdersRestaurant.ExampleViewHolder> {
    private ArrayList<OrderModel> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView orderId;
        public TextView date;
        public TextView total;
        public TextView address;
        public ImageView mDeleteImage;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.txtCustomerName);
            orderId = itemView.findViewById(R.id.txtOrderId);
            date = itemView.findViewById(R.id.txtDate);
            total = itemView.findViewById(R.id.txtTotal);
            address=itemView.findViewById(R.id.txtAddress);

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

    public ExampleAdapterOrdersRestaurant(ArrayList<OrderModel> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view_pending_orders, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        OrderModel currentItem = mExampleList.get(position);

        holder.name.setText(currentItem.getName());
        holder.total.setText(currentItem.getPrice());
        holder.orderId.setText(currentItem.getOrderId());
        holder.date.setText(currentItem.getDate());
        holder.address.setText(currentItem.getAddress());
    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}