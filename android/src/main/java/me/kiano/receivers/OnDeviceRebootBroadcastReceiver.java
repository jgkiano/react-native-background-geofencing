package me.kiano.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class OnDeviceRebootBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "RNBackgroundGeofencing";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "Device restart detected");

        final Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, "We see you've restarted your device -_-", Toast.LENGTH_LONG).show();
            }
        });

//        Log.v(TAG, "Starting job from OnDeviceRebootBroadcastReceiver  -_-");
//        GeofenceDB db = new GeofenceDB(context);
//        ArrayList<RNGeofence> storedGeofences = db.getAllGeofences();
//        for (RNGeofence storedGeofence: storedGeofences) {
//            storedGeofence.start(false, new RNGeofenceHandler() {
//                @Override
//                public void onSuccess(final String geofenceId) {
//                    Log.v(TAG, "Geofence successfully reinitialised: " + geofenceId);
//                    handler.post(new Runnable() {
//                        public void run() {
//                            Toast.makeText(context, "Geofence successfully reinitialised: " + geofenceId, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//                @Override
//                public void onError(final String geofenceId, Exception e) {
//                    Log.v(TAG, "Geofence FAILED reinitialisation: " + geofenceId);
//                    Log.e(TAG, e.getMessage());
//                    handler.post(new Runnable() {
//                        public void run() {
//                            Toast.makeText(context, "Geofence successfully reinitialised: " + geofenceId, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//            });
//        }

    }
}
