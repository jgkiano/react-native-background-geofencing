package me.kiano.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import me.kiano.models.Constant;
import me.kiano.models.RNGeofenceWebhook;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RNGeofenceWebhookWorker extends Worker {

    private String TAG = "GeofenceUploadWorker";
    private OkHttpClient httpClient;
    private RNGeofenceWebhook rnGeofenceWebhook;

    public RNGeofenceWebhookWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        try {
            String rnGeofenceJSONWebhook = getInputData().getString(Constant.RN_RN_UPLOAD_WORK_WEBHOOK_CONFIG);
            rnGeofenceWebhook = new RNGeofenceWebhook(new JSONObject(rnGeofenceJSONWebhook));
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(rnGeofenceWebhook.getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(rnGeofenceWebhook.getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(rnGeofenceWebhook.getTimeout(), TimeUnit.MILLISECONDS).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result doWork() {

        // check if we have a http client and webhook instance
        if (httpClient == null || rnGeofenceWebhook == null) {
            Log.v(TAG, "Unable to call webhook. Missing configuration");
            return Result.success();
        }

        try {
            // retrieve the geofence transition
            String rnGeofenceJSONTransition = getInputData().getString("RNGeofenceJSONTransition");

            // construct the payload object
            JSONObject payload = new JSONObject(rnGeofenceJSONTransition);

            // add in the meta key from the webhook configuration
            JSONObject meta = rnGeofenceWebhook.getMeta();
            if (meta != null) {
                payload.put("meta", meta);
            }

            // build request body
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), payload.toString());

            // build the request
            Request request = new Request.Builder()
                    .url(rnGeofenceWebhook.getUrl())
                    .headers(rnGeofenceWebhook.getHeaders())
                    .post(requestBody)
                    .build();

            // execute the http request
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                Log.v(TAG, "Request successfully sent status code: " + response.code());
            } else {
                Log.e(TAG, "Request failed with status code: " + response.code());
            }

            Log.v(TAG, "Transmitted payload: ");
            Log.v(TAG, payload.toString());
            Log.v(TAG, "Webhook configuration: ");
            Log.v(TAG, rnGeofenceWebhook.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return Result.success();
        }
    }
}
