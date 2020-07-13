package me.kiano;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

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

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void add(ReadableMap geoFence, final Promise promise) {
        try {
            int permission = ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                promise.reject("permission_denied", "Access fine location is not permitted");
                return;
            }

            if (!isLocationEnabled(getReactApplicationContext())) {
                promise.reject("location_services_disabled", "Location services are disabled");
                return;
            }

            final RNGeofence rnGeofence = new RNGeofence(getReactApplicationContext(), geoFence);

            rnGeofence.start(rnGeofence.registerOnDeviceRestart, new RNGeofenceHandler() {
                @Override
                public void onSuccess(String geofenceId) {
                    promise.resolve(geofenceId);
                }
                @Override
                public void onError(String geofenceId, Exception e) {
                    promise.reject("geofence_exception", "Failed to start geofence service for id: " + rnGeofence.id, e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            promise.reject("geofence_exception", "Failed to start geofence service for id: " + geoFence.getString("id"), e);
            e.printStackTrace();
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
