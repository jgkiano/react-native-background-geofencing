package me.kiano.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;

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
        for (RNGeofence storedGeofence: storedGeofences) {
            storedGeofence.start(false, new RNGeofenceHandler() {
                @Override
                public void onSuccess(final String geofenceId) {
                    Log.v(TAG, "Geofence successfully reinitialised: " + geofenceId);
                }
                @Override
                public void onError(final String geofenceId, Exception e) {
                    Log.v(TAG, "Geofence FAILED reinitialisation: " + geofenceId);
                    Log.e(TAG, e.getMessage());
                }
            });
        }
    }
}
