package me.kiano.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import java.util.List;

import javax.annotation.Nullable;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNNotification;

public class RNGeoFenceEventJavaScriptTaskService extends HeadlessJsTaskService {

    @Override
    public void onCreate() {
        super.onCreate();
        RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
        RNNotification rnNotification = null;
        try {
            rnNotification = db.getNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isAppOnForeground(getApplicationContext()) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && rnNotification != null) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Notification notification = rnNotification.getNotification(notificationManager, getApplicationContext());
            startForeground(1, notification);
        }
    }

    @Override
    protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            return new HeadlessJsTaskConfig(
                    "OnGeoFenceEventJavaScript",
                    Arguments.fromBundle(extras),
                    30000, // timeout for the task
                    true // optional: defines whether or not  the task is allowed in foreground. Default is false
            );
        }
        return null;
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
