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
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.receivers.GeofenceBroadcastReceiver;
import me.kiano.models.RNGeofence;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(ReadableMap geoFence, final Promise promise) {
        try {
            final RNGeofence rnGeofence = new RNGeofence(getReactApplicationContext(), geoFence);
            rnGeofence.start(rnGeofence.initialiseOnDeviceRestart, new RNGeofenceHandler() {
                @Override
                public void onSuccess(String geofenceId) {
                    promise.resolve(geofenceId);
                }
                @Override
                public void onError(String geofenceId, Exception e) {
                    promise.reject("geofence_exception", "Failed to start geofence service for id: " + rnGeofence.id, e);
                }
            });
        } catch (Exception e) {
            promise.reject("geofence_exception", "Failed to start geofence service for id: " + geoFence.getString("id"), e);
        }
    }

}
