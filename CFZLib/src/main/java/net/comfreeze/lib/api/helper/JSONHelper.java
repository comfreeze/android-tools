package net.comfreeze.lib.api.helper;

import android.os.Bundle;
import android.util.Log;

import net.comfreeze.lib.BundleBuilder;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.custom.JSONArray;
import org.json.custom.JSONException;
import org.json.custom.JSONObject;

public abstract class JSONHelper {
    public static final String TAG = JSONHelper.class.getSimpleName();
    public static final String FIELD_OUTER_KEY = "outer_key";
    public static boolean silent = true;

    public JSONHelper() {
    }

    public static JSONObject generateObject(String source) {
        if (null != source) {
            JSONObject output;
            try {
                output = new JSONObject(source);
                return output;
            } catch (JSONException e) {
                if (!silent)
                    Log.e(TAG, "JSONException", e);
            }
        }
        return new JSONObject();
    }

    public static JSONArray generateArray(String source) {
        if (null != source) {
            JSONArray output;
            try {
                output = new JSONArray(source);
                return output;
            } catch (JSONException e) {
                if (!silent)
                    Log.e(TAG, "JSONException", e);
            }
        }
        return new JSONArray();
    }

    public static Object get(JSONObject source, String... keys) {
        if (null == source)
            return null;
        if (!silent)
            Log.d(TAG, "Fetching generic value: " + StringUtils.join(keys, " ") + " from " + source.toString());
        JSONObject item = null;
        String key = keys[keys.length - 1];
        String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
        if (objectKeys.length > 0) {
            item = JSONHelper.getObject(source, objectKeys);
        } else {
            item = source;
        }
        try {
            if (null != item && item.has(key))
                return item.get(key);
        } catch (JSONException e) {
            if (!silent)
                Log.w(TAG, "Unable to retrieve key: " + key);
        }
        return null;
    }

    public static JSONArray getArray(JSONObject source, String... keys) {
        if (!silent)
            Log.d(TAG, "Fetching array: " + StringUtils.join(keys, " ") + " from " + source.toString());
        JSONObject item = source;
        JSONArray array = null;
        String key = keys[keys.length - 1];
        if (keys.length > 1) {
            String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
            if (objectKeys.length > 0) {
                item = JSONHelper.getObject(source, objectKeys);
            }
        }
        try {
            if (null != item && item.has(key))
                array = item.getJSONArray(key);
        } catch (JSONException e) {
            if (!silent)
                Log.w(TAG, "Not an array: " + key);
        }
        if (null == array && null != item && item.has(key)) {
            array = new JSONArray();
            array.put(JSONHelper.get(item, key));
        } else if (null == item) {
            if (!silent)
                Log.d(TAG, "Invalid source");
        } else if (null == array) {
            if (!silent)
                Log.d(TAG, "Invalid data [" + key + "]: " + item);
        }
        if (null != array)
            return array;
        return new JSONArray();
    }

    public static JSONObject getObject(JSONObject source, String... keys) {
        if (!silent)
            Log.d(TAG, "Fetching object: " + StringUtils.join(keys, " ") + " from " + source.toString());
        if (null == source)
            return null;
        JSONObject item = null;
        String key = keys[keys.length - 1];
        try {
            if (keys.length > 1) {
                String[] remainder = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (null != source && !silent)
                    Log.d(TAG, "Subordinate search: " + StringUtils.join(remainder, " ") + " from " + source.toString());
                source = JSONHelper.getObject(source, remainder);
            }
            Object temp = null;
            if (null != source) {
                if (source.has(key)) {
                    temp = source.get(key);
                    if (!silent)
                        Log.d(TAG, "Retrieved key " + key + ": " + temp);
                    if (temp instanceof String)
                        item = generateObject((String) temp);
                    else if (temp instanceof JSONObject)
                        item = (JSONObject) temp;
                    else
                        item = null;
                }
            }
        } catch (JSONException e) {
            try {
                item = new JSONObject(source.getString(key));
            } catch (JSONException e1) {
                if (!silent)
                    Log.w(TAG, "Error loading target object [ALT]: " + key);
            }
        }
        return item;
    }

    public static double getDouble(JSONObject source, String... keys) {
        return getDouble(source, -1, keys);
    }

