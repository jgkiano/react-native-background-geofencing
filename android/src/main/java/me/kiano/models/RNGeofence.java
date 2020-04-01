package me.kiano.models;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.kiano.database.GeofenceDB;
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.receivers.GeofenceBroadcastReceiver;

public class RNGeofence {
    public final String id;
    public final double lat;
    public final double lng;
    public final float radius;
    public final long expiration;
    public final long expirationDate;
    public final int notificationResponsiveness;
    public final int loiteringDelay;
    public final int dwellTransitionType;
    public final boolean initialiseOnDeviceRestart;
    public final boolean setInitialTriggers;
    private final Context context;
    private final ArrayList<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private final String TAG = "RNGeofence";

    public RNGeofence (Context context, JSONObject geoFence) throws JSONException {
        this.context = context;
        id = geoFence.getString("id");
        lat = geoFence.getDouble("lat");
        lng = geoFence.getDouble("lng");
        radius = (float) geoFence.getDouble("radius");
        expiration = geoFence.getLong("expiration");
        expirationDate = geoFence.getLong("expirationDate");
        notificationResponsiveness = geoFence.getInt("notificationResponsiveness");
        loiteringDelay = geoFence.getInt("loiteringDelay");
        dwellTransitionType = geoFence.getInt("dwellTransitionType");
        initialiseOnDeviceRestart = geoFence.getBoolean("initialiseOnDeviceRestart");
        setInitialTriggers = geoFence.getBoolean("setInitialTriggers");
        setUpRNGeofence();
    }

    public RNGeofence(Context context, ReadableMap geoFence) {
        this.context = context;
        id = geoFence.getString("id");
        lat = geoFence.getDouble("lat");
        lng = geoFence.getDouble("lng");
        radius = (float) geoFence.getDouble("radius");
        expiration = geoFence.getDouble("expiration") > 0 ? (long) geoFence.getDouble("expiration") : Geofence.NEVER_EXPIRE;
        notificationResponsiveness = geoFence.getInt("notificationResponsiveness");
        loiteringDelay = geoFence.getInt("loiteringDelay");
        dwellTransitionType = geoFence.getBoolean("setDwellTransitionType") ? Geofence.GEOFENCE_TRANSITION_DWELL : 0;
        expirationDate = expiration > Geofence.NEVER_EXPIRE ? System.currentTimeMillis() + expiration : Geofence.NEVER_EXPIRE;
        initialiseOnDeviceRestart = geoFence.getBoolean("initialiseOnDeviceRestart");
        setInitialTriggers = geoFence.getBoolean("setInitialTriggers");
        geofencingClient = LocationServices.getGeofencingClient(context);
        setUpRNGeofence();
    }

    public boolean isExpired () {
        if (System.currentTimeMillis() > expirationDate) {
            return true;
        }
        return false;
    }

    private void setUpRNGeofence() {
        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(lat, lng, radius)
                .setExpirationDuration(expiration)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | dwellTransitionType)
                .setLoiteringDelay(loiteringDelay)
                .setNotificationResponsiveness(notificationResponsiveness)
                .build();
        geofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        if (setInitialTriggers) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT | dwellTransitionType);
        }
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void saveToDB() {
        GeofenceDB db = new GeofenceDB(context);
        db.saveGeofence(this);
    }

    public void start (final Boolean save, final RNGeofenceHandler handler) {
        try {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v(TAG, "Geofence successfully added :)");
                            if(save) { saveToDB(); }
                            handler.onSuccess(id);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.v(TAG, "Geofence add failed :(");
                            handler.onError(id, e);
                        }
                    });
        } catch (Exception e) {
            handler.onError(id, e);
        }
    }

    public String toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("lat", lat);
        json.put("lng", lng);
        json.put("radius", radius);
        json.put("expiration", expiration);
        json.put("expirationDate", expirationDate);
        json.put("notificationResponsiveness", notificationResponsiveness);
        json.put("loiteringDelay", loiteringDelay);
        json.put("dwellTransitionType", dwellTransitionType);
        json.put("initialiseOnDeviceRestart", initialiseOnDeviceRestart);
        json.put("setInitialTriggers", setInitialTriggers);
        Log.v( "RNGeofenceJSON",json.toString(2));
        return json.toString();
    }
}
