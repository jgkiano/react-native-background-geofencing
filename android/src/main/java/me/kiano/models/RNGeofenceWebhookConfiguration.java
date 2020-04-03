package me.kiano.models;

import android.content.Context;

import com.facebook.react.bridge.ReadableMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.kiano.database.RNGeofenceDB;
import okhttp3.Headers;

public class RNGeofenceWebhookConfiguration {
    private String url;
    private long timeout;
    private ArrayList<Object> exclude;
    private HashMap<String, Object> headersHashMap;
    private long DEFAULT_TIMEOUT = 15000;

    public RNGeofenceWebhookConfiguration(ReadableMap configuration) {
        url = configuration.hasKey("url") ? configuration.getString("url") : null;
        headersHashMap = configuration.hasKey("headers") ? configuration.getMap("headers").toHashMap() : new HashMap<String, Object>();
        timeout = configuration.hasKey("timeout") ? configuration.getInt("timeout") : DEFAULT_TIMEOUT;
        exclude = configuration.hasKey("exclude") ? configuration.getArray("exclude").toArrayList() : new ArrayList<>();
    }

    public RNGeofenceWebhookConfiguration (JSONObject configuration) throws JSONException {
        url = configuration.has("url") ? configuration.getString("url") : null;
        timeout = configuration.has("timeout") ? configuration.getLong("timeout") : DEFAULT_TIMEOUT;
        headersHashMap = new HashMap<>();
        exclude = new ArrayList<Object>();
        JSONArray excludeJSONArray = configuration.has("exclude") ? configuration.getJSONArray("exclude") : new JSONArray();
        JSONObject headersJSONObject = configuration.has("headers") ? configuration.getJSONObject("headers") : new JSONObject();
        Iterator<String> hIterator = headersJSONObject.keys();
        while(hIterator.hasNext()) {
            String key = hIterator.next();
            headersHashMap.put(key, headersJSONObject.getString(key));
        }
        for (int i = 0; i < excludeJSONArray.length(); i++) {
            exclude.add(excludeJSONArray.getString(i));
        }
    }

    public String getUrl() {
        return url;
    }

    public long getTimeout() {
        return timeout;
    }

    public ArrayList<Object> getExclude() {
        return exclude;
    }

    public void save(Context context) {
        RNGeofenceDB rnGeofenceDB = new RNGeofenceDB(context);
        rnGeofenceDB.saveWebhookConfiguration(this);
    }

    public String toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", url);
        jsonObject.put("headers", new JSONObject(headersHashMap));
        jsonObject.put("exclude", new JSONArray(exclude));
        jsonObject.put("timeout", timeout);
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
}
