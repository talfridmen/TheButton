package com.example.thebutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        getSharedPreferences("_", MODE_PRIVATE)
                .edit()
                .putString("FirebaseToken", token)
                .apply();

        if (getSharedPreferences("_", MODE_PRIVATE).getBoolean("isRegistered", false)) {
            try {
                HTTP.post(
                        "/api/token/update",
                        new JSONObject()
                                .put(
                                        "userId",
                                        getSharedPreferences("_", MODE_PRIVATE)
                                                .getInt("userId", 0)
                                )
                                .put("token", token)
                                .toString()
                );
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        final String CHANNEL_ID = "The_Button_Notification";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "The Button Notification",
                NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Intent alertIntent = new Intent(getApplicationContext(), RespondActivity.class);
        alertIntent.putExtra("alertUUID", message.getData().get("alertUUID"));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                1337,
                alertIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());
    }
}
