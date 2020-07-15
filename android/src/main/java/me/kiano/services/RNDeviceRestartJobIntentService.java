package me.kiano.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;
import java.util.Collections;

import me.kiano.database.RNGeofenceDB;
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.models.RNGeofence;

public class RNDeviceRestartJobIntentService extends JobIntentService {

    private final static String TAG = "RNDeviceRestartJob";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "RNDeviceRestartJobIntentService called");
        enqueueWork(context, RNDeviceRestartJobIntentService.class, 665, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        RNGeofenceDB db = new RNGeofenceDB(getApplication());
        ArrayList<RNGeofence> storedGeofences = db.getAllGeofences();
        Log.v(TAG, "RNDeviceRestartJobIntentService work started: " + storedGeofences.size());

        for (RNGeofence storedGeofence : storedGeofences) {
            if (storedGeofence.registerOnDeviceRestart) {
                storedGeofence.start(true, new RNGeofenceHandler() {
                    @Override
                    public void onSuccess(final String geofenceId) {
                        Log.v(TAG, "Geofence started successfully after restart");
                        RNGeofence.setFailing(geofenceId, false, getApplicationContext());
                    }

                    @Override
                    public void onError(final String geofenceId, Exception e) {
                        // TODO: After saving failed geofence start the worker
                        Log.v(TAG, "Geofence failed to start. We'll get em next time.");
                        RNGeofence.setFailing(geofenceId, true, getApplicationContext());
                    }
                });
            } else {
                // TODO: manually expire geofences so that periodic worker doesn't restart them
            }
        }
    }
}
