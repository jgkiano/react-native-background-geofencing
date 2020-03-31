package me.kiano.interfaces;

public interface RNGeofenceHandler {
    public void onSuccess(String geofenceId);
    public void onError(String geofenceId, Exception e);
}
