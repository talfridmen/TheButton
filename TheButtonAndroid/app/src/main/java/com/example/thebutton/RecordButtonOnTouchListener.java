package com.example.thebutton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    public String createJson(float longitude, float latitude, String recordingBlob) {
        JsonBuilder json = new JsonBuilder();
        json.addItem("longitude", longitude);
        json.addItem("latitude", latitude);
        json.addItem("recording", recordingBlob);
        return json.build();
    }


    private String getAlertData() throws IOException {
        String recording = readBinaryFileToBase64(activity.getFilesDir() + alertUuid);

        return createJson(
                0,
                0,
                recording
        );
    }

    private void sendAlert() {
        Thread sendAlertThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HTTP.post("http://192.168.1.40:5000/api/alert", getAlertData());
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

    private boolean stopRecording() {
        Toast.makeText(
                activity.getApplicationContext(),
                "Stopped Recording. Sending Alert!",
                Toast.LENGTH_SHORT
        ).show();
        recorder.stop();
        recorder.release();

        sendAlert();


        if ((new File(activity.getFilesDir() + alertUuid)).exists()) {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "Recording Saved!",
                    Toast.LENGTH_SHORT
            ).show();
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && !isRecording) {
            return startRecording();
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            return stopRecording();
        }
        return true;
    }
}
