package me.kiano.models;

import android.content.Context;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.kiano.database.RNGeofenceDB;
import okhttp3.Headers;

public class RNGeofenceWebhook {
    private String url;
    private long timeout;
    private HashMap<String, Object> headersHashMap;
    private JSONObject meta;

    public RNGeofenceWebhook(ReadableMap configuration) throws JSONException {
        url = configuration.hasKey("url") ? configuration.getString("url") : null;
        headersHashMap = configuration.hasKey("headers") ? configuration.getMap("headers").toHashMap() : new HashMap<String, Object>();
        timeout = configuration.hasKey("timeout") ? configuration.getInt("timeout") : Constant.DEFAULT_WEBHOOK_TIMEOUT;
        meta = configuration.hasKey("meta") ? new JSONObject(configuration.getMap("meta").toHashMap()) : null;
    }

    public RNGeofenceWebhook(JSONObject configuration) throws JSONException {
        url = configuration.has("url") ? configuration.getString("url") : null;
        timeout = configuration.has("timeout") ? configuration.getLong("timeout") : Constant.DEFAULT_WEBHOOK_TIMEOUT;
        meta = configuration.has("meta") ? configuration.getJSONObject("meta") : null;
        headersHashMap = new HashMap<>();
        JSONObject headersJSONObject = configuration.has("headers") ? configuration.getJSONObject("headers") : new JSONObject();
        Iterator<String> hIterator = headersJSONObject.keys();
        while(hIterator.hasNext()) {
            String key = hIterator.next();
            headersHashMap.put(key, headersJSONObject.getString(key));
        }
    }

    public String getUrl() {
        return url;
    }

    public long getTimeout() {
        return timeout;
    }

    public void save(Context context) {
        RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(context);
        rnGeofenceDB.saveWebhookConfiguration(this);
    }

    public String toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", url);
        jsonObject.put("headers", new JSONObject(headersHashMap));
        jsonObject.put("timeout", timeout);
        jsonObject.put("meta", meta);
        if (meta != null) {
            jsonObject.put("meta", meta);
        }
        return  jsonObject.toString();
    }

    public Headers getHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        Iterator hIterator = headersHashMap.entrySet().iterator();
        while (hIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hIterator.next();
            String key = (String) mapElement.getKey();
            String value = (String) mapElement.getValue();
            if (key.toLowerCase().equals("content-type")) {
                headersHashMap.remove(key);
            } else {
                headerBuilder.add(key, value);
            }
        }
        headerBuilder.add("Content-Type", "application/json");
        return headerBuilder.build();
    }

    public JSONObject getMeta() {
        return meta;
    }
}
