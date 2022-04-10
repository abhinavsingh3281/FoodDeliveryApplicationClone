package com.developer.fooddeliveryapp.Customer.HomePageAdapter;




public class ExampleItemCustomer {
    private String name;
    private String email;
    private String image;
    private String mobileNo;
    private String password;
    private String restaurantName;

    public ExampleItemCustomer(){

    }

    public ExampleItemCustomer(String name, String email, String mobileNo, String password,String restaurantName,String image) {
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.password = password;
        this.image=image;
        this.restaurantName=restaurantName;
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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
