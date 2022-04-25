package com.developer.fooddeliveryapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAlgaAx08:APA91bGrbdiGO2c6d5OEETCr3S8P5_ptJWkZtAGgqHYTPKeK9Mywk70lDDUDklO8SdLIfSfoeqo1iqdGcVGnsoQF5qkADW4lS3uJpi48OPo3daYU2pOmZJ38ZMMH457N0wBBqjKPHx0j"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

