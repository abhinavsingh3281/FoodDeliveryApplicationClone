package com.developer.fooddeliveryapp.Customer.CartAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.developer.fooddeliveryapp.R;

import java.util.ArrayList;


public class ExampleAdapterListCustomerCart extends RecyclerView.Adapter<ExampleAdapterListCustomerCart.ExampleViewHolderItemsCustomer> {
    private static ArrayList<ExampleItemCustomerListCart> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void addQuantityCart(int position);
        void removeQuantityCart(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public ExampleAdapterListCustomerCart(ArrayList<ExampleItemCustomerListCart> exampleList) {
        mExampleList = exampleList;
    }


    public static class ExampleViewHolderItemsCustomer extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView quantity;
        public ImageView addBtn;
        public ImageView removeBtn;


//        NumberPicker numberPicker;


        public ExampleViewHolderItemsCustomer(View itemView, final OnItemClickListener listener) {
            super(itemView);
//            mImageView = itemView.findViewById(R.id.imageViewCart);
            mTextView1 = itemView.findViewById(R.id.textViewCart);
            mTextView2 = itemView.findViewById(R.id.textView2Cart);

            quantity = itemView.findViewById(R.id.quantity);

            addBtn=itemView.findViewById(R.id.AddBtn);
            removeBtn=itemView.findViewById(R.id.RemoveBtn);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ExampleItemCustomerListCart currentItem = mExampleList.get(position);
                            listener.addQuantityCart(position);

                           int quant= Integer.parseInt(currentItem.getQuantity())+1;

                            currentItem.setQuantity(Integer.toString(quant));
                            quantity.setText(Integer.toString(quant));
                        }
                    }
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ExampleItemCustomerListCart currentItem = mExampleList.get(position);
                            listener.removeQuantityCart(position);
                            int quant= Integer.parseInt(currentItem.getQuantity())-1;

                            currentItem.setQuantity(Integer.toString(quant));

                            quantity.setText(Integer.toString(quant));
                        }
                    }
                }
            });

        }
    }
//            quantity.addTextChangedListener(new TextWatcher() {
//                private String num;
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    num = s.toString();
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });

//            numberPicker= (NumberPicker) itemView.findViewById(R.id.number_picker);
//            if (numberPicker != null) {
//                numberPicker.setMinValue(0);
//                numberPicker.setMaxValue(10);
//                numberPicker.setWrapSelectorWheel(true);
//            }
//            assert numberPicker != null;
//            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                @Override
//                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                    String text = "Changed from " + oldVal + " to " + newVal;
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//
//                            listener.updateQuantityCart(position);
//                            numberPicker.setValue(newVal);
//                        }
//                    }
//
//                        numberPicker.setVisibility(View.INVISIBLE);
//                    }
//
//            });

//            numberPicker.set

//            numberPicker.setValue(1);
//
//        });
//    }



    @Override
    public ExampleViewHolderItemsCustomer onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_cart_list_view, parent, false);
        ExampleViewHolderItemsCustomer evh = new ExampleViewHolderItemsCustomer(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolderItemsCustomer holder, int position) {
        ExampleItemCustomerListCart currentItem = mExampleList.get(position);

//        holder.mImageView.setImageResource(currentItem.getImageResource());
        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
        holder.quantity.setText(currentItem.getQuantity());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}