package me.kiano;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;

import java.util.ArrayList;

import me.kiano.database.RNGeofenceDB;
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.interfaces.RNLocationServicesRequestHandler;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceWebhookConfiguration;
import me.kiano.models.RNLocationServicesSettings;
import me.kiano.models.RNNotification;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {
    private String TAG = "BackgroundGeofencing";

    private RNGeofenceDB db;

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.db = new RNGeofenceDB(reactContext);
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(final ReadableMap geoFence, final Promise promise) {
        try {

            if (!RNGeofence.hasLocationPermission(getReactApplicationContext())) {
                promise.reject("permission_denied", "Access fine location is not permitted");
                return;
            }

            if (!RNGeofence.isLocationServicesEnabled(getReactApplicationContext())) {
                RNLocationServicesSettings rnLocationServicesSettings = new RNLocationServicesSettings(getCurrentActivity(), getReactApplicationContext(), new RNLocationServicesRequestHandler() {
                    @Override
                    public void onSuccess() {
                        addGeofences(geoFence, promise);
                    }

                    @Override
                    public void onError() {
                        promise.reject("location_services_disabled", "Location services are disabled");
                        return;
                    }
                });
                rnLocationServicesSettings.showLocationServicesRequestDialog();
            } else {
                addGeofences(geoFence, promise);
            }
        } catch (Exception e) {
            promise.reject("geofence_exception", "Failed to start geofence service for id: " + geoFence.getString("id"), e);
        }
    }

    private void addGeofences(ReadableMap geoFence, final Promise promise) {
        final RNGeofence rnGeofence = new RNGeofence(getReactApplicationContext(), geoFence);
        rnGeofence.start(true, rnGeofence.setInitialTriggers, new RNGeofenceHandler() {
            @Override
            public void onSuccess(String geofenceId) {
                promise.resolve(geofenceId);
            }
            @Override
            public void onError(String geofenceId, Exception e) {
                promise.reject("geofence_exception", "Failed to start geofence service for id: " + rnGeofence.id, e);
            }
        });
    }

    @ReactMethod
    public void remove(String id) {
        RNGeofence.remove(getReactApplicationContext(), id);
    }

    @ReactMethod
    public void configureWebhook (ReadableMap configureWebhook, final Promise promise) {
        try {
            RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration = new RNGeofenceWebhookConfiguration(configureWebhook);
            rnGeofenceWebhookConfiguration.save(getReactApplicationContext());
            promise.resolve(true);
        } catch (JSONException e) {
            promise.reject("geofence_exception", e.getMessage());
            e.printStackTrace();
        }

    }

    @ReactMethod
    public void configureNotification (ReadableMap configureNotification, final Promise promise) {
        RNNotification notification = new RNNotification(configureNotification);
        notification.save(getReactApplicationContext());
        promise.resolve(true);
    }

    @ReactMethod
    public void configure(ReadableMap configuration, final Promise promise) {
        try {
            if (configuration.hasKey("notification")) {
                ReadableMap notification = configuration.getMap("notification");
                RNNotification rnNotification = new RNNotification(notification);
                rnNotification.save(getReactApplicationContext());
            } else {
                RNNotification rnNotification = new RNNotification();
                rnNotification.save(getReactApplicationContext());
            }
            if (configuration.hasKey("webhook")) {
                ReadableMap webhook = configuration.getMap("webhook");
                RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration = new RNGeofenceWebhookConfiguration(webhook);
                rnGeofenceWebhookConfiguration.save(getReactApplicationContext());
            }
            if (configuration.hasKey("jsTask")) {
                db.saveJSTask();
            }
            promise.resolve(true);
        } catch (JSONException e) {
            promise.reject("geofence_exception", e.getMessage());
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void hasLocationPermission(Promise promise) {
        promise.resolve(RNGeofence.hasLocationPermission(getReactApplicationContext()));
    }

    @ReactMethod
    public void isLocationServicesEnabled(Promise promise) {
        promise.resolve(RNGeofence.isLocationServicesEnabled(getReactApplicationContext()));
    }

    @ReactMethod
    public void init() {
        ArrayList<RNGeofence> geofences = db.getAllErrorGeofences();

        if (geofences.isEmpty()) {
            Log.v(TAG, "No bad geofences yet, cancelling any existing jobs");
            RNGeofence.cancelPeriodicWork(getReactApplicationContext());
            return;
        }

        if (RNGeofence.hasLocationPermission(getReactApplicationContext()) && RNGeofence.isLocationServicesEnabled(getReactApplicationContext())) {
            RNGeofence.restartGeofences(getReactApplicationContext(), geofences, new RNGeofenceHandler() {
                @Override
                public void onSuccess(final String geofenceId) {
                    Log.v(TAG, "Successfully re-registered geofence in init: " + geofenceId);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            db.removeErrorGeofence(geofenceId);
                        }
                    }, 10000);   //10 seconds
                }

                @Override
                public void onError(final String geofenceId, Exception e) {
                    Log.v(TAG, "Failed to re-register geofence in init will try later: " + geofenceId);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            db.saveErrorGeofence(geofenceId);
                            RNGeofence.schedulePeriodicWork(getReactApplicationContext());
                        }
                    }, 10000);   //10 seconds
                    e.printStackTrace();
                }
            });
        }
    }

    @ReactMethod
    public void restart() {
        if (RNGeofence.hasLocationPermission(getReactApplicationContext()) && RNGeofence.isLocationServicesEnabled(getReactApplicationContext())) {
            ArrayList<RNGeofence> geofences = db.getAllGeofences();
            if (geofences.isEmpty()) {
                return;
            }
            for(RNGeofence geofence: geofences) {
                geofence.start(true, geofence.setInitialTriggers, new RNGeofenceHandler() {
                    @Override
                    public void onSuccess(String geofenceId) {
                        Log.v(TAG, "Successfully restarted geofence: " + geofenceId);
                        db.removeErrorGeofence(geofenceId);
                    }

                    @Override
                    public void onError(String geofenceId, Exception e) {
                        Log.v(TAG, "Failed to restart geofence: " + geofenceId + " Will try later");
                        db.saveErrorGeofence(geofenceId);
                        RNGeofence.schedulePeriodicWork(getReactApplicationContext());
                    }
                });
            }
        }
    }

    @ReactMethod
    public void openLocationServicesSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        getReactApplicationContext().startActivityForResult(intent, 516, new Bundle());
    }

}
