package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.kiano.models.RNGeofence;

public class GeofenceDB {
    private final Context context;

    public GeofenceDB(Context context) {
        this.context = context;
    }

    private String DB_NAME = "RNBackgroundGeofencingDB";

    private String DB_KEY_NAME = "RNGeofences";

    private String TAG = "GeofenceDB";

    public void saveGeofence (RNGeofence rnGeofence) {
        try {
            DB db = DBFactory.open(context, DB_NAME);
            List<RNGeofence> rnGeofences = new ArrayList<>();
            if(db.exists(DB_KEY_NAME)) {
                ArrayList<RNGeofence> savedRNGeofences = (ArrayList<RNGeofence>) db.getObject(DB_KEY_NAME, ArrayList.class);
                Log.v(TAG, "DB has values");
                Iterator iterator = savedRNGeofences.iterator();
                while (iterator.hasNext()) {
                    RNGeofence currentRNGeofence = (RNGeofence) iterator.next();
                    if (currentRNGeofence.id == rnGeofence.id) {
                        Log.v(TAG, "DB already has geopoint with existing id: " + rnGeofence.id);
                        savedRNGeofences.remove(currentRNGeofence);
                    }
                }
                savedRNGeofences.add(rnGeofence);
                rnGeofences = savedRNGeofences;
            } else {
                rnGeofences.add(rnGeofence);
            }
            Log.v(TAG, "DB new size: " + rnGeofences.size());
            db.put(DB_KEY_NAME, rnGeofences);
            db.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
