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
        if (RNLocationService.isLocationPermissionGranted(getApplicationContext()) && RNLocationService.isLocationServicesEnabled(getApplicationContext()) && RNGooglePlayService.isGooglePlayServicesAvailable(getApplicationContext())) {
            Log.v(TAG, "All conditions met for restart attempt");
            RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
            final ArrayList<String> ids = new ArrayList<>();
            ArrayList<RNGeofence> geofences = db.getAllGeofences();
            for (final RNGeofence geofence: geofences) {
                if (geofence.getFailing()) {
                    geofence.start(true, new RNGeofenceHandler() {
                        @Override
                        public void onSuccess(String geofenceId) {
                            RNGeofence.setFailing(geofenceId, false, getApplicationContext());
                            Log.v(TAG, "Successfully restarted geofence: " + geofenceId);
                        }

                        @Override
                        public void onError(String geofenceId, Exception e) {
                            RNGeofence.setFailing(geofenceId, true, getApplicationContext());
                            ids.add(geofenceId);
                            Log.v(TAG, "Failed restart geofence: " + geofenceId);
                        }
                    });
                }
            }
            if (ids.isEmpty()) {
                RNGeofence.cancelPeriodicWork(getApplicationContext());
                Log.v(TAG, "Successfully restarted all failed geofences");
            }
        } else {
            Log.v(TAG, "Conditions not met for restart attempt");
        }
        return Result.success();
    }
}
