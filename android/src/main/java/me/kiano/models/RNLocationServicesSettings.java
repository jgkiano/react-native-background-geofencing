package me.kiano.models;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

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

import me.kiano.interfaces.RNLocationServicesRequestHandler;

public class RNLocationServicesSettings {

    private String TAG = "RNLocationServicesSettings";
    private Activity activity;
    private static final int REQUEST_SETTINGS_SINGLE_UPDATE = 11403;
    private LocationSettingsRequest locationSettingsRequest = buildLocationSettingsRequest();
    private SettingsClient settingsClient;
    final Handler handler = new Handler();

    public RNLocationServicesSettings(Activity activity, ReactApplicationContext context, final RNLocationServicesRequestHandler locationServicesRequestHandler) {
        this.activity = activity;
        this.settingsClient = LocationServices.getSettingsClient(context);
        ActivityEventListener activityEventListener = new ActivityEventListener() {
            boolean invokedHandler = false;
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                Log.v(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
                if (requestCode == REQUEST_SETTINGS_SINGLE_UPDATE) {
                    if (!invokedHandler) {
                        if (resultCode == Activity.RESULT_OK) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    locationServicesRequestHandler.onSuccess();
                                }
                            }, 5000);
                            Log.v(TAG, "Location services enabled: " + resultCode);
                        } else {
                            locationServicesRequestHandler.onError();
                            Log.v(TAG, "Location services unavailable: " + resultCode);
                        }
                        invokedHandler = true;
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
                        resolvable.startResolutionForResult(activity, REQUEST_SETTINGS_SINGLE_UPDATE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showLocationServicesRequestDialog() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                onLocationSettingsResponse(task);
            }
        });
    }

}
