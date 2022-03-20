package com.developer.fooddeliveryapp.Customer.ItemsAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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


        public ExampleViewHolderItemsCustomer(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
            addToCart=itemView.findViewById(R.id.addToCart);


            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.addToCart(position);
                        }
                        addToCart.setVisibility(View.INVISIBLE);
                    }
                }
            });




//            addImageQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onAddImageQuantity(position);
//                        }
//                        ExampleItemCustomerList currentItem = mExampleList.get(position);
//                        String q = quantity.getText().toString();
//                        int quant = Integer.parseInt(q);
//                        quant++;
//                        currentItem.setQuantity(Integer.toString(quant));
//                        quantity.setText(Integer.toString(quant));
//                    }
//                }
//            });

//            removeImageQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        listener.onRemoveImageQuantity(position);
//                    }
//                    String q = quantity.getText().toString();
//                    int quant = Integer.parseInt(q);
//                    quant--;
//                    if (quant == 0) {
//                        linearLayout.setVisibility(View.GONE);
//                        mAddImage.setVisibility(View.VISIBLE);
//                    }
//                    ExampleItemCustomerList currentItem = mExampleList.get(position);
//                    currentItem.setQuantity(Integer.toString(quant));
//                    quantity.setText(Integer.toString(quant));
//                }
//            });

        }
    }


    @Override
    public ExampleViewHolderItemsCustomer onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exampleitem_list_customer, parent, false);
        ExampleViewHolderItemsCustomer evh = new ExampleViewHolderItemsCustomer(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolderItemsCustomer holder, int position) {
        ExampleItemCustomerList currentItem = mExampleList.get(position);

        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}