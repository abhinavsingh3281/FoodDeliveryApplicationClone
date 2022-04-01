package com.developer.fooddeliveryapp.Customer.CartAdapter;




public class ExampleItemCustomerListCart {
    private int mImageResource;
    private String mText1;
    private String mText2;
    private String quantity;

    public ExampleItemCustomerListCart()
    {

    }

    public ExampleItemCustomerListCart(int mImageResource, String mText1, String mText2, String quantity) {
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.mImageResource=mImageResource;
        this.quantity=quantity;
    }

    public ExampleItemCustomerListCart( String mText1, String mText2, String quantity) {
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.quantity=quantity;
    }
    public void changeText1(String text) {
        mText1 = text;
    }

////    public int getImageResource() {
////        return mImageResource;
////    }
//
//    public String getText1() {
//        return mText1;
//    }
//
//    public String getText2() {
//        return mText2;
//    }

    public String getText1() {
        return mText1;
    }

    public void setText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getText2() {
        return mText2;
    }

    public void setText2(String mText2) {
        this.mText2 = mText2;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public void setImageResource(int mImageResource) {
        this.mImageResource = mImageResource;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
