package com.example.thebutton.MyLocationService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MyLocationServiceConnection implements ServiceConnection {
    MyLocationService mService;
    boolean mBound;

    public boolean isBound() {
        return mBound;
    }

    public MyLocationService getService() {
        return mService;
    }

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        MyLocationService.MyLocationServiceBinder binder = (MyLocationService.MyLocationServiceBinder) service;
        mService = binder.getService();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mBound = false;
    }
}
