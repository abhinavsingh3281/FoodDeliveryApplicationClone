package com.developer.fooddeliveryapp.Customer.AddressAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;
import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ExampleViewHolderItemsCustomer> {
    private static ArrayList<String> mExampleList;
    private AddressAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void deleteAddress(int position);
    }

    public void setOnItemClickListener(AddressAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public AddressAdapter(ArrayList<String> exampleList) {
        mExampleList = exampleList;
    }


    public static class ExampleViewHolderItemsCustomer extends RecyclerView.ViewHolder {
        public TextView mTextView1;
        public ImageButton btnDeleteAddress;

        public ExampleViewHolderItemsCustomer(View itemView, final AddressAdapter.OnItemClickListener listener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.addressAddressList);
            btnDeleteAddress=itemView.findViewById(R.id.btnDeleteAddress);

            btnDeleteAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.deleteAddress(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AddressAdapter.ExampleViewHolderItemsCustomer onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_address_list_view, parent, false);
        return new ExampleViewHolderItemsCustomer(v, mListener);
    }

    @Override
    public void onBindViewHolder(AddressAdapter.ExampleViewHolderItemsCustomer holder, int position) {
        String currentItem = mExampleList.get(position);
        holder.mTextView1.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}