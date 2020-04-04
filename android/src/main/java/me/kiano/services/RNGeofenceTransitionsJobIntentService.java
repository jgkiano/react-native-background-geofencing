package me.kiano.services;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.GeofencingEvent;

import java.util.concurrent.TimeUnit;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofenceData;

public class RNGeofenceTransitionsJobIntentService extends JobIntentService {

    public static final String TAG = "RNBackgroundGeofencing";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "GeofenceTransitionsJobIntentService called");
        enqueueWork(context, RNGeofenceTransitionsJobIntentService.class, 456, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.v(TAG, "GeofenceTransitionsJobIntentService work started");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        RNGeofenceData rnGeofenceData = new RNGeofenceData(geofencingEvent);
        Intent service = new Intent(getApplicationContext(), RNGeoFenceEventJavaScriptTaskService.class);
        Bundle bundle = new Bundle();
        RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(getApplicationContext());
        bundle.putString("event", rnGeofenceData.getEventName());
        bundle.putString("data", rnGeofenceData.getEventData());
        Log.v(TAG, "Geofence transition: " + rnGeofenceData.getEventName());
        Log.v(TAG, rnGeofenceData.getEventData());
        service.putExtras(bundle);
        getApplicationContext().startService(service);
        HeadlessJsTaskService.acquireWakeLockNow(getApplicationContext());

        if (!rnGeofenceDB.hasWebhookConfiguration()) {
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data rnGeofenceWorkData = new Data.Builder()
                .putString("event", rnGeofenceData.getEventName())
                .putString("data", rnGeofenceData.getEventData())
                .build();

        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(RNGeofenceWebhookWorker.class)
                .setConstraints(constraints)
                .setInputData(rnGeofenceWorkData)
                .addTag("RNGeofenceWork")
                .setInitialDelay(1, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);
    }
}
