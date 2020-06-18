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

public class RNGeofenceRestartWorker extends Worker {
    final private String TAG = "RNGeofenceRestartWorker";
    public RNGeofenceRestartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        if (RNGeofence.hasLocationPermission(context) && RNGeofence.isLocationServicesEnabled(context)) {
            RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
            ArrayList<RNGeofence> geofences = db.getAllRestartGeofences();
            if (geofences.size() > 0) {
                for (RNGeofence geofence : geofences) {
                    geofence.start(false, false, new RNGeofenceHandler() {
                        @Override
                        public void onSuccess(String geofenceId) {
                            Log.v(TAG, "successfully re-registered: " + geofenceId);
                        }
                        @Override
                        public void onError(String geofenceId, Exception e) {
                            Log.v(TAG, "failed to re-register: " + geofenceId);
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
        return Result.success();
    }
}
