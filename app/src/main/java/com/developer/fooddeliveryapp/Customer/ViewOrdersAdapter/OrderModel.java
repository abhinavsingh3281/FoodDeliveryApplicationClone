package com.developer.fooddeliveryapp.Customer.ViewOrdersAdapter;

import com.developer.fooddeliveryapp.Customer.CartAdapter.ExampleItemCustomerListCart;

import java.util.List;

public class OrderModel {
    private String name;
    private String address;
    private String restaurantName;
    private String price;
    private String email;
    private String date;
    private String orderId;
    private String status;
    private List<ExampleItemCustomerListCart> food;
    private String mobileNo;

    public OrderModel() {
    }

    public OrderModel(String name, String address, String restaurantName, String price, String email, String date, String orderId, String status, List<ExampleItemCustomerListCart> food, String mobileNo) {
        this.name = name;
        this.address = address;
        this.restaurantName = restaurantName;
        this.price = price;
        this.email = email;
        this.date = date;
        this.orderId = orderId;
        this.status = status;
        this.food = food;
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ExampleItemCustomerListCart> getFood() {
        return food;
    }

    public void setFood(List<ExampleItemCustomerListCart> food) {
        this.food = food;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
