package me.kiano.models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

public class RNLocationService {
    private static String TAG = "RNLocationService";

    public static void openLocationServicesSettings(ReactApplicationContext context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivityForResult(intent, Constant.OPEN_LOCATION_SERVICES_SETTINGS_REQUEST_CODE, new Bundle());
        Log.v(TAG, "launched location services successfully");
    }
}
