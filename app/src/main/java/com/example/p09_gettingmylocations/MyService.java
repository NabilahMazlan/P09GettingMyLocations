package com.example.p09_gettingmylocations;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    boolean started;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("Service", "Service started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(started == false){
            started = true;
            Log.d("Service", "Service stared");

        }else{
            Log.d("Service", "Service is still running");

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");

        super.onDestroy();
    }
}
