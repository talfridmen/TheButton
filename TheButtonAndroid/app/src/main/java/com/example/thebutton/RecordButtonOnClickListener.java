package com.example.thebutton;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class RecordButtonOnClickListener implements View.OnClickListener {
    MainActivity activity;
    String alertUuid;


    public RecordButtonOnClickListener(MainActivity activity) {
        this.activity = activity;
    }

    private void sendAlert(double latitude, double longitude) {
        Thread sendAlertThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post(
                            "/api/alert",
                            new JSONObject()
                                    .put("latitude", latitude)
                                    .put("longitude", longitude)
                                    .put("alertUUID", alertUuid)
                                    .put("userId", activity.getSharedPreferences("_", Context.MODE_PRIVATE).getInt("userId", 0))
                                    .toString()
                    );
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        sendAlertThread.start();
    }

    private void changeViewToMap(double latitude, double longitude) {
        Intent intent = new Intent(activity, AlerterMapActivity.class);

        intent.putExtra("alertUUID", alertUuid);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        activity.startActivity(intent);
    }

    @NonNull
    private double[] getCoordinates() {
        return new double[]{activity.connection.getService().getLatitude(), activity.connection.getService().getLongitude()};
    }


    @Override
    public void onClick(View view) {
        alertUuid = UUID.randomUUID().toString();

        double[] latlang = getCoordinates();
        double latitude = latlang[0], longitude = latlang[1];

        sendAlert(latitude, longitude);

        changeViewToMap(latitude, longitude);
    }
}
