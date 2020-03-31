package me.kiano.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class ReinitialiseGeofencesJobIntentService extends JobIntentService {
    private static String TAG = "RNReinitialiseGeofencesJobIntentService";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "GeofenceTransitionsJobIntentService called");
        enqueueWork(context, ReinitialiseGeofencesJobIntentService.class, 789, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.v(TAG, "I should be handling some work right now -_-");
    }
}
