package com.developer.fooddeliveryapp.Restaurant;




public class ExampleItem {
    private String mImageResource;
    private String mText1;
    private String mText2;

    public ExampleItem()
    {

    }

    public ExampleItem(String mImageResource,String mText1, String mText2) {
        this.mText1 = mText1;
        this.mText2 = mText2;
        this.mImageResource=mImageResource;
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

    public String getImageResource() {
        return mImageResource;
    }

    public void setImageResource(String mImageResource) {
        this.mImageResource = mImageResource;
    }
}
