package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;

import org.json.JSONObject;

import java.util.ArrayList;

import me.kiano.models.Constant;
import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceWebhookConfiguration;
import me.kiano.models.RNNotification;

public class RNGeofenceDB {
    private final Context context;

    public RNGeofenceDB(Context context) {
        this.context = context;
    }

    private String TAG = "RNGeofenceDB";

    public void saveGeofence(RNGeofence rnGeofence) {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            db.put(Constant.RN_GEOFENCE_KEY_PREFIX + rnGeofence.id, rnGeofence.toJSON());
            Log.v(TAG, "Geofence successfully saved to DB: " + rnGeofence.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RNGeofence> getAllGeofences() {
        ArrayList<RNGeofence> savedGeofences = new ArrayList<>();
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            String[] geofences = db.findKeys(Constant.RN_GEOFENCE_KEY_PREFIX);
            for (String key : geofences) {
                String savedGeofenceJSON = db.get(key);
                RNGeofence rnGeofence = new RNGeofence(context, new JSONObject(savedGeofenceJSON));
                if (rnGeofence.isExpired()) {
                    removeGeofence(rnGeofence.id);
                } else {
                    savedGeofences.add(rnGeofence);
                }
            }
            db.close();
            return savedGeofences;
        } catch (Exception e) {
            e.printStackTrace();
            return savedGeofences;
        }
    }

    public String removeGeofence(String geofenceId) {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            db.del(Constant.RN_GEOFENCE_KEY_PREFIX + geofenceId);
            Log.v(TAG, "Geofence successfully removed from DB: " + geofenceId);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return geofenceId;
        }
    }

    public void saveWebhookConfiguration(RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration) {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            db.put(Constant.RN_WEBHOOK_CONFIG_KEY, rnGeofenceWebhookConfiguration.toJSON());
            db.close();
            Log.v(TAG, "Geofence webhook configuration saved: " + rnGeofenceWebhookConfiguration.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RNGeofenceWebhookConfiguration getWebhookConfiguration() {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration = new RNGeofenceWebhookConfiguration(new JSONObject(db.get(Constant.RN_WEBHOOK_CONFIG_KEY)));
            db.close();
            return rnGeofenceWebhookConfiguration;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasWebhookConfiguration() {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            boolean exists = db.exists(Constant.RN_WEBHOOK_CONFIG_KEY);
            db.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveNotification(RNNotification notification) {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            db.put(Constant.RN_NOTIFICATION_CONFIG_KEY, notification.toJSON());
            db.close();
            Log.v(TAG, "Geofence notification successfully saved to DB: " + notification.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasNotificationConfiguration() {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            boolean exists = db.exists(Constant.RN_NOTIFICATION_CONFIG_KEY);
            db.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RNNotification getNotification() {
        try {
            DB db = DBFactory.open(context, Constant.RN_DB_NAME);
            RNNotification notification = new RNNotification(new JSONObject(db.get(Constant.RN_NOTIFICATION_CONFIG_KEY)));
            db.close();
            return notification;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
