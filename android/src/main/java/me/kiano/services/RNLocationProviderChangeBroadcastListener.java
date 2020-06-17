package me.kiano.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNNotification;

public class RNLocationProviderChangeBroadcastListener extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
            RNNotification rnNotification = null;
            try {
                rnNotification = db.getNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (rnNotification != null) {
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                Notification notification = rnNotification.getNotification(notificationManager, getApplicationContext());
                startForeground(1, notification);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
            private boolean isLocationEnabled;
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                    if (isLocationEnabled != RNGeofence.isLocationEnabled(context)) {
                        isLocationEnabled = RNGeofence.isLocationEnabled(context);
                        Toast.makeText(context, "Status change: " + isLocationEnabled, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        getApplicationContext().registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopService(intent);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
