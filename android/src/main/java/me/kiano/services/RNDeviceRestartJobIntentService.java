package me.kiano.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import me.kiano.models.RNGeofence;

public class RNDeviceRestartJobIntentService extends JobIntentService {

    private final static String TAG = "RNDeviceRestartJob";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "RNDeviceRestartJobIntentService called");
        enqueueWork(context, RNDeviceRestartJobIntentService.class, 665, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (RNGeofence.hasLocationPermission(getApplicationContext()) && RNGeofence.isLocationEnabled(getApplicationContext())) {
            Log.v(TAG, "Starting to re register geofences");
            RNGeofence.reRegisterStoredGeofences(getApplicationContext());
        } else {
            Log.v(TAG, "Starting location provider listener service..");
            Intent service = new Intent(getApplicationContext(), RNLocationProviderChangeBroadcastListener.class);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(service);
            } else {
                getApplicationContext().startService(service);
            }
        }
    }
}
