package com.example.thebutton.MyLocationService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.example.thebutton.HTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyLocationService extends Service implements LocationListener {
    double latitude, longitude;
    private final IBinder binder = new MyLocationServiceBinder();

    class MyLocationServiceBinder extends Binder {
        MyLocationService getService() {
            // Return this instance of MyLocationService so clients can call public methods
            return MyLocationService.this;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        if (getSharedPreferences("_", MODE_PRIVATE).getBoolean("isRegistered", false)) {
            Thread updateLocationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HTTP.post(
                                "/api/location/update",
                                new JSONObject()
                                        .put("userId", getSharedPreferences("_", MODE_PRIVATE).getInt("userId", 0))
                                        .put("longitude", longitude)
                                        .put("latitude", latitude)
                                        .toString()
                        );
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            updateLocationThread.start();
        }
    }

    public void registerLocationUpdates() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
    }
}
