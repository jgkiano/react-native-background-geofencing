package me.kiano.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofence;

public class RNDeviceRestartJobIntentService extends JobIntentService {

    private final static String TAG = "RNDeviceRestartJob";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "RNDeviceRestartJobIntentService called");
        enqueueWork(context, RNDeviceRestartJobIntentService.class, 665, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (RNGeofence.isLocationServicesEnabled(getApplicationContext()) && RNGeofence.hasLocationPermission(getApplicationContext())) {
            RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
            ArrayList<RNGeofence> geofences = db.getAllRestartGeofences();
            if (geofences.size() > 0) {
                RNGeofence.schedulePeriodicWork(getApplicationContext());
            }
        }
    }
}
