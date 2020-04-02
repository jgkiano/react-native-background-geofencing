package me.kiano.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.kiano.services.GeofenceTransitionsJobIntentService;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "RNBackgroundGeofencing";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "GeofenceBroadcastReceiver received!");
        GeofenceTransitionsJobIntentService.enqueueWork(context, intent);
    }
}
