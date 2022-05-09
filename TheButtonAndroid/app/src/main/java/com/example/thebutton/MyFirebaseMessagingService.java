package com.example.thebutton;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

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
}
