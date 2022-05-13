package com.example.thebutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.view.View;
import android.widget.Button;

import com.example.thebutton.databinding.ActivityAlerterMapBinding;
import com.example.thebutton.databinding.ActivityRespondBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RespondActivity extends AppCompatActivity implements OnMapReadyCallback {
    String alertUUID;
    private GoogleMap mMap;
    private ActivityRespondBinding binding;
    Button respondButton, declineButton, callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alertUUID = getIntent().getStringExtra("alertUUID");

        binding = ActivityRespondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        respondButton = findViewById(R.id.respondButton);
        respondButton.setOnClickListener(new RespondButtonOnClickListener(this));
        declineButton = findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new DeclineButtonOnClickListener(this));
        callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new CallButtonOnClickListener(this));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        Thread getAlertLocationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = HTTP.get("/api/alert/" + alertUUID);
                    if (! response.getBoolean("alertActive")) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    double latitude = response.getDouble("latitude");
                    double longitude = response.getDouble("longitude");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LatLng alertPosition = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(alertPosition).title("Alert"));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(alertPosition));
                            mMap.setMyLocationEnabled(true);
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        getAlertLocationThread.start();
    }
}

class RespondButtonOnClickListener implements View.OnClickListener {
    RespondActivity activity;

    public RespondButtonOnClickListener(RespondActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Thread respondThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post(
                            "/api/alert/" + activity.alertUUID + "/respond",
                            new JSONObject()
                                    .put("userId", activity.getSharedPreferences("_", Context.MODE_PRIVATE).getInt("userId", 0))
                                    .toString()
                    );
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        respondThread.start();

        activity.respondButton.setVisibility(View.INVISIBLE);
        activity.declineButton.setVisibility(View.INVISIBLE);
        activity.callButton.setVisibility(View.VISIBLE);
    }
}

class DeclineButtonOnClickListener implements View.OnClickListener {
    RespondActivity activity;

    public DeclineButtonOnClickListener(RespondActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}

class CallButtonOnClickListener implements View.OnClickListener {
    RespondActivity activity;

    public CallButtonOnClickListener(RespondActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Thread callAlerterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = HTTP.get("/api/alert/" + activity.alertUUID + "/phone");
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + response.getString("phone")));
                    activity.startActivity(callIntent);
                } catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });

        callAlerterThread.start();

    }
}