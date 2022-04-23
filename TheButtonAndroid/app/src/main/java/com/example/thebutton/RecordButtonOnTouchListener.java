package com.example.thebutton;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public class RecordButtonOnTouchListener implements View.OnTouchListener {
    Activity activity;
    MediaRecorder recorder;
    String alertUuid;
    boolean isRecording = false;
    MyLocationListener locationListener;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.w("MEEEEE", "Service Connected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyLocationListener.LocalBinder binder = (MyLocationListener.LocalBinder) service;
            locationListener = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public RecordButtonOnTouchListener(Activity activity) {
        this.activity = activity;
//        locationListener.registerLocationUpdates();
    }

    private String readBinaryFileToBase64(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] recordingData = new byte[fis.available()];
            fis.read(recordingData);

            String base64Value = Base64.getEncoder().encodeToString(recordingData);
            fis.close();
            return base64Value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createJson(double latitude, double longitude, String recordingBlob) {
        JsonBuilder json = new JsonBuilder();
        json.addItem("latitude", latitude);
        json.addItem("longitude", longitude);
        json.addItem("recording", recordingBlob);
        return json.build();
    }

    private void sendAlert(double latitude, double longitude, String recordingData) {
        Thread sendAlertThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post("http://192.168.1.40:5000/api/alert", createJson(latitude, longitude, recordingData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sendAlertThread.start();
    }

    private boolean startRecording() {
        alertUuid = UUID.randomUUID().toString();
        recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.MediaColumns.TITLE, alertUuid);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(activity.getFilesDir() + alertUuid);
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Error while preparing recorder",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }
        recorder.start();
        isRecording = true;
        Toast.makeText(
                activity.getApplicationContext(),
                "Started Recording",
                Toast.LENGTH_SHORT
        ).show();
        return true;
    }

    private String stopRecording() {
        recorder.stop();
        recorder.release();

        String recordingPath = activity.getFilesDir() + alertUuid;

        if ((new File(recordingPath)).exists()) {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Recording Saved!",
                    Toast.LENGTH_SHORT
            ).show();
        }

        return readBinaryFileToBase64(recordingPath);
    }

    private void changeViewToMap(double latitude, double longitude) {
        Intent intent = new Intent(activity, AlerterMapActivity.class);

        intent.putExtra("alertUUID", alertUuid);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        activity.startActivity(intent);
    }

    private double[] getCoordinates() {
        return new double[] {locationListener.latitude, locationListener.longitude};
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isRecording) {
            return startRecording();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            String recordingData = stopRecording();
            double[] latlang = getCoordinates();
            double latitude = latlang[0], longitude = latlang[1];

            sendAlert(latitude, longitude, recordingData);

            changeViewToMap(latitude, longitude);
        }
        return true;
    }
}
