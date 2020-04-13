package me.kiano.interfaces;

public interface RNGeofenceHandler {
    void onSuccess(String geofenceId);
    void onError(String geofenceId, Exception e);
}
