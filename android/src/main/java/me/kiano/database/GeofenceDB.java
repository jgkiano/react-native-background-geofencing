package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;

import org.json.JSONObject;

import java.util.ArrayList;

import me.kiano.models.RNGeofence;

public class GeofenceDB {
    private final Context context;

    public GeofenceDB(Context context) {
        this.context = context;
    }

    private String DB_NAME = "RNBackgroundGeofencingDB";

    private String TAG = "RNGeofenceDB";

    private String KEY_PREFIX = "RNGeofenceDB:v1:";

    public void saveGeofence (RNGeofence rnGeofence) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.put(KEY_PREFIX + rnGeofence.id, rnGeofence.toJSON());
            db.close();
            Log.v(TAG, "Geofence successfully saved to DB: " + rnGeofence.id);
            getAllGeofences();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public ArrayList<RNGeofence> getAllGeofences () {
        ArrayList<RNGeofence> savedGeofences = new ArrayList<>();
        try {
            DB db = DBFactory.open(context, DB_NAME);
            String[] geofences = db.findKeys(KEY_PREFIX);
            for (String key: geofences) {
                String savedGeofenceJSON = db.get(key);
                RNGeofence rnGeofence = new RNGeofence(context, new JSONObject(savedGeofenceJSON));
                if (rnGeofence.isExpired()) {
                    remove(rnGeofence.id);
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

    public String remove(String geofenceId) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            db.del(KEY_PREFIX + geofenceId);
            Log.v(TAG, "Geofence removed from DB: " + geofenceId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return geofenceId;
        }
    }
}
