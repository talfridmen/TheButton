package com.example.thebutton;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;


public class CallPoliceButtonOnClickListener implements View.OnClickListener {
    Activity activity;

    public CallPoliceButtonOnClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(
                activity.getApplicationContext(),
                "Calling Police!",
                Toast.LENGTH_SHORT
        ).show();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:100"));
        this.activity.startActivity(callIntent);
    }
}
