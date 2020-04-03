package me.kiano.models;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RNGeofenceData {
    private final String TAG = "RNGeofenceData";
    private final String ERROR_EVENT_NAME = "GEOFENCE_TRANSITION_ERROR";
    private final String ENTER_EVENT_NAME = "GEOFENCE_TRANSITION_ENTER";
    private final String EXIT_EVENT_NAME = "GEOFENCE_TRANSITION_EXIT";
    private final String DWELL_EVENT_NAME = "GEOFENCE_TRANSITION_DWELL";
    private final String UNKNOWN_EVENT_NAME = "GEOFENCE_TRANSITION_UNKNOWN";
    private final ArrayList<String> geofenceIds = new ArrayList<>();
    private GeofencingEvent geofencingEvent;
    private String eventData;
    private String eventName;

    public RNGeofenceData(GeofencingEvent geofencingEvent) {
        this.geofencingEvent = geofencingEvent;

        for (Geofence geofence : geofencingEvent.getTriggeringGeofences()) {
            geofenceIds.add(geofence.getRequestId());
        }

        if (!geofencingEvent.hasError()) {
            if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {
                eventName = EXIT_EVENT_NAME;
                eventData = generateJSONLocationPayload();
            } else if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
                eventName = ENTER_EVENT_NAME;
                eventData = generateJSONLocationPayload();
            } else if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_DWELL) {
                eventName = DWELL_EVENT_NAME;
                eventData = generateJSONLocationPayload();
            } else {
                eventName = UNKNOWN_EVENT_NAME;
                eventData = generateErrorMessage("unknown geofence event");
            }
        } else {
            eventName = ERROR_EVENT_NAME;
            eventData = generateErrorMessage(GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode()));
        }
    }

    private String generateErrorMessage(String errorMessage) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("geofenceIds", new JSONArray(geofenceIds));
            jsonObject.put("errorMessage", errorMessage);
            return jsonObject.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "{}";
        }
    }

    private String generateJSONLocationPayload() {
        try {
            Location location = geofencingEvent.getTriggeringLocation();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accuracy", location.getAccuracy());
            jsonObject.put("altitude", location.getAltitude());
            jsonObject.put("bearing", location.getBearing());
            jsonObject.put("time", location.getTime());
            jsonObject.put("provider", location.getProvider());
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            jsonObject.put("geofenceIds", new JSONArray(geofenceIds));
            return jsonObject.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "{}";
        }
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventData() {
        return eventData;
    }
}
