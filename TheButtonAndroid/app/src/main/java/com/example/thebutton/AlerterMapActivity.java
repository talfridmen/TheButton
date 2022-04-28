package com.example.thebutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.thebutton.databinding.ActivityAlerterMapBinding;



public class AlerterMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button stopButton;
    private GoogleMap mMap;
    private ActivityAlerterMapBinding binding;
    String alertUUID;

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

        stopButton = findViewById(R.id.stopAlertButton);
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
    }
}
