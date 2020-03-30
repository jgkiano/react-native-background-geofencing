package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;

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
            db.put(KEY_PREFIX + rnGeofence.id, rnGeofence);
            db.close();
            Log.v(TAG, "Geofence successfully saved to DB: " + rnGeofence);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
