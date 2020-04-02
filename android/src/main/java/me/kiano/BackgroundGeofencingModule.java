package me.kiano;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceWebhookConfiguration;

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
            rnGeofence.start(rnGeofence.registerOnDeviceRestart, new RNGeofenceHandler() {
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

    @ReactMethod
    public void configureWebhook (ReadableMap configureWebhook, final Promise promise) {
        RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration = new RNGeofenceWebhookConfiguration(configureWebhook);
        rnGeofenceWebhookConfiguration.save(getReactApplicationContext());
        promise.resolve(true);
    }

}
