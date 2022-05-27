package com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter.OrderModel;
import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;


public class DeliveryHomePageAdapter extends RecyclerView.Adapter<DeliveryHomePageAdapter.ExampleViewHolder> {
    private ArrayList<DeliveryHomePageModel> mExampleList;
    private DeliveryHomePageAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(DeliveryHomePageAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView pickUpAddress;
        public TextView dropOffAddress;

        public ExampleViewHolder(View itemView, final DeliveryHomePageAdapter.OnItemClickListener listener) {
            super(itemView);
            pickUpAddress = itemView.findViewById(R.id.pickUpAddressDP);
            dropOffAddress = itemView.findViewById(R.id.dropOffAddressDP);

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

    public DeliveryHomePageAdapter(ArrayList<DeliveryHomePageModel> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public DeliveryHomePageAdapter.ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_partner_home_page_card, parent, false);
        DeliveryHomePageAdapter.ExampleViewHolder evh = new DeliveryHomePageAdapter.ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(DeliveryHomePageAdapter.ExampleViewHolder holder, int position) {
        DeliveryHomePageModel  currentItem = mExampleList.get(position);

        holder.pickUpAddress.setText(currentItem.getRestaurantAddress());
        holder.dropOffAddress.setText(currentItem.getUserAddress());
    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
