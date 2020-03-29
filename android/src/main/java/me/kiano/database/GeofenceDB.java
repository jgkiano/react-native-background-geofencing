package me.kiano.database;

import android.content.Context;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GeofenceDB {
    public GeofenceDB(Context context) {
        Hawk.init(context).build();
    }

    private String STORAGE_KEY = "@savedGeofences";

    private String TAG = "GeofenceDB";

    public void saveGeofence (HashMap<String, Object> geofence) {
        try {
            ArrayList<HashMap<String, Object>> savedGeofences = new ArrayList<>();
            savedGeofences = Hawk.get(STORAGE_KEY, savedGeofences);
            Iterator<HashMap<String, Object>> iterator = savedGeofences.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> savedGeofence = iterator.next();
                if (savedGeofence.get("id") == geofence.get("id")) {
                    savedGeofences.remove(savedGeofence);
                }
            }
            savedGeofences.add(geofence);
            Hawk.put(STORAGE_KEY, savedGeofences);
            Log.v(TAG, "Successfully saved geofence to DB!");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
