package me.kiano;

import android.app.PendingIntent;
import android.content.Intent;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import me.kiano.database.GeofenceDB;
import me.kiano.receivers.GeofenceBroadcastReceiver;
import me.kiano.models.RNGeofence;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {

    private GeofencingClient geofencingClient;

    private PendingIntent geofencePendingIntent;

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        geofencingClient = LocationServices.getGeofencingClient(getReactApplicationContext());
    }

    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofenceList, boolean setInitialTriggers, int dwellTransitionType) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        if (setInitialTriggers) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | dwellTransitionType);
        }
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(getReactApplicationContext(), GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(getReactApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void saveToDB(RNGeofence rnGeofence, Promise promise) {
        GeofenceDB db = new GeofenceDB(getReactApplicationContext());
        db.saveGeofence(rnGeofence);
        promise.resolve(rnGeofence.id);
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(ReadableMap geoFence, final Promise promise) {
        try {
            final ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
            final RNGeofence rnGeofence = new RNGeofence(geoFence);

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(rnGeofence.id)
                    .setCircularRegion(rnGeofence.lat, rnGeofence.lng, rnGeofence.radius)
                    .setExpirationDuration(rnGeofence.expiration)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | rnGeofence.dwellTransitionType)
                    .setLoiteringDelay(rnGeofence.loiteringDelay)
                    .setNotificationResponsiveness(rnGeofence.notificationResponsiveness)
                    .build();

            geofenceList.add(geofence);

            geofencingClient.addGeofences(getGeofencingRequest(geofenceList, rnGeofence.setInitialTriggers, rnGeofence.dwellTransitionType), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (rnGeofence.initialiseOnDeviceRestart) {
                                saveToDB(rnGeofence, promise);
                            } else {
                                promise.resolve(rnGeofence.id);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            promise.reject(e);
                        }
                    });
        } catch (Exception e) {
            promise.reject(e);
        }
    }

}
