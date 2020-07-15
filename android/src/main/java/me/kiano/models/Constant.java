package me.kiano.models;

import com.google.android.gms.location.Geofence;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Constant {
    // request codes
    public static int OPEN_LOCATION_SERVICES_SETTINGS_REQUEST_CODE = 1;
    public static int ENABLE_LOCATION_SERVICES_REQUEST_CODE = 2;
    public static int ENABLE_GOOGLE_PLAY_SERVICES_REQUEST_CODE = 3;

    // timers and delays
    public static int SERVICE_WAIT_DELAY = 3000;
    public static int DEFAULT_WEBHOOK_TIMEOUT = 15000;

    // db
    public static String RN_DB_NAME = "RNBackgroundGeofencingDB";
    public static String RN_GEOFENCE_KEY_PREFIX = "RNGeofenceDB:v1:";
    public static String RN_WEBHOOK_CONFIG_KEY = "RNWebhookDB:v1:configuration";
    public static String RN_NOTIFICATION_CONFIG_KEY = "RNNotificationDB:v1:configuration";

    // jobs
    public static int RN_GEOFENCE_TRANSITION_JOB_ID = 456;
    public static String RN_ONE_TIME_WORK_TAG = "RNGeofenceWork";
    public static String RN_UPLOAD_WORK_GEOFENCE_TRANSITION = "RN_UPLOAD_WORK_GEOFENCE_TRANSITION";
    public static String RN_UPLOAD_WORK_WEBHOOK_CONFIG = "RN_RN_UPLOAD_WORK_WEBHOOK_CONFIG";

    public static String RN_PERIODIC_WORK_NAME = "RNGeofenceRestartPeriodicWorker";
    public static String RN_PERIODIC_WORK_TAG = "RNGeofenceRestartPeriodicWork";
    public static TimeUnit RN_PERIODIC_WORK_TIME_UNIT = TimeUnit.MINUTES;
    public static int RN_PERIODIC_WORK_TIME_INTERVAL = 16;
}
