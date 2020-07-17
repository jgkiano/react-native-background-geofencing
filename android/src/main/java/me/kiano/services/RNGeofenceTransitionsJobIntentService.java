package me.kiano.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.kiano.database.RNGeofenceDB;
import me.kiano.models.Constant;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceOrigin;
import me.kiano.models.RNGeofenceTransition;
import me.kiano.models.RNGeofenceWebhook;
import me.kiano.models.RNHelper;
import me.kiano.models.RNNotification;

public class RNGeofenceTransitionsJobIntentService extends JobIntentService {

    public static final String TAG = "RNBackgroundGeofencing";

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, RNGeofenceTransitionsJobIntentService.class, Constant.RN_GEOFENCE_TRANSITION_JOB_ID, work);
        Log.v(TAG, "Geofence work successfully enqueued");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.v(TAG, "Geofence work started..");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            List<Geofence> failedGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence failedGeofence: failedGeofences) {
                RNGeofence.setFailing(failedGeofence.getRequestId(), true, getApplicationContext());
            }
            return;
        }

        RNGeofenceDB db = new RNGeofenceDB(getApplicationContext());
        RNGeofenceTransition rnGeofenceTransition = new RNGeofenceTransition(geofencingEvent);
        RNGeofenceWebhook rnGeofenceWebhook = db.getWebhookConfiguration();
        RNNotification rnNotification = db.getNotification();

        if (rnGeofenceWebhook != null) {
            scheduleOneTimeUploadWork(rnGeofenceWebhook, rnGeofenceTransition);
        }

        if (rnNotification != null && rnGeofenceTransition.getTriggeringGeofenceOrigin(getApplicationContext()).equals(RNGeofenceOrigin.RN)) {
            startHeadlessJavaScriptTask(rnGeofenceTransition, rnNotification);
        }
    }

    private void startHeadlessJavaScriptTask(RNGeofenceTransition rnGeofenceTransition, RNNotification rnNotification) {
        try {
            Intent intent = new Intent(getApplicationContext(), RNGeoFenceEventJavaScriptTaskService.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.RN_HEADLESS_JS_TRANSITION_EVENT, rnGeofenceTransition.toJSON());
            intent.putExtras(bundle);
            RNHelper.startService(intent, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleOneTimeUploadWork(RNGeofenceWebhook webhook, RNGeofenceTransition transition) {
        try {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data rnGeofenceWorkData = new Data.Builder()
                    .putString(Constant.RN_UPLOAD_WORK_GEOFENCE_TRANSITION, transition.toJSON())
                    .putString(Constant.RN_UPLOAD_WORK_WEBHOOK_CONFIG, webhook.toJSON())
                    .build();

            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(RNGeofenceWebhookWorker.class)
                    .setConstraints(constraints)
                    .setInputData(rnGeofenceWorkData)
                    .addTag(Constant.RN_ONE_TIME_WORK_TAG)
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            Constant.RN_UPLOAD_WORK_BACK_OFF_DELAY,
                            Constant.RN_UPLOAD_WORK_BACK_OFF_DELAY_TIME_UNIT
                    )
                    .build();

            WorkManager.getInstance(getApplicationContext()).enqueue(uploadWorkRequest);

            Log.v(TAG, "Geofence one-time work request queued up");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
