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
        // cancel any existing jobs
        RNGeofence.cancelPeriodicWork(getApplicationContext());

        // get db instance
        final RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());

        // remove all error geofences
        db.removeAllErrorGeofence();

        // get all geofences with a restart policy
        ArrayList<RNGeofence> geofences = db.getAllGeofences();
        ArrayList<RNGeofence> restartGeofences = new ArrayList<>();
        for (RNGeofence geofence: geofences) {
            if (geofence.registerOnDeviceRestart) {
                restartGeofences.add(geofence);
            }
        }

        // start geofences with restart policy, if failed to start add them to db to restart later
        if (!restartGeofences.isEmpty()) {
            RNGeofence.restartGeofences(getApplicationContext(), restartGeofences, new RNGeofenceHandler() {
                @Override
                public void onSuccess(String geofenceId) {
                    Log.v(TAG, "Successfully registered geofence after device restart: " + geofenceId);
                    db.removeErrorGeofence(geofenceId);
                }

                @Override
                public void onError(String geofenceId, Exception e) {
                    Log.v(TAG, "Failed geofence registration after device restart will try later: " + geofenceId);
                    db.saveErrorGeofence(geofenceId);
                    RNGeofence.schedulePeriodicWork(getApplicationContext());
                }
            });
        }
    }
}
