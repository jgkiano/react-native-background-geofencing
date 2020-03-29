package me.kiano;

import android.app.PendingIntent;
import android.content.Intent;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import me.kiano.receivers.GeofenceBroadcastReceiver;

public class BackgroundGeofencingModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private GeofencingClient geofencingClient;

    private ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();

    private PendingIntent geofencePendingIntent;

    public BackgroundGeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        geofencingClient = LocationServices.getGeofencingClient(getReactApplicationContext());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(getReactApplicationContext(), GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(getReactApplicationContext(), 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @Override
    public String getName() {
        return "BackgroundGeofencing";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void add(ReadableMap geoFence, Promise promise) {
        try {
            String id = geoFence.getString("id");
            Double lat = geoFence.getDouble("lat");
            Double lon = geoFence.getDouble("lon");
            Float radius = (float) geoFence.getDouble("radius");
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(id)
                    .setCircularRegion(lat, lon, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            geofenceList.add(geofence);
            promise.resolve(id);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void start(final Promise promise) {
        if (geofenceList.size() > 0) {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            promise.resolve(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            promise.reject(e);
                        }
                    });
        } else {
            // TODO: figure out error handling
            promise.reject(new Exception("No geo fences were added"));
        }
    }

}
