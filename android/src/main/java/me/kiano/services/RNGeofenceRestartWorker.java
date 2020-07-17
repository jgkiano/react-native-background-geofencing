package me.kiano.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

import me.kiano.database.RNGeofenceDB;
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGooglePlayService;
import me.kiano.models.RNLocationService;

public class RNGeofenceRestartWorker extends Worker {

    private String TAG = "RNGeofenceRestartWorker";

    public RNGeofenceRestartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.v(TAG, "Starting work schedule");
    }

    @NonNull
    @Override
    public Result doWork() {

        boolean isRestartSuccessful = restartFailedGeofences();

        if (isRestartSuccessful) {
            RNGeofence.cancelPeriodicWork(getApplicationContext());
        }

        return Result.success();
    }

    private boolean restartFailedGeofences() {
        boolean isLocationPermissionGranted = RNLocationService.isLocationPermissionGranted(getApplicationContext());
        boolean isLocationServicesEnabled = RNLocationService.isLocationServicesEnabled(getApplicationContext());
        boolean isGooglePlayServicesAvailable = RNGooglePlayService.isGooglePlayServicesAvailable(getApplicationContext());
        RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
        final ArrayList<RNGeofence> failingGeofences = new ArrayList<>();
        ArrayList<RNGeofence> geofences = db.getAllGeofences();

        // get all failing geofences
        for (RNGeofence geofence: geofences) {
            if (geofence.getFailing()) {
                failingGeofences.add(geofence);
            }
        }

        // return true if empty
        if (failingGeofences.isEmpty()) {
            return true;
        }

        // check conditions for restart
        if (isLocationPermissionGranted && isLocationServicesEnabled && isGooglePlayServicesAvailable) {
            Log.v(TAG, "All conditions met for restart attempt");

            // attempt to restart geofences silently
            for(RNGeofence failedGeofence: failingGeofences) {
                failedGeofence.start(true, true, new RNGeofenceHandler() {
                    @Override
                    public void onSuccess(String geofenceId) {
                        RNGeofence.setFailing(geofenceId, false, getApplicationContext());
                        Log.v(TAG, "Successfully restarted geofence: " + geofenceId);
                    }

                    @Override
                    public void onError(String geofenceId, Exception e) {
                        RNGeofence.setFailing(geofenceId, true, getApplicationContext());
                        Log.v(TAG, "Failed restart geofence: " + geofenceId);
                    }
                });
            }
        }

        // check later if geofence were restarted successfully
        return false;
    }
}
