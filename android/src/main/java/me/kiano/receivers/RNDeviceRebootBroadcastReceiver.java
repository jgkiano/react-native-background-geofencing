package me.kiano.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.kiano.services.RNDeviceRestartJobIntentService;

public class RNDeviceRebootBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "RNBackgroundGeofencing";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "Device restart detected");
        RNDeviceRestartJobIntentService.enqueueWork(context, intent);
    }
}
