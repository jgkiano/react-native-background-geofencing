package me.kiano.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.util.ArrayList;
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
                    .readTimeout(rnGeofenceWebhookConfiguration.getTimeout(), TimeUnit.MILLISECONDS)
                    .build();
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
            JSONObject payload = new JSONObject();
            payload.put("event", event);
            payload.put("data", jsonData);
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
