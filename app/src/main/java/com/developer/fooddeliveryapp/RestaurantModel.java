package com.developer.fooddeliveryapp;

public class RestaurantModel {

    private String image;
    private String name;
    private String email;
    private String mobileNo;
    private String password;
    private String role;
    private String restaurantName;
    private String address;
    private String pinCode;
    private String gstNo;
    private String uid;

    public RestaurantModel(String image, String name, String email, String mobileNo, String password, String role, String restaurantName, String address, String pinCode, String gstNo, String uid) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
        this.restaurantName = restaurantName;
        this.address = address;
        this.pinCode = pinCode;
        this.gstNo = gstNo;
        this.uid = uid;
    }

    public RestaurantModel(String image, String name, String email, String mobileNo, String password, String role, String restaurantName, String address, String pinCode) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
        this.restaurantName = restaurantName;
        this.address = address;
        this.pinCode = pinCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
