package me.kiano;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;

import me.kiano.database.GeofenceDB;
import me.kiano.receivers.GeofenceBroadcastReceiver;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {

    private GeofencingClient geofencingClient;

    private PendingIntent geofencePendingIntent;

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        geofencingClient = LocationServices.getGeofencingClient(getReactApplicationContext());
    }

    private GeofencingRequest getGeofencingRequest(ArrayList<Geofence> geofenceList, boolean setInitialTriggers, int setDwellTransitionType) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        if (setInitialTriggers) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | setDwellTransitionType);
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

    private void saveToDB(HashMap<String, Object> geofence, Promise promise) {
        GeofenceDB db = new GeofenceDB(getReactApplicationContext());
        db.saveGeofence(geofence);
        promise.resolve(geofence.get("id"));
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(ReadableMap geoFence, final Promise promise) {
        try {
            final ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
            final String id = geoFence.getString("id");
            final double lat = geoFence.getDouble("lat");
            final double lng = geoFence.getDouble("lng");
            final float radius = (float) geoFence.getDouble("radius");
            final long expiration = geoFence.getDouble("expiration") > 0 ? (long) geoFence.getDouble("expiration") : Geofence.NEVER_EXPIRE;
            final int notificationResponsiveness = geoFence.getInt("notificationResponsiveness");
            final int loiteringDelay = geoFence.getInt("loiteringDelay");
            final int setDwellTransitionType = geoFence.getBoolean("setDwellTransitionType") ? Geofence.GEOFENCE_TRANSITION_DWELL : 0;
            final long expirationDate = System.currentTimeMillis() + expiration;
            final boolean initialiseOnDeviceRestart = geoFence.getBoolean("initialiseOnDeviceRestart");
            final boolean setInitialTriggers = geoFence.getBoolean("setInitialTriggers");

            final HashMap<String, Object> geofenceHashMap = new HashMap<String, Object>();
            geofenceHashMap.put("id", id);
            geofenceHashMap.put("lat", lat);
            geofenceHashMap.put("lng", lng);
            geofenceHashMap.put("radius", radius);
            geofenceHashMap.put("expiration", expiration);
            geofenceHashMap.put("notificationResponsiveness", notificationResponsiveness);
            geofenceHashMap.put("loiteringDelay", loiteringDelay);
            geofenceHashMap.put("setDwellTransitionType", setDwellTransitionType);
            geofenceHashMap.put("expirationDate", expirationDate);
            geofenceHashMap.put("initialiseOnDeviceRestart", initialiseOnDeviceRestart);
            geofenceHashMap.put("setInitialTriggers", setInitialTriggers);

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(lat, lng, radius)
                    .setExpirationDuration(expiration)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | setDwellTransitionType)
                    .setLoiteringDelay(loiteringDelay)
                    .setNotificationResponsiveness(notificationResponsiveness)
                    .build();

            geofenceList.add(geofence);

            geofencingClient.addGeofences(getGeofencingRequest(geofenceList, setInitialTriggers, setDwellTransitionType), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (initialiseOnDeviceRestart) {
                                saveToDB(geofenceHashMap, promise);
                            } else {
                                promise.resolve(id);
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
