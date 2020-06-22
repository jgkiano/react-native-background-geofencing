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
import me.kiano.models.RNNotification;

public class RNGeofenceDB {
    private final Context context;

    public RNGeofenceDB(Context context) {
        this.context = context;
    }

    private String DB_NAME = "RNBackgroundGeofencingDB";

    private String TAG = "RNGeofenceDB";

    private String GEOFENCE_KEY_PREFIX = "RNGeofenceDB:v1:";

    private String WEBHOOK_CONFIG_KEY = "RNWebhookDB:v1:configuration";

    private String NOTIFICATION_CONFIG_KEY = "RNNotificationDB:v1:configuration";

    private String ERROR_GEOFENCES_PREFIX = "RNErrorGeofenceDB:v1:";

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

    public RNGeofence getGeofence(String id) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            String geofenceKey = GEOFENCE_KEY_PREFIX + id;
            if (db.exists(geofenceKey)) {
                String savedGeofenceJSON = db.get(geofenceKey);
                return new RNGeofence(context, new JSONObject(savedGeofenceJSON));
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public String removeGeofence(String geofenceId) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.del(GEOFENCE_KEY_PREFIX + geofenceId);
            Log.v(TAG, "Geofence removed from DB: " + geofenceId);
            db.close();
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
            db.close();
            Log.v(TAG, "Geofence Webhook successfully saved to DB: " + rnGeofenceWebhookConfiguration.toJSON() + " stored: " + rnGeofenceWebhookConfiguration.getUrl());
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

    public void saveNotification (RNNotification notification) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.put(NOTIFICATION_CONFIG_KEY, notification.toJSON());
            db.close();
            Log.v(TAG, "Geofence notification successfully saved to DB: " + notification.toJSON() + " stored: " + notification.getText());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public boolean hasNotificationConfiguration() {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            boolean result = db.exists(NOTIFICATION_CONFIG_KEY);
            db.close();
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public RNNotification getNotification () throws JSONException, SnappydbException {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            RNNotification notification = new RNNotification(new JSONObject(db.get(NOTIFICATION_CONFIG_KEY)));
            db.close();
            return notification;
        } catch (Exception e) {
            throw e;
        }
    }

    public void saveErrorGeofence(String id) {
        try {
            RNGeofence geofence = getGeofence(id);
            if (geofence != null) {
                DB db = DBFactory.open(context, DB_NAME);
                db.put(ERROR_GEOFENCES_PREFIX + geofence.id, geofence.toJSON());
                db.close();
                Log.v(TAG, "Geofence error successfully saved to DB: " + geofence.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RNGeofence> getAllErrorGeofences() {
        ArrayList<RNGeofence> geofences = new ArrayList<>();
        try {
            DB db = DBFactory.open(context, DB_NAME);
            String[] keys = db.findKeys(ERROR_GEOFENCES_PREFIX);
            for(String key: keys) {
                String savedGeofenceJSON = db.get(key);
                RNGeofence geofence = new RNGeofence(context, new JSONObject(savedGeofenceJSON));
                if (geofence.isExpired()) {
                    removeErrorGeofence(geofence.id);
                } else {
                    geofences.add(geofence);
                }
            }
            return geofences;
        } catch (Exception e) {
            e.printStackTrace();
            return geofences;
        }
    }

    public void removeErrorGeofence(String id) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.del(ERROR_GEOFENCES_PREFIX + id);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAllErrorGeofence() {
        try {
            ArrayList<RNGeofence> geofences = getAllErrorGeofences();
            if (!geofences.isEmpty()) {
                for(RNGeofence geofence: geofences) {
                    removeErrorGeofence(geofence.id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
