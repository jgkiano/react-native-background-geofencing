package me.kiano.models;

import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;

public class RNGeofence {
    public final String id;
    public final double lat;
    public final double lng;
    public final float radius;
    public final long expiration;
    public final long expirationDate;
    public final int notificationResponsiveness;
    public final int loiteringDelay;
    public final int dwellTransitionType;
    public final boolean initialiseOnDeviceRestart;
    public final boolean setInitialTriggers;

    public RNGeofence(ReadableMap geoFence) {
        id = geoFence.getString("id");
        lat = geoFence.getDouble("lat");
        lng = geoFence.getDouble("lng");
        radius = (float) geoFence.getDouble("radius");
        expiration = geoFence.getDouble("expiration") > 0 ? (long) geoFence.getDouble("expiration") : Geofence.NEVER_EXPIRE;
        notificationResponsiveness = geoFence.getInt("notificationResponsiveness");
        loiteringDelay = geoFence.getInt("loiteringDelay");
        dwellTransitionType = geoFence.getBoolean("setDwellTransitionType") ? Geofence.GEOFENCE_TRANSITION_DWELL : 0;
        expirationDate = System.currentTimeMillis() + expiration;
        initialiseOnDeviceRestart = geoFence.getBoolean("initialiseOnDeviceRestart");
        setInitialTriggers = geoFence.getBoolean("setInitialTriggers");
    }
}
