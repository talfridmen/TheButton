package com.example.thebutton;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.provider.MediaStore;
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

    public RecordButtonOnTouchListener(Activity activity) {
        this.activity = activity;
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

    public String createJson(float latitude, float longitude, String recordingBlob) {
        JsonBuilder json = new JsonBuilder();
        json.addItem("latitude", latitude);
        json.addItem("longitude", longitude);
        json.addItem("recording", recordingBlob);
        return json.build();
    }

    private void sendAlert(float latitude, float longitude, String recordingData) {
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

    private void changeViewToMap(float latitude, float longitude) {
        Intent intent = new Intent(activity, AlerterMapActivity.class);

        intent.putExtra("alertUUID", alertUuid);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);

        activity.startActivity(intent);
    }

    private float[] getCoordinates() {
        return new float[] {33.0536f, 35.5890f};
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isRecording) {
            return startRecording();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            String recordingData = stopRecording();
            float[] latlang = getCoordinates();
            float latitude = latlang[0], longitude = latlang[1];

            sendAlert(latitude, longitude, recordingData);

            changeViewToMap(latitude, longitude);
        }
        return true;
    }
}
