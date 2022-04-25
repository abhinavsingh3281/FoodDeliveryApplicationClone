package com.developer.fooddeliveryapp;

public class User {

    private String name;
    private String email;
    private String mobileNo;
    private String password;
    private String role;
    private String address;
    private String pinCode;
    private String image;
    private String uid;


    public User(String name, String email, String mobileNo, String password) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
    }

    public User(String name, String email, String mobileNo, String password, String role) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
    }

    public User(String name, String email, String mobileNo, String password, String role, String address, String pinCode,String image) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
        this.address = address;
        this.pinCode = pinCode;
        this.image=image;
    }

    public User(String name, String email, String mobileNo, String password, String role, String address, String pinCode, String image, String uid) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.role = role;
        this.address = address;
        this.pinCode = pinCode;
        this.image = image;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public void setRole(String role) {
        this.role = role;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
