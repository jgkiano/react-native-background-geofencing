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

public class RNGooglePlayService {

    private RNRequestHandler requestHandler;
    private GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    private Activity activity;
    private Context context;

    public RNGooglePlayService(Activity activity, final ReactApplicationContext context) {
        this.activity = activity;
        this.context = context;
        ActivityEventListener activityEventListener = new ActivityEventListener() {
            private boolean invokedHandler = false;
            private Handler handle = new Handler();
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                if (requestCode == Constant.ENABLE_GOOGLE_PLAY_SERVICES_REQUEST_CODE && !invokedHandler) {
                    invokedHandler = true;
                    handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (RNGooglePlayService.isGooglePlayServicesAvailable(context)) {
                                requestHandler.onSuccess();
                            } else {
                                requestHandler.onError(new Exception("User failed to enable google play services"));
                            }
                        }
                    }, Constant.SERVICE_WAIT_DELAY);
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

    public void requestEnableGooglePlayServices(final RNRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                Dialog dialog = googleAPI.getErrorDialog(activity,result,Constant.ENABLE_GOOGLE_PLAY_SERVICES_REQUEST_CODE);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        requestHandler.onError(new Exception("User failed to enable google play services"));
                    }
                });
                dialog.show();
            } else {
                // device not supported
                requestHandler.onError(new Exception("Device is not supported"));
            }
        } else {
            requestHandler.onSuccess();
        }
    }
}
