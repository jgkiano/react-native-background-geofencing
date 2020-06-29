package me.kiano.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.PermissionListener;
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

public class RNLocationServicesSettings implements PermissionListener {

    private String TAG = "RNLocationServicesSettings";
    private Activity activity;
    private static final int REQUEST_SETTINGS_SINGLE_UPDATE = 11403;
    private static final int REQUEST_LOCATION_PERMISSION = 10007;
    private LocationSettingsRequest locationSettingsRequest = buildLocationSettingsRequest();
    private SettingsClient settingsClient;
    private Context context;
    private RNRequestHandler rnRequestHandler;

    public RNLocationServicesSettings(Activity activity, final ReactApplicationContext context) {

        this.activity = activity;
        this.context = context;
        this.settingsClient = LocationServices.getSettingsClient(context);

        ActivityEventListener activityEventListener = new ActivityEventListener() {
            boolean invokedHandler = false;
            Handler handler = new Handler();

            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                Log.v(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
                if (invokedHandler) {
                    return;
                }
                invokedHandler = true;
                switch (requestCode) {
                    case REQUEST_SETTINGS_SINGLE_UPDATE:
                        if (resultCode == Activity.RESULT_OK) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rnRequestHandler.onSuccess();
                                }
                            }, 3000);
                            Log.v(TAG, "Location services enabled: " + resultCode);
                        } else {
                            rnRequestHandler.onError();
                            Log.v(TAG, "Location services unavailable: " + resultCode);
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNewIntent(Intent intent) {

            }
        };
        context.addActivityEventListener(activityEventListener);
    }

    public static boolean hasLocationPermission(Context context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
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

    public void requestEnableLocationServices(RNRequestHandler handler) {
        this.rnRequestHandler = handler;

        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                onLocationSettingsResponse(task);
            }
        });
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }
}
