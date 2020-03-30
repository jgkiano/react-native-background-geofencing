package me.kiano.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GeofenceDB {
    private final Context ctx;

    public GeofenceDB(Context context) {
        ctx = context;
    }

    private String DB_NAME = "RNBackgroundGeofencingDB";

    private String TAG = "GeofenceDB";

    public void saveGeofence (HashMap<String, Object> geofence) {
        try {
            DB db = DBFactory.open(ctx, DB_NAME);
            ArrayList<HashMap<String, Object>> savedGeofences = new ArrayList<>();
            savedGeofences.add(geofence);
            db.put("savedGeofences", savedGeofences);
            Log.v(TAG, "Added Geofence with id" + geofence.get("id"));
            ArrayList[] retrievedGeofences = db.getObjectArray("savedGeofences", ArrayList.class);
            Log.v(TAG, "Got Geofences" + retrievedGeofences.toString());
            db.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
