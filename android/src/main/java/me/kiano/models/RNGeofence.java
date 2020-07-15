package me.kiano.models;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

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
import me.kiano.services.RNGeofenceRestartWorker;

public class RNGeofence {
    public final String id;
    public final double lat;
    public final double lng;
    public final float radius;
    public final long expiration;
    public final long expirationDate;
    public final int notificationResponsiveness;
    public final int loiteringDelay;
    public final boolean registerOnDeviceRestart;
    private final Context context;
    private final ArrayList<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private final String TAG = "RNGeofence";
    private final ArrayList<Object> transitionTypes;
    private final ArrayList<Object> initialTriggerTransitionTypes;
    private boolean failing = false;

    public static void schedulePeriodicWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(RNGeofenceRestartWorker.class, Constant.RN_PERIODIC_WORK_TIME_INTERVAL, Constant.RN_PERIODIC_WORK_TIME_UNIT)
                        .addTag(Constant.RN_PERIODIC_WORK_TAG)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(Constant.RN_PERIODIC_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
        Log.v("RNGeofence", "Periodic work scheduled");
    }

    public static void cancelPeriodicWork(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(Constant.RN_PERIODIC_WORK_NAME);
        Log.v("RNGeofence", "Periodic work canceled");
    }

    public static void setFailing (String geofenceId, boolean failing, Context context) {
        RNGeofenceDB db = new RNGeofenceDB(context);
        RNGeofence geofence = db.getGeofence(geofenceId);
        if (geofence != null && geofence.failing != failing) {
            geofence.setFailing(failing);
            geofence.save();
            Log.v("RNGeofence", "Updated failing property of: " + geofence.id + " to: " + failing);
        }
    }

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
        expirationDate = expiration > Geofence.NEVER_EXPIRE ? System.currentTimeMillis() + expiration : Geofence.NEVER_EXPIRE;
        registerOnDeviceRestart = geoFence.getBoolean("registerOnDeviceRestart");
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
        registerOnDeviceRestart = geoFence.getBoolean("registerOnDeviceRestart");
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
        failing = geoFence.getBoolean("failing");
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

    private GeofencingRequest getGeofencingRequest(boolean silently) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofenceList);
        if (silently) {
            return builder.build();
        }
        int enter = initialTriggerTransitionTypes.contains("enter") ? GeofencingRequest.INITIAL_TRIGGER_ENTER : 0;
        int exit = initialTriggerTransitionTypes.contains("exit") ? GeofencingRequest.INITIAL_TRIGGER_EXIT : 0;
        int dwell = initialTriggerTransitionTypes.contains("dwell") ? GeofencingRequest.INITIAL_TRIGGER_DWELL : 0;
        builder.setInitialTrigger(enter | exit | dwell);
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

    public void start (final Boolean silently, final RNGeofenceHandler handler) {
        try {
            geofencingClient.addGeofences(getGeofencingRequest(silently), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v(TAG, "Geofence successfully added :)");
                            saveToDB();
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
        json.put("registerOnDeviceRestart", registerOnDeviceRestart);
        JSONArray transitionTypesJSONArray = new JSONArray(transitionTypes);
        json.put("transitionTypes", transitionTypesJSONArray);
        JSONArray initialTriggerTransitionTypesJSONArray = new JSONArray(initialTriggerTransitionTypes);
        json.put("initialTriggerTransitionTypes", initialTriggerTransitionTypesJSONArray);
        json.put("failing", failing);
        Log.v( "RNGeofenceJSON",json.toString(2));
        return json.toString();
    }

    public boolean getFailing() {
        return this.failing;
    }

    public void setFailing(boolean failing) {
        this.failing = failing;
    }

    public void save() {
        this.saveToDB();
    }
}
