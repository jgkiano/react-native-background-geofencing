package me.kiano.services;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.RNGeofenceWebhookConfiguration;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RNGeofenceWebhookWorker extends Worker {

    private OkHttpClient httpClient;

    private String TAG = "GeofenceUploadWorker";

    private RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration;

    public RNGeofenceWebhookWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        try {
            RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(getApplicationContext());
            rnGeofenceWebhookConfiguration = rnGeofenceDB.getWebhookConfiguration();
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(rnGeofenceWebhookConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(rnGeofenceWebhookConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(rnGeofenceWebhookConfiguration.getTimeout(), TimeUnit.MILLISECONDS).build();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public Result doWork() {
        if (httpClient == null || rnGeofenceWebhookConfiguration == null) {
            Log.v(TAG, "Unable to call webhook. Missing configuration");
            return Result.success();
        }
        try {
            Log.v(TAG, "Started webhook work");
            String event = getInputData().getString("event");
            String data = getInputData().getString("data");
            Log.v(TAG, "Sending event: " + event);
            Log.v(TAG, data);
            ArrayList<Object> excludes = rnGeofenceWebhookConfiguration.getExclude();
            JSONObject jsonData = new JSONObject(data);
            Iterator iterator = excludes.iterator();
            while (iterator.hasNext()) {
                String item = (String) iterator.next();
                if (jsonData.has(item)) {
                    jsonData.remove(item);
                }
            }
            JSONObject geofenceEventData = new JSONObject(data);
            JSONObject payload = new JSONObject();
            JSONArray transits = new JSONArray();
            JSONObject transit = new JSONObject();

            if (geofenceEventData.has("geofenceIds")) {
                transit.put("ids", geofenceEventData.getJSONArray("geofenceIds"));
            }
            if (geofenceEventData.has("time")) {
                transit.put("transition_date", geofenceEventData.getLong("time"));
            }
            if (geofenceEventData.has("provider")) {
                transit.put("geopoint_provider", geofenceEventData.get("provider"));
            }
            if (geofenceEventData.has("lat") && geofenceEventData.has("lng")) {
                JSONObject geoPoint = new JSONObject();
                geoPoint.put("lat", geofenceEventData.getDouble("lat"));
                geoPoint.put("lon", geofenceEventData.getDouble("lng"));
                transit.put("geo_point", geoPoint);
            }
            if (geofenceEventData.has("accuracy")) {
                transit.put("gps_accuracy", (float) geofenceEventData.getDouble("accuracy"));
            }
            if (event.equals("GEOFENCE_TRANSITION_ENTER")) {
                transit.put("transition_event", "enter");
            }
            if (event.equals("GEOFENCE_TRANSITION_ERROR")) {
                transit.put("transition_event", "error");
            }
            if (event.equals("GEOFENCE_TRANSITION_EXIT")) {
                transit.put("transition_event", "exit");
            }
            if (event.equals("GEOFENCE_TRANSITION_DWELL")) {
                transit.put("transition_event", "dwell");
            }
            if (event.equals("GEOFENCE_TRANSITION_UNKNOWN")) {
                transit.put("transition_event", "unknown");
            }
            transit.put("geo_point_source", "geofence");
            transit.put("device_os_name", "android");
            transit.put("device_os_version", Build.VERSION.RELEASE);
            transit.put("device_manufacturer", Build.MANUFACTURER);
            transit.put("device_model", Build.MODEL);

            if (rnGeofenceWebhookConfiguration.getMeta() != null) {
                payload.put("meta", rnGeofenceWebhookConfiguration.getMeta());
            }

            transits.put(transit);
            payload.put("transits", transits);
            Log.v(TAG, "Sending data: ");
            Log.v(TAG, payload.toString());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());
            Request request = new Request.Builder()
                    .url(rnGeofenceWebhookConfiguration.getUrl())
                    .headers(rnGeofenceWebhookConfiguration.getHeaders())
                    .post(requestBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.v(TAG, "Request successfully sent status code: " + response.code());
            } else {
                Log.e(TAG, "Request failed with status code: " + response.code());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return Result.success();
        }
    }
}