    public static double getDouble(JSONObject source, double defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                double realValue = item.optDouble(key, Double.MIN_VALUE);
                if (realValue != Double.MIN_VALUE)
                    return realValue;
                value = source.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value)
                return Double.valueOf(value);
        }
        return defaultValue;
    }

    public static short getShort(JSONObject source, String... keys) {
        return getShort(source, null, keys);
    }

    public static short getShort(JSONObject source, Short defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                value = item.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value)
                return Short.valueOf(value);
        }
        return defaultValue;
    }

    public static String getString(JSONObject source, String... keys) {
        return getStringDefault(source, null, keys);
    }

    public static String getStringDefault(JSONObject source, String defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                value = item.getString(key);
                if (null != value)
                    return value;
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
        }
        return defaultValue;
    }

    public static long getLong(JSONObject source, String... keys) {
        return getLong(source, -1, keys);
    }

    public static long getLong(JSONObject source, long defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                long realValue = item.optLong(key, Long.MIN_VALUE);
                if (realValue != Long.MIN_VALUE)
                    return realValue;
                value = item.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value)
                return Long.valueOf(value);
        }
        return defaultValue;
    }

    public static float getFloat(JSONObject source, String... keys) {
        return getFloat(source, -1, keys);
    }

    public static float getFloat(JSONObject source, float defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                value = item.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value)
                return Float.valueOf(value);
        }
        return defaultValue;
    }

    public static int getInteger(JSONObject source, String... keys) {
        return getInteger(source, -1, keys);
    }

    public static int getInteger(JSONObject source, int defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                int realValue = item.optInt(key, Integer.MIN_VALUE);
                if (realValue != Integer.MIN_VALUE)
                    return realValue;
                value = item.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value)
                return Integer.valueOf(value);
        }
        return defaultValue;
    }

    public static boolean getBoolean(JSONObject source, String... keys) {
        return getBoolean(source, false, keys);
    }

    public static boolean getBoolean(JSONObject source, boolean defaultValue, String... keys) {
        String value = null;
        if (null != source) {
            JSONObject item = source;
            String key = keys[keys.length - 1];
            if (keys.length > 1) {
                String[] objectKeys = ArrayUtils.subarray(keys, 0, keys.length - 1);
                if (objectKeys.length > 0) {
                    item = JSONHelper.getObject(source, objectKeys);
                }
            }
            try {
                value = item.getString(key);
            } catch (JSONException e) {
                if (!silent)
                    Log.w(TAG, "JSON Exception", e);
            }
            if (null != value && (value.equalsIgnoreCase("t") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")))
                return true;
        }
        return defaultValue;
    }

    public static JSONObject getObject(JSONArray source, int index) {
        if (index < source.length())
            try {
                return source.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public static Object getItem(JSONArray source, int index) {
        if (index < source.length())
            try {
                return source.get(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public static JSONArray put(JSONArray target, Object data) {
        return target.put(data);
    }

    public static JSONArray put(JSONArray target, int index, Object data) {
        try {
            return target.put(index, data);
        } catch (JSONException e) {
            if (!silent)
                Log.w(TAG, "JSON Exception", e);
        }
        return target;
    }

    public static JSONObject put(JSONObject target, String key, Object data) {
        try {
            return target.put(key, data);
        } catch (JSONException e) {
            if (!silent)
                Log.w(TAG, "JSON Exception", e);
        } catch (NullPointerException e) {
            if (!silent)
                Log.w(TAG, "Null value: ", e);
            return null;
        }
        return target;
    }

    public JSONObject buildJsonObject(Bundle parameters) {
        if (!silent)
            Log.i(TAG, "Generating JSON from parameters");
        JSONObject container = new JSONObject();
        JSONObject json = new JSONObject();
        String wrapper = parameters.getString(FIELD_OUTER_KEY);
        parameters.remove(FIELD_OUTER_KEY);
        if (null == wrapper) {
            json = BundleBuilder.instance(parameters).buildJson();
        } else {
            container = BundleBuilder.instance(parameters).buildJson();
            try {
                json.put(wrapper, container);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static org.json.JSONObject buildStandardObject(Bundle parameters) {
        if (!silent)
            Log.i(TAG, "Generating JSON from parameters");
        org.json.JSONObject container = new org.json.JSONObject();
        org.json.JSONObject json = new org.json.JSONObject();
        String wrapper = parameters.getString(FIELD_OUTER_KEY);
        parameters.remove(FIELD_OUTER_KEY);
        if (null == wrapper) {
            json = BundleBuilder.instance(parameters).buildStandardJson();
        } else {
            container = BundleBuilder.instance(parameters).buildStandardJson();
            try {
                json.put(wrapper, container);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }
}