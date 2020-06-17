package me.kiano.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

import me.kiano.database.RNGeofenceDB;

public class RNNotification {
    private String title;
    private String text;
    private int importance;
    private String channelId;
    private String channelName;
    private String channelDescription;

    private static String DEFAULT_TITLE = "";
    private static String DEFAULT_TEXT = "";
    private static int DEFAULT_IMPORTANCE = 3;
    private static String DEFAULT_CHANNEL_ID = "RNNotification";
    private static String DEFAULT_CHANNEL_NAME = "RN Notification";
    private static String DEFAULT_CHANNEL_DESCRIPTION = "RN Notification";

    public RNNotification() {
        title = DEFAULT_TITLE;
        text = DEFAULT_TEXT;
        importance = DEFAULT_IMPORTANCE;
        channelId = DEFAULT_CHANNEL_ID;
        channelName = DEFAULT_CHANNEL_NAME;
        channelDescription = DEFAULT_CHANNEL_DESCRIPTION;
    }

    public RNNotification(ReadableMap notification) {
        title = notification.hasKey("title") ? notification.getString("title") : DEFAULT_TITLE;
        text = notification.hasKey("text") ? notification.getString("text") : DEFAULT_TEXT;
        importance = notification.hasKey("importance") ? notification.getInt("importance") : DEFAULT_IMPORTANCE;
        channelId = notification.hasKey("channelId") ? notification.getString("channelId") : DEFAULT_CHANNEL_ID;
        channelName = notification.hasKey("channelName") ? notification.getString("channelName") : DEFAULT_CHANNEL_NAME;
        channelDescription = notification.hasKey("channelDescription") ? notification.getString("channelDescription") : DEFAULT_CHANNEL_DESCRIPTION;
    }

    public RNNotification(JSONObject notification) throws JSONException {
        title = notification.has("title") ? notification.getString("title") : DEFAULT_TITLE;
        text = notification.has("text") ? notification.getString("text") : DEFAULT_TEXT;
        importance = notification.has("importance") ? notification.getInt("importance") : DEFAULT_IMPORTANCE;
        channelId = notification.has("channelId") ? notification.getString("channelId") : DEFAULT_CHANNEL_ID;
        channelName = notification.has("channelName") ? notification.getString("channelName") : DEFAULT_CHANNEL_NAME;
        channelDescription = notification.has("channelDescription") ? notification.getString("channelDescription") : DEFAULT_CHANNEL_DESCRIPTION;
    }

    public String toJSON() throws JSONException {
        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("text", text);
        notification.put("importance", importance);
        notification.put("channelId", channelId);
        notification.put("channelName", channelName);
        notification.put("channelDescription", channelDescription);
        return notification.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification getNotification(NotificationManager notificationManager, Context context) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);
        notificationManager.createNotificationChannel(channel);
        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(context.getApplicationInfo().icon)
                .build();
    }

    public void save (Context context) {
        RNGeofenceDB db = new RNGeofenceDB(context);
        db.saveNotification(this);
    }
}
