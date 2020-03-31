package me.kiano.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import me.kiano.services.ReinitialiseGeofencesJobIntentService;

public class OnDeviceRebootBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "RNBackgroundGeofencing";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "Device restart detected");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, "We see you've restarted your device -_-", Toast.LENGTH_LONG).show();
            }
        });
        ReinitialiseGeofencesJobIntentService.enqueueWork(context, intent);
    }
}
