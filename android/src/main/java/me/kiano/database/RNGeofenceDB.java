package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.kiano.models.RNGeofence;
import me.kiano.models.RNGeofenceWebhookConfiguration;

public class RNGeofenceDB {
    private final Context context;

    public RNGeofenceDB(Context context) {
        this.context = context;
    }

    private String DB_NAME = "RNBackgroundGeofencingDB";

    private String TAG = "RNGeofenceDB";

    private String GEOFENCE_KEY_PREFIX = "RNGeofenceDB:v1:";

    private String WEBHOOK_CONFIG_KEY = "RNWebhookDB:v1:configuration";

    public void saveGeofence (RNGeofence rnGeofence) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.put(GEOFENCE_KEY_PREFIX + rnGeofence.id, rnGeofence.toJSON());
            db.close();
            Log.v(TAG, "Geofence successfully saved to DB: " + rnGeofence.id);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public ArrayList<RNGeofence> getAllGeofences () {
        ArrayList<RNGeofence> savedGeofences = new ArrayList<>();
        try {
            DB db = DBFactory.open(context, DB_NAME);
            String[] geofences = db.findKeys(GEOFENCE_KEY_PREFIX);
            for (String key: geofences) {
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
            Log.e(TAG, e.getMessage());
            return savedGeofences;
        }
    }

    public String removeGeofence(String geofenceId) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.del(GEOFENCE_KEY_PREFIX + geofenceId);
            Log.v(TAG, "Geofence removed from DB: " + geofenceId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return geofenceId;
        }
    }

    public void saveWebhookConfiguration(RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.put(WEBHOOK_CONFIG_KEY, rnGeofenceWebhookConfiguration.toJSON());
            RNGeofenceWebhookConfiguration stored = new RNGeofenceWebhookConfiguration(new JSONObject(db.get(WEBHOOK_CONFIG_KEY)));
            db.close();
            Log.v(TAG, "Geofence Webhook successfully saved to DB: " + rnGeofenceWebhookConfiguration.toJSON() + " stored: " + stored.getUrl());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public RNGeofenceWebhookConfiguration getWebhookConfiguration () throws JSONException, SnappydbException {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            RNGeofenceWebhookConfiguration rnGeofenceWebhookConfiguration = new RNGeofenceWebhookConfiguration(new JSONObject(db.get(WEBHOOK_CONFIG_KEY)));
            db.close();
            return rnGeofenceWebhookConfiguration;
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean hasWebhookConfiguration() {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            return db.exists(WEBHOOK_CONFIG_KEY);
        } catch (Exception e) {
            return false;
        }
    }
}
