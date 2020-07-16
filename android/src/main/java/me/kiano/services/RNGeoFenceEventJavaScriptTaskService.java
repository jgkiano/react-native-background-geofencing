package me.kiano.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import javax.annotation.Nullable;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.Constant;
import me.kiano.models.RNHelper;
import me.kiano.models.RNNotification;

public class RNGeoFenceEventJavaScriptTaskService extends HeadlessJsTaskService {

    @Override
    public void onCreate() {
        super.onCreate();
        // get the current sdk version
        int sdk = android.os.Build.VERSION.SDK_INT;

        if (!RNHelper.isAppOnForeground(getApplicationContext()) && sdk >= Build.VERSION_CODES.O) {
            RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
            RNNotification rnNotification = db.getNotification();
            if (rnNotification != null) {
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                Notification notification = rnNotification.getNotification(notificationManager, getApplicationContext());
                startForeground(Constant.RN_JS_FOREGROUND_SERVICE_ID, notification);
            }
        }
    }

    @Override
    protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            return new HeadlessJsTaskConfig(
                    Constant.RN_JS_FOREGROUND_SERVICE_TASK_KEY,
                    Arguments.fromBundle(extras),
                    Constant.RN_JS_FOREGROUND_SERVICE_TIMEOUT, // timeout for the task
                    true // optional: defines whether or not  the task is allowed in foreground. Default is false
            );
        }
        return null;
    }
}
