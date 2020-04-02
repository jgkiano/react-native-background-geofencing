package me.kiano.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeofenceWebhookWorker extends Worker {

    private final OkHttpClient httpClient = new OkHttpClient();

    private String TAG = "GeofenceUploadWorker";

    public GeofenceWebhookWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        Log.v(TAG, "Doing some nice work");
        String EVENT_NAME = getInputData().getString("EVENT_NAME");
        String EVENT_DATA = getInputData().getString("EVENT_DATA");
        Log.v(TAG, "EVENT NAME: " + EVENT_NAME);
        Log.v(TAG, EVENT_DATA);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                EVENT_DATA
        );

        Request request = new Request.Builder()
                .url("http://192.168.100.190:4000/geofence")
                .addHeader("Kiano", "Man")
                .post(body)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.e(TAG, "Request successfully sent status code: " + response.code());
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
