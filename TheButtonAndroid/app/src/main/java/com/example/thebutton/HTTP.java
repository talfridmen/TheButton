package com.example.thebutton;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        OkHttpClient okClient = new OkHttpClient();

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
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
