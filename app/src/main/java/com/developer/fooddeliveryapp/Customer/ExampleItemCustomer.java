package com.developer.fooddeliveryapp.Customer;




public class ExampleItemCustomer {
    private String name;
    private String email;
    private String mobileNo;
    private String password;

    public ExampleItemCustomer(){

    }

    public ExampleItemCustomer(String name, String email, String mobileNo, String password) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
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
}
