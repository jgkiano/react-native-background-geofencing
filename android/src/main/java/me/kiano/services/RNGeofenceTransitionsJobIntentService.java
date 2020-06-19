package me.kiano.services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofence;
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
        RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();
            for(Geofence geofence: geofences) {
                db.saveErrorGeofence(geofence.getRequestId());
            }
            RNGeofence.schedulePeriodicWork(getApplicationContext());
            return;
        }

        RNGeofenceData rnGeofenceData = new RNGeofenceData(geofencingEvent);

        if (db.hasWebhookConfiguration()) {
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
            Log.v(TAG, "Geofence work request queued up");
        }

        if (db.hasNotificationConfiguration()) {
            Intent service = new Intent(getApplicationContext(), RNGeoFenceEventJavaScriptTaskService.class);
            Bundle bundle = new Bundle();
            bundle.putString("event", rnGeofenceData.getEventName());
            bundle.putString("data", rnGeofenceData.getEventData());
            Log.v(TAG, "Geofence transition: " + rnGeofenceData.getEventName());
            Log.v(TAG, rnGeofenceData.getEventData());
            service.putExtras(bundle);
            if (!isAppOnForeground(getApplicationContext()) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(service);
            } else {
                getApplicationContext().startService(service);
            }
            HeadlessJsTaskService.acquireWakeLockNow(getApplicationContext());
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
