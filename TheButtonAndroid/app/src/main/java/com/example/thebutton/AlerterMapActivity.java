package com.example.thebutton;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.thebutton.databinding.ActivityAlerterMapBinding;

public class AlerterMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAlerterMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAlerterMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        float latitude = intent.getFloatExtra("latitude", 0);
        float longitude = intent.getFloatExtra("longitude", 0);

        // Add a marker in Sydney and move the camera
        LatLng alertPosition = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(alertPosition).title("Alert"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(alertPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }
}
