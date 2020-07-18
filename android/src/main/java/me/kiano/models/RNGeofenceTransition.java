package me.kiano.models;

import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.kiano.database.RNGeofenceDB;

public class RNGeofenceTransition {
    private final String TAG = "RNGeofenceData";
    private final GeofencingEvent geofenceTransitionEvent;
    private final HashMap<Integer, String> GeofenceTransitionEventNameMap = generateGeofenceTransitionHashMap();
    public RNGeofenceTransition(GeofencingEvent geofenceTransitionEvent) {
        this.geofenceTransitionEvent = geofenceTransitionEvent;
    }

    private HashMap<Integer, String> generateGeofenceTransitionHashMap() {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(Geofence.GEOFENCE_TRANSITION_EXIT, "exit");
        map.put(Geofence.GEOFENCE_TRANSITION_DWELL, "dwell");
        map.put(Geofence.GEOFENCE_TRANSITION_ENTER, "enter");
        return map;
    }

    public String toJSON() throws JSONException {

        JSONObject payload = new JSONObject();
        JSONArray transits = new JSONArray();
        JSONObject transit = new JSONObject();

        // generate triggering ids
        ArrayList<String> geofenceTriggeringIds = new ArrayList<>();
        for (Geofence geofence : geofenceTransitionEvent.getTriggeringGeofences()) {
            geofenceTriggeringIds.add(geofence.getRequestId());
        }

        // triggering location
        Location location = geofenceTransitionEvent.getTriggeringLocation();

        // get transition date
        long transitionDate = location.getTime();

        // get gps provider
        String geoPointProvider = location.getProvider();

        // get actual geopoint
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        HashMap<String, Double> geoPoint = new HashMap<>();
        geoPoint.put("lat", lat);
        geoPoint.put("lon", lon);

        // get accuracy
        double gpsAccuracy = location.getAccuracy();

        // get transition event name
        String transitionEvent = GeofenceTransitionEventNameMap.get(geofenceTransitionEvent.getGeofenceTransition());

        // set geopoint source
        String geoPointSource = "geofence";

        // get device info
        String deviceOSName = "android";
        String deviceOSVersion = Build.VERSION.RELEASE;
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;

        // build transit
        transit.put("ids", new JSONArray(geofenceTriggeringIds));
        transit.put("transition_date", transitionDate);
        transit.put("geopoint_provider", geoPointProvider);
        transit.put("geo_point", new JSONObject(geoPoint));
        transit.put("gps_accuracy", gpsAccuracy);
        transit.put("transition_event", transitionEvent);
        transit.put("geo_point_source", geoPointSource);
        transit.put("device_os_name", deviceOSName);
        transit.put("device_os_version", deviceOSVersion);
        transit.put("device_manufacturer", deviceManufacturer);
        transit.put("device_model", deviceModel);

        // build transits
        transits.put(transit);

        // build payload
        payload.put("transits", transits);

        return payload.toString();
    }

    public String getTriggeringGeofenceOrigin(Context context) {
        Geofence geofence = geofenceTransitionEvent.getTriggeringGeofences().get(0);
        if (geofence != null) {
            RNGeofenceDB db = new RNGeofenceDB(context);
            RNGeofence rnGeofence = db.getGeofence(geofence.getRequestId());
            if (rnGeofence != null) {
                return  rnGeofence.getOrigin();
            }
        }
        return null;
    }
}
