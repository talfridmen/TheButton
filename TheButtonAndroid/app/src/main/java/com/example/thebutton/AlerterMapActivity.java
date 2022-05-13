package com.example.thebutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.thebutton.databinding.ActivityAlerterMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class AlerterMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button stopButton;
    private GoogleMap mMap;
    private ActivityAlerterMapBinding binding;
    String alertUUID;
    HashMap<String, Marker> responders;
    static boolean isShowing;

    @Override
    protected void onPause() {
        super.onPause();
        isShowing = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowing = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertUUID = getIntent().getStringExtra("alertUUID");

        binding = ActivityAlerterMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stopButton = findViewById(R.id.respondButton);
        stopButton.setOnClickListener(new StopAlertButtonOnClickListener(this));
    }

    public String getAlertUUID() {
        return alertUUID;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        // Add a marker in Sydney and move the camera
        LatLng alertPosition = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(alertPosition).title("Alert"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(alertPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.setMyLocationEnabled(true);

        startUpdateMapThread();
    }

    private void startUpdateMapThread() {
        responders = new HashMap<>();
        Thread updateMapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isShowing) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject json = HTTP.get("/api/alert/" + alertUUID + "/responding");
                        JSONArray respondersJsonArray = json.getJSONArray("responding");
                        for (int i = 0; i < respondersJsonArray.length(); i++) {
                            JSONObject responderJsonObject = respondersJsonArray.getJSONObject(i);
                            String responderID = responderJsonObject.getString("id");
                            String responderName = responderJsonObject.getString("name");
                            double longitude = responderJsonObject.getDouble("longitude");
                            double latitude = responderJsonObject.getDouble("latitude");
                            if (responders.containsKey(responderID)) {
                                Marker responderMarker = responders.get(responderID);
                                if (responderMarker != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            responderMarker.setPosition(new LatLng(latitude, longitude));
                                        }
                                    });
                                }
                                else {
                                    System.out.println("WTF?!");
                                }
                            } else {
                                MarkerOptions responderMarkerOptions =
                                        new MarkerOptions()
                                                .position(new LatLng(latitude, longitude))
                                                .title(responderName);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        responders.put(responderID, mMap.addMarker(responderMarkerOptions));
                                    }
                                });
                            }
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateMapThread.start();
    }
}

class StopAlertButtonOnClickListener implements View.OnClickListener {
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
                            "/api/alert/" + activity.alertUUID + "/cancel",
                            ""
                    );
                } catch (IOException | JSONException e) {
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
