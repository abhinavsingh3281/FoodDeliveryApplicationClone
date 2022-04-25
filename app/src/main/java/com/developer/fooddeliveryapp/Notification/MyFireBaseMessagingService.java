package com.developer.fooddeliveryapp.Notification;

import static android.os.Build.VERSION_CODES.R;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.developer.fooddeliveryapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        String CHANNEL_ID = "MESSAGE";
        String CHANNEL_NAME = "MESSAGE";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + com.developer.fooddeliveryapp.R.raw.sound);
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(com.developer.fooddeliveryapp.R.drawable.applogo)  // here is the animated icon
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), com.developer.fooddeliveryapp.R.drawable.applogo))
//                            .setSmallIcon(com.developer.fooddeliveryapp.R.drawable.gifstart)
                            .setContentTitle(title)
                            .setPriority(Notification.PRIORITY_MAX)
                            .setSound(soundUri)
                            .setContentText(message);

            NotificationManager manager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager1.notify(0, builder.build());
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(com.developer.fooddeliveryapp.R.drawable.applogo)  // here is the animated icon
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), com.developer.fooddeliveryapp.R.drawable.applogo))
//                .setSmallIcon(com.developer.fooddeliveryapp.R.drawable.gifstart)
                .setContentTitle(title + " hello")
                .setContentText(message)
                .build();
        manager.notify(getRandomNumber(), notification);


    }

    private static int getRandomNumber() {
        Date dd= new Date();
        SimpleDateFormat ft =new SimpleDateFormat ("mmssSS");
        String s=ft.format(dd);
        return Integer.parseInt(s);
    }
}
