package com.example.thebutton;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordButtonOnTouchListener implements View.OnTouchListener {
    MainActivity activity;
    MediaRecorder recorder;
    String alertUuid;
    boolean isRecording = false;


    public RecordButtonOnTouchListener(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
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

    private void sendAlert(double latitude, double longitude, String recordingData) {
        Thread sendAlertThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post(
                            "/api/alert",
                            new JSONObject()
                                    .put("latitude", latitude)
                                    .put("longitude", longitude)
                                    .put("recording", recordingData)
                                    .put("alertUUID", alertUuid)
                                    .put("userId", 1)
                                    .toString()
                    );
                } catch (IOException | JSONException e) {
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
        isRecording = false;

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

    @NonNull
    private double[] getCoordinates() {
        return new double[]{activity.connection.getService().getLatitude(), activity.connection.getService().getLongitude()};
    }

    @Override
    public boolean onTouch(View view, @NonNull MotionEvent motionEvent) {
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
