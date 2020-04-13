package me.kiano.models;

import android.content.Context;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

import me.kiano.database.RNGeofenceDB;

public class RNNotification {
    private String title;
    private String text;

    public RNNotification(ReadableMap notification) {
        title = notification.hasKey("title") ? notification.getString("title") : "";
        text = notification.hasKey("text") ? notification.getString("text") : "";
    }

    public RNNotification(JSONObject notification) throws JSONException {
        title = notification.has("title") ? notification.getString("title") : "";
        text = notification.has("text") ? notification.getString("text") : "";
    }

    public String toJSON() throws JSONException {
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("text", text);
        return notification.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void save (Context context) {
        RNGeofenceDB db = new RNGeofenceDB(context);
        db.saveNotification(this);
    }
}
