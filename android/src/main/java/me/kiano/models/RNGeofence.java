package me.kiano.models;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.kiano.database.RNGeofenceDB;
import me.kiano.interfaces.RNGeofenceHandler;
import me.kiano.receivers.RNGeofenceBroadcastReceiver;

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
    public final boolean registerOnDeviceRestart;
    public final boolean setInitialTriggers;
    private final Context context;
    private final ArrayList<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private final String TAG = "RNGeofence";
    private final ArrayList<Object> transitionTypes;
    private final ArrayList<Object> initialTriggerTransitionTypes;

    public static void remove(Context context,String id) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        List<String> ids = new ArrayList<String>();
        RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(context);
        ids.add(id);
        geofencingClient.removeGeofences(ids);
        rnGeofenceDB.removeGeofence(id);
        Log.v("RNGeofence", "Geofence successfully removed from client and DB :)");
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
        registerOnDeviceRestart = geoFence.getBoolean("registerOnDeviceRestart");
        setInitialTriggers = geoFence.getBoolean("setInitialTriggers");
        transitionTypes = geoFence.getArray("transitionTypes").toArrayList();
        initialTriggerTransitionTypes = geoFence.getArray("initialTriggerTransitionTypes").toArrayList();
        setUpRNGeofence();
    }

    public RNGeofence (Context context, JSONObject geoFence) throws JSONException {
        this.context = context;
        Log.v(TAG, geoFence.toString(2));
        id = geoFence.getString("id");
        lat = geoFence.getDouble("lat");
        lng = geoFence.getDouble("lng");
        radius = (float) geoFence.getDouble("radius");
        expiration = geoFence.getLong("expiration");
        expirationDate = geoFence.getLong("expirationDate");
        notificationResponsiveness = geoFence.getInt("notificationResponsiveness");
        loiteringDelay = geoFence.getInt("loiteringDelay");
        dwellTransitionType = geoFence.getInt("dwellTransitionType");
        registerOnDeviceRestart = geoFence.getBoolean("registerOnDeviceRestart");
        setInitialTriggers = geoFence.getBoolean("setInitialTriggers");
        JSONArray transitionTypesJSONArray = geoFence.getJSONArray("transitionTypes");
        transitionTypes = new ArrayList<>();
        for (int i = 0; i < transitionTypesJSONArray.length(); i++) {
            transitionTypes.add(transitionTypesJSONArray.getString(i));
        }
        JSONArray initialTriggerTransitionTypesJSONArray = geoFence.getJSONArray("initialTriggerTransitionTypes");
        initialTriggerTransitionTypes = new ArrayList<>();
        for (int i = 0; i < initialTriggerTransitionTypesJSONArray.length(); i++) {
            initialTriggerTransitionTypes.add(initialTriggerTransitionTypesJSONArray.getString(i));
        }
        setUpRNGeofence();
    }

    public boolean isExpired () {
        if (expirationDate > 0 && System.currentTimeMillis() > expirationDate) {
            return true;
        }
        return false;
    }

    private void setUpRNGeofence() {
        final int enter = transitionTypes.contains("enter") ? Geofence.GEOFENCE_TRANSITION_ENTER : 0;
        final int exit = transitionTypes.contains("exit") ? Geofence.GEOFENCE_TRANSITION_EXIT : 0;
        final int dwell = transitionTypes.contains("dwell") ? Geofence.GEOFENCE_TRANSITION_DWELL : 0;
        geofencingClient = LocationServices.getGeofencingClient(context);
        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(lat, lng, radius)
                .setExpirationDuration(expiration)
                .setTransitionTypes(enter | exit | dwell)
                .setLoiteringDelay(loiteringDelay)
                .setNotificationResponsiveness(notificationResponsiveness)
                .build();
        geofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {
        final int enter = initialTriggerTransitionTypes.contains("enter") ? GeofencingRequest.INITIAL_TRIGGER_ENTER : 0;
        final int exit = initialTriggerTransitionTypes.contains("exit") ? GeofencingRequest.INITIAL_TRIGGER_EXIT : 0;
        final int dwell = initialTriggerTransitionTypes.contains("dwell") ? GeofencingRequest.INITIAL_TRIGGER_DWELL : 0;
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(enter | exit | dwell);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, RNGeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void saveToDB() {
        RNGeofenceDB db = new RNGeofenceDB(context);
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
                            Log.v(TAG, e.getMessage());
                            e.printStackTrace();
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
        json.put("registerOnDeviceRestart", registerOnDeviceRestart);
        json.put("setInitialTriggers", setInitialTriggers);
        JSONArray transitionTypesJSONArray = new JSONArray(transitionTypes);
        json.put("transitionTypes", transitionTypesJSONArray);
        JSONArray initialTriggerTransitionTypesJSONArray = new JSONArray(initialTriggerTransitionTypes);
        json.put("initialTriggerTransitionTypes", initialTriggerTransitionTypesJSONArray);
        Log.v( "RNGeofenceJSON",json.toString(2));
        return json.toString();
    }
}
