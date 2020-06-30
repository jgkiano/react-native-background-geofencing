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
import me.kiano.models.RNLocationServicesSettings;

public class RNGeofenceRestartWorker extends Worker {
    final private String TAG = "RNGeofenceRestartWorker";
    public RNGeofenceRestartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.v(TAG, "Starting periodic work..");
    }

    @NonNull
    @Override
    public Result doWork() {
        final RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
        ArrayList<RNGeofence> geofences = db.getAllErrorGeofences();
        if (geofences.isEmpty()) {
            Log.v(TAG, "No more error geofences to register, canceling job");
            RNGeofence.cancelPeriodicWork(getApplicationContext());
        } else if (RNLocationServicesSettings.isLocationServicesEnabled(getApplicationContext()) && RNLocationServicesSettings.hasLocationPermission(getApplicationContext())) {
            RNGeofence.restartGeofences(getApplicationContext(), geofences, new RNGeofenceHandler() {
                @Override
                public void onSuccess(String geofenceId) {
                    db.removeErrorGeofence(geofenceId);
                    Log.v(TAG, "Successfully re-registered error geofence: " + geofenceId);
                }
                @Override
                public void onError(String geofenceId, Exception e) {
                    Log.v(TAG, "Failed to re-register error geofence: " + geofenceId);
                }
            });
        }
        return Result.success();
    }
}
