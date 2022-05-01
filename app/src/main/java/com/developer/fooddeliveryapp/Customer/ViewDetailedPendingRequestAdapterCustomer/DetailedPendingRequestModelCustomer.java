package com.developer.fooddeliveryapp.Customer.ViewDetailedPendingRequestAdapterCustomer;

public class DetailedPendingRequestModelCustomer {
    private String text1;
    private String quantity;

    public DetailedPendingRequestModelCustomer(String itemName, String quantity) {
        this.text1 = itemName;
        this.quantity = quantity;
    }

    public DetailedPendingRequestModelCustomer() {
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
