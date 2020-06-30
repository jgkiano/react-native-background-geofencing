package me.kiano.models;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import me.kiano.interfaces.RNRequestHandler;

public class RNGooglePlayServices {
    private Activity activity;
    private Context context;
    private GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    private int PLAY_SERVICES_RESOLUTION_REQUEST = 101;
    private RNRequestHandler handler;
    private String TAG = "RNGooglePlayServices";

    public RNGooglePlayServices(Activity activity, final ReactApplicationContext context) {
        this.activity = activity;
        this.context = context;
        ActivityEventListener activityEventListener = new ActivityEventListener() {
            boolean activityResultCalled = false;
            Handler handle = new Handler();
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST && !activityResultCalled) {
                    activityResultCalled = true;
                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (RNGooglePlayServices.isGooglePlayServicesAvailable(context)) {
                                handler.onSuccess();
                            } else {
                                handler.onError();
                            }
                        }
                    }, 3000);
                }
            }

            @Override
            public void onNewIntent(Intent intent) {

            }
        };
        context.addActivityEventListener(activityEventListener);
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    public void showGooglePlayServicesDialog(final RNRequestHandler handler) {
        this.handler = handler;
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                Dialog dialog = googleAPI.getErrorDialog(activity,result,PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        handler.onError();
                    }
                });
                dialog.show();
            } else {
                // device not supported
                handler.onError();
            }
        } else {
            handler.onSuccess();
        }
    }
}
