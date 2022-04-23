package com.example.thebutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button callPoliceButton, recordButton;

    private void setOnClickListeners() {
        callPoliceButton = findViewById(R.id.callPoliceButton);
        callPoliceButton.setOnClickListener(new CallPoliceButtonOnClickListener(this));

        recordButton = findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new RecordButtonOnTouchListener(this));
    }

    private void validatePermissions() {
        while (!(
                ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                                getApplicationContext(), Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validatePermissions();

        setContentView(R.layout.activity_main);

        setOnClickListeners();

    }
}