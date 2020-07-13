package me.kiano.models;

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
}
