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
        recordButton.setOnTouchListener(new RecordButtonOnTouchListener(this));
    }

    private void validatePermissions() {
        while (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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

        Intent intent = new Intent(this, MyLocationService.class);
        this.startService(intent);

        setContentView(R.layout.activity_main);

        setOnClickListeners();

        bindLocationService();
    }
}