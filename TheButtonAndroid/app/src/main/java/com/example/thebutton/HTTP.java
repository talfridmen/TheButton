package com.example.thebutton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTP {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static JSONObject post(String urlSuffix, String json) throws IOException, JSONException {
        OkHttpClient okClient = new OkHttpClient();
        // TODO: if missing BuildConfig.SERVER_IP/PORT - write an indicative log message

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://" + BuildConfig.SERVER_IP + ":" + BuildConfig.SERVER_PORT + urlSuffix)
                .post(body)
                .build();
        try (Response response = okClient.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
    }

    public static JSONObject get(String urlSuffix) throws IOException {
        OkHttpClient okClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://" + BuildConfig.SERVER_IP + ":" + BuildConfig.SERVER_PORT + urlSuffix)
                .get()
                .build();
        try (Response response = okClient.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}
