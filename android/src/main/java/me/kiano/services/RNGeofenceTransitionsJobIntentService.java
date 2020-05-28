package me.kiano.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofenceData;

import com.facebook.react.bridge.ReactContextBaseJavaModule;

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
        RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(getApplicationContext());

        if (rnGeofenceDB.hasWebhookConfiguration()) {
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

        if (rnGeofenceDB.hasNotificationConfiguration()) {
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

        showEventNotification();
    }

    private void showEventNotification() {
        Log.v(TAG, "Configuring notification..");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String CHANNEL_ID = "RNBackgroundGeofencingEventNotification";
        String CHANNEL_NAME = "Geofencing Events Notification";
        String CHANNEL_DESCRIPTION = "Get notified when geofence events occurs";
        int NOTIFICATION_ID = 2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, getMainActivityClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(getApplicationContext().getApplicationInfo().icon)
                .setContentTitle("You have a new geofence event to review")
                .setContentText("Tap to review")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.v(TAG, "Sent notification 🤞");
    }

    public Class getMainActivityClass() {
        String packageName = getApplicationContext().getPackageName();
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
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
