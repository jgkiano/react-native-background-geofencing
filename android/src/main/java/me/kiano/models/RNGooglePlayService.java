package me.kiano.models;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class RNGooglePlayService {

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }
}
