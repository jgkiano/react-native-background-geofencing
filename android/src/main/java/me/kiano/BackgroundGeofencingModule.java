package me.kiano;

import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;

import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.interfaces.RNRequestHandler;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceWebhook;
import me.kiano.models.RNGooglePlayService;
import me.kiano.models.RNLocationService;
import me.kiano.models.RNNotification;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void addGeofence(ReadableMap geoFence, final Promise promise) {
        final RNGeofence rnGeofence = new RNGeofence(getReactApplicationContext(), geoFence);
        rnGeofence.start(false, false, new RNGeofenceHandler() {
            @Override
            public void onSuccess(String geofenceId) {
                Log.v("RNBGeofencing", "Geofence successfully added");
                promise.resolve(geofenceId);
            }
            @Override
            public void onError(String geofenceId, Exception e) {
                e.printStackTrace();
                promise.reject("geofence_exception", "Failed to start geofence service for id: " + rnGeofence.id, e);
            }
        });
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(ReadableMap geofence, final Promise promise) {
        try {
            if (!RNLocationService.isLocationPermissionGranted(getReactApplicationContext())) {
                promise.reject("permission_denied", "Access fine location is not permitted");
                return;
            }

            if (!RNLocationService.isLocationServicesEnabled(getReactApplicationContext())) {
                promise.reject("location_services_disabled", "Location services are disabled");
                return;
            }

            if (!RNGooglePlayService.isGooglePlayServicesAvailable(getReactApplicationContext())) {
                promise.reject("google_play_service_unavailable", "Google play services is unavailable");
                return;
            }

            addGeofence(geofence, promise);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("geofence_exception", "Failed to start geofence service for id: " + geofence.getString("id"), e);
        }
    }

    @ReactMethod
    public void remove(String id) {
        RNGeofence.remove(getReactApplicationContext(), id);
    }

    @ReactMethod
    public void configureWebhook (ReadableMap configureWebhook, final Promise promise) {
        try {
            RNGeofenceWebhook rnGeofenceWebhook = new RNGeofenceWebhook(configureWebhook);
            rnGeofenceWebhook.save(getReactApplicationContext());
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
    public void openLocationServicesSettings(Promise promise) {
        RNLocationService.openLocationServicesSettings(getReactApplicationContext());
        promise.resolve(true);
    }

    @ReactMethod
    public void isLocationPermissionGranted(Promise promise) {
        promise.resolve(RNLocationService.isLocationPermissionGranted(getReactApplicationContext()));
    }

    @ReactMethod
    public void isLocationServicesEnabled(Promise promise) {
        promise.resolve(RNLocationService.isLocationServicesEnabled(getReactApplicationContext()));
    }

    @ReactMethod
    public void isGooglePlayServicesAvailable(Promise promise) {
        promise.resolve(RNGooglePlayService.isGooglePlayServicesAvailable(getReactApplicationContext()));
    }

    @ReactMethod
    public void requestEnableLocationServices(final Promise promise) {
        if (RNLocationService.isLocationServicesEnabled(getReactApplicationContext())) {
            promise.resolve(true);
        } else {
            RNLocationService rnLocationService = new RNLocationService(getCurrentActivity(), getReactApplicationContext());
            rnLocationService.requestEnableLocationServices(new RNRequestHandler() {
                @Override
                public void onSuccess() {
                    promise.resolve(true);
                }

                @Override
                public void onError(Exception e) {
                    promise.resolve(false);
                    e.printStackTrace();
                }
            });
        }
    }

    @ReactMethod
    public void requestEnableGooglePlayServices (final Promise promise) {
        if (RNGooglePlayService.isGooglePlayServicesAvailable(getReactApplicationContext())) {
            promise.resolve(true);
        } else {
            RNGooglePlayService rnGooglePlayService = new RNGooglePlayService(getCurrentActivity(), getReactApplicationContext());
            rnGooglePlayService.requestEnableGooglePlayServices(new RNRequestHandler() {
                @Override
                public void onSuccess() {
                    promise.resolve(true);
                }

                @Override
                public void onError(Exception e) {
                    promise.resolve(false);
                }
            });
        }
    }
}
