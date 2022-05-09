package com.example.thebutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText nameEditText, phoneEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        phoneEditText = (EditText) findViewById(R.id.editTextPhone);
        registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(this);
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            nameEditText.setError("Name is required");
            valid = false;
        }
        if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            nameEditText.setError("Phone is required");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View view) {
        if (validateForm()) {
            Thread registrationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = getSharedPreferences("_", MODE_PRIVATE).getString("FirebaseToken", "");
                        JSONObject registrationJSON = HTTP.post(
                                "/api/register",
                                new JSONObject()
                                        .put("name", nameEditText.getText().toString())
                                        .put("phone", phoneEditText.getText().toString())
                                        .put("token", token)
                                        .toString());
                        if (registrationJSON.has("userId")) {
                            getSharedPreferences("_", MODE_PRIVATE)
                                    .edit()
                                    .putInt("userId", registrationJSON.getInt("userId"))
                                    .putBoolean("isRegistered", true)
                                    .apply();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            registrationThread.start();
        }
    }
}
