package com.example.thebutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.example.thebutton.MyLocationService.MyLocationService;
import com.example.thebutton.MyLocationService.MyLocationServiceConnection;


public class MainActivity extends AppCompatActivity {
    Button callPoliceButton, recordButton;
    MyLocationServiceConnection connection = new MyLocationServiceConnection();

    private void setOnClickListeners() {
        callPoliceButton = findViewById(R.id.callPoliceButton);
        callPoliceButton.setOnClickListener(new CallPoliceButtonOnClickListener(this));

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new RecordButtonOnClickListener(this));
    }

    private void validatePermissions() {
        boolean requested = false;
        while (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            if (!requested) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                requested = true;
            }
        }
    }

    private void bindLocationService() {
        if (!connection.isBound()) {
            Intent intent = new Intent(this, MyLocationService.class);
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validatePermissions();
        Intent locationServiceIntent = new Intent(this, MyLocationService.class);
        this.startService(locationServiceIntent);

        if (!getSharedPreferences("_", MODE_PRIVATE).getBoolean("isRegistered", false)) {
            Intent registrationIntent = new Intent(this, RegistrationActivity.class);
            this.startActivity(registrationIntent);
        }

        if (getIntent().getStringExtra("alertUUID") != null) {
            Intent alertIntent = new Intent(this, RespondActivity.class);
            alertIntent.putExtra("alertUUID", getIntent().getStringExtra("alertUUID"));
            startActivity(alertIntent);
        }

        setContentView(R.layout.activity_main);
        setOnClickListeners();
        bindLocationService();
    }
}