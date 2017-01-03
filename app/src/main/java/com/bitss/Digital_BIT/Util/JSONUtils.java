package com.bitss.Digital_BIT.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

    @SuppressWarnings("unchecked")
    public static <T> T get(JSONObject o, String name, T defaultValue) throws JSONException {
        if (!o.isNull(name)) {
            if (defaultValue.getClass().equals(Boolean.class)) {
                return (T) (Boolean) o.getBoolean(name);
            } else if (defaultValue.getClass().equals(Integer.class)) {
                return (T) (Integer) o.getInt(name);
            } else if (defaultValue.getClass().equals(Long.class)) {
                return (T) (Long) o.getLong(name);
            } else if (defaultValue.getClass().equals(Double.class)) {
                return (T) (Double) o.getDouble(name);
            } else if (defaultValue.getClass().equals(String.class)) {
                return (T) o.getString(name);
            } else {
                throw new JSONException("unsupported type:" + defaultValue.getClass().toString());
            }
        }
        return defaultValue;
    }

    public static <T> T gets(JSONObject o, String name, T defaultValue) {

        try {
            return get(o, name, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
