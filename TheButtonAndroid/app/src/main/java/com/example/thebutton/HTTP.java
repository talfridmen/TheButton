package com.example.thebutton;

import android.graphics.Bitmap;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String post(String urlSuffix, String json) throws IOException {
        OkHttpClient okClient = new OkHttpClient();

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://" + BuildConfig.SERVER_IP + ":" + BuildConfig.SERVER_PORT + urlSuffix)
                .post(body)
                .build();
        try (Response response = okClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String get(String url) {
        return "";
    }
}
