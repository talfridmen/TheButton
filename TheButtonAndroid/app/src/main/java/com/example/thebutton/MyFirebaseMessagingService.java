package com.example.thebutton;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        JsonBuilder json = new JsonBuilder();
        json.addItem("token", token);

        try {
            HTTP.post("http://127.0.0.1/api/token/update", json.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}