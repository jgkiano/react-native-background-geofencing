package me.kiano.services;

import android.app.ActivityManager;
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

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import me.kiano.models.RNGeofenceData;

public class GeofenceTransitionsJobIntentService extends JobIntentService {

    public static final String TAG = "RNBackgroundGeofencing";

    public static void enqueueWork(Context context, Intent work) {
        Log.v(TAG, "GeofenceTransitionsJobIntentService called");
        enqueueWork(context, GeofenceTransitionsJobIntentService.class, 456, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.v(TAG, "GeofenceTransitionsJobIntentService work started");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        RNGeofenceData rnGeofenceData = new RNGeofenceData(geofencingEvent);
        Intent service = new Intent(getApplicationContext(), OnGeoFenceEventJavaScriptTaskService.class);
        Bundle bundle = new Bundle();
        bundle.putString("EVENT_NAME", rnGeofenceData.getEventName());
        bundle.putString("EVENT_DATA", rnGeofenceData.getEventData());
        service.putExtras(bundle);
        getApplicationContext().startService(service);
        HeadlessJsTaskService.acquireWakeLockNow(getApplicationContext());
        sendNotification(rnGeofenceData.getEventName());
//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O || isAppOnForeground(getApplicationContext())) {
//            getApplicationContext().startService(service);
//        } else {
//            getApplicationContext().startForegroundService(service);
//        }
    }

    private void sendNotification (final String message) {
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
