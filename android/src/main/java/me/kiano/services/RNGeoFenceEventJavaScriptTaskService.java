package me.kiano.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import java.util.List;

import javax.annotation.Nullable;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNNotification;

public class RNGeoFenceEventJavaScriptTaskService extends HeadlessJsTaskService {

    private static final String CHANNEL_ID = "RNBackgroundGeofencing";

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

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "RNBackgroundGeofencing", importance);
            channel.setDescription("Background geofencing service");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(rnNotification.getTitle())
                    .setContentText(rnNotification.getText())
                    .setSmallIcon(getApplicationContext().getApplicationInfo().icon)
                    .build();

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
