package com.example.thebutton;

import android.content.Intent;
import android.view.View;

import java.io.IOException;

public class StopAlertButtonOnClickListener implements View.OnClickListener {
    AlerterMapActivity activity;

    public StopAlertButtonOnClickListener(AlerterMapActivity activity) {
        this.activity = activity;
    }

    private void sendCancellationToServer() {
        Thread sendAlertCancellationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post(
                            "/api/stopAlert",
                            new JsonBuilder()
                                    .addItem("AlertId", activity.getAlertUUID())
                                    .build()
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sendAlertCancellationThread.start();
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        sendCancellationToServer();
        moveToMainActivity();
    }
}
