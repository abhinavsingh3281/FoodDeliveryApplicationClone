package com.developer.fooddeliveryapp.Delivery.DeliveryHomePageAdap;

public class DeliveryHomePageModel {
    String orderId;
    String name;
    String restaurantName;
    String userAddress;
    String restaurantAddress;
    String restaurantLongitude;
    String restaurantLatitude;
    String customerLongitude;
    String customerLatitude;
    String customerMobileNo;
    String restaurantMobileNo;

    public DeliveryHomePageModel(String name, String restaurantName, String userAddress, String restaurantAddress,String restaurantLatitude,String restaurantLongitude,String customerLongitude,String customerLatitude) {
        this.name = name;
        this.restaurantName = restaurantName;
        this.userAddress = userAddress;
        this.restaurantAddress = restaurantAddress;
        this.restaurantLatitude=restaurantLatitude;
        this.restaurantLongitude=restaurantLongitude;
        this.customerLatitude=customerLatitude;
        this.customerLongitude=customerLongitude;
    }

    public DeliveryHomePageModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public void setRestaurantLongitude(String restaurantLongitude) {
        this.restaurantLongitude = restaurantLongitude;
    }

    public String getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public void setRestaurantLatitude(String restaurantLatitude) {
        this.restaurantLatitude = restaurantLatitude;
    }

    public String getCustomerLongitude() {
        return customerLongitude;
    }

    public void setCustomerLongitude(String customerLongitude) {
        this.customerLongitude = customerLongitude;
    }

    public String getCustomerLatitude() {
        return customerLatitude;
    }

    public void setCustomerLatitude(String customerLatitude) {
        this.customerLatitude = customerLatitude;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerMobileNo() {
        return customerMobileNo;
    }

    public void setCustomerMobileNo(String customerMobileNo) {
        this.customerMobileNo = customerMobileNo;
    }

    public String getRestaurantMobileNo() {
        return restaurantMobileNo;
    }

    public void setRestaurantMobileNo(String restaurantMobileNo) {
        this.restaurantMobileNo = restaurantMobileNo;
    }
}
