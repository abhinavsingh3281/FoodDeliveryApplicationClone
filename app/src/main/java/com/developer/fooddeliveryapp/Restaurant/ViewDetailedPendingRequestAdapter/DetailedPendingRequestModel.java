package com.developer.fooddeliveryapp.Restaurant.ViewDetailedPendingRequestAdapter;

public class DetailedPendingRequestModel {
    private String text1;
    private String quantity;

    public DetailedPendingRequestModel(String itemName, String quantity) {
        this.text1 = itemName;
        this.quantity = quantity;
    }

    public DetailedPendingRequestModel() {
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
