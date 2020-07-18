package me.kiano.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import me.kiano.interfaces.RNRequestHandler;

public class RNLocationService {
    private static String TAG = "RNLocationService";
    private RNRequestHandler rnRequestHandler;
    private SettingsClient settingsClient;
    private LocationSettingsRequest locationSettingsRequest = buildLocationSettingsRequest();
    private Activity activity;

    public RNLocationService(Activity activity, final ReactApplicationContext context) {
        this.activity = activity;
        this.settingsClient = LocationServices.getSettingsClient(context);

        ActivityEventListener activityEventListener = new ActivityEventListener() {
            private boolean invokedHandler = false;
            private Handler handler = new Handler();

            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                Log.v(TAG, "Result received. Request code: " + requestCode + ", Result code: " + resultCode);
                if (requestCode == Constant.ENABLE_LOCATION_SERVICES_REQUEST_CODE && !invokedHandler) {
                    invokedHandler = true;
                    if (resultCode == Activity.RESULT_OK) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rnRequestHandler.onSuccess();
                            }
                        }, Constant.SERVICE_WAIT_DELAY);
                        Log.v(TAG, "Location services enabled: " + resultCode);
                    } else {
                        rnRequestHandler.onError(new Exception("User failed to enable location services"));
                    }
                }
            }

            @Override
            public void onNewIntent(Intent intent) {

            }
        };
        context.addActivityEventListener(activityEventListener);
    }

    private LocationSettingsRequest buildLocationSettingsRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        return builder.build();
    }

    private void onLocationSettingsResponse(Task<LocationSettingsResponse> task) {
        try {
            LocationSettingsResponse response = task.getResult(ApiException.class);
        } catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        resolvable.startResolutionForResult(activity, Constant.ENABLE_LOCATION_SERVICES_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void openLocationServicesSettings(ReactApplicationContext context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivityForResult(intent, Constant.OPEN_LOCATION_SERVICES_SETTINGS_REQUEST_CODE, new Bundle());
        Log.v(TAG, "launched location services successfully");
    }

    public static boolean isLocationServicesEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean isLocationPermissionGranted(Context context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public void requestEnableLocationServices(RNRequestHandler handler) {
        this.rnRequestHandler = handler;
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                onLocationSettingsResponse(task);
            }
        });
    }
}
