package net.comfreeze.lib.api.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.comfreeze.lib.api.helper.ModelHelper.ModelHelperMap.ElementType;

import org.json.custom.JSONException;
import org.json.custom.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

abstract public class ModelHelper<T> {
    public LinkedHashMap<String, ModelHelperMap> map;
    public LinkedHashMap<String, Object> dataMap;

    public T fromJSON(JSONObject source) {
        if (null != source) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.JSON);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    dataMap.put(key, JSONHelper.get(source, key));
                }
            }
        }
        return (T) this;
    }

    public T fromCursor(Cursor source) {
        if (null != source) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.SQL);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    dataMap.put(key, CursorHelper.get(source, key, indexes.get(key)));
                }
            }
        }
        return (T) this;
    }

    public T fromBundle(Bundle source) {
        if (null != source) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.JAVA);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ElementType type = indexes.get(key);
                    switch (type) {
                        case SQL_INTEGER:
                        case INTEGER:
                            dataMap.put(key, source.getInt(key));
                            break;
                        case LONG:
                            dataMap.put(key, source.getLong(key));
                            break;
                        case DOUBLE:
                            dataMap.put(key, source.getDouble(key));
                            break;
                        case SQL_REAL:
                        case FLOAT:
                            dataMap.put(key, source.getFloat(key));
                            break;
                        case SQL_TEXT:
                        case STRING:
                            dataMap.put(key, source.getString(key));
                            break;
                        case BYTE:
                            dataMap.put(key, source.getByte(key));
                            break;
                        case BYTE_ARRAY:
                        case SQL_BLOB:
                            dataMap.put(key, source.getByteArray(key));
                            break;
                    }
                }
            }
        }
        return (T) this;
    }

    public JSONObject toJSONObject() {
        JSONObject output = new JSONObject();
        if (null != dataMap) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.JSON);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (dataMap.containsKey(key)) {
                        try {
                            output.put(key, dataMap.get(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return output;
    }

    public ContentValues toContentValues() {
        ContentValues output = new ContentValues();
        if (null != dataMap) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.SQL);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ElementType type = indexes.get(key);
                    switch (type) {
                        case SQL_INTEGER:
                        case INTEGER:
                            output.put(key, (Integer) dataMap.get(key));
                            break;
                        case SQL_REAL:
                        case FLOAT:
                            output.put(key, (Float) dataMap.get(key));
                            break;
                        case DOUBLE:
                            output.put(key, (Double) dataMap.get(key));
                            break;
                        case LONG:
                            output.put(key, (Long) dataMap.get(key));
                            break;
                        case SQL_TEXT:
                        case STRING:
                            output.put(key, (String) dataMap.get(key));
                            break;
                        case BYTE:
                            output.put(key, (Byte) dataMap.get(key));
                            break;
                        case BYTE_ARRAY:
                        case SQL_BLOB:
                            output.put(key, (byte[]) dataMap.get(key));
                            break;
                    }
                }
            }
        }
        return output;
    }

    public Bundle toBundle() {
        Bundle output = new Bundle();
        if (null != dataMap) {
            HashMap<String, ElementType> indexes = getTypeIndex(MAP_INDEX.JAVA);
            if (null != indexes) {
                Iterator<String> keys = indexes.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ElementType type = indexes.get(key);
                    switch (type) {
                        case SQL_INTEGER:
                        case INTEGER:
                            output.putInt(key, (Integer) dataMap.get(key));
                            break;
                        case SQL_REAL:
                        case FLOAT:
                            output.putFloat(key, (Float) dataMap.get(key));
                            break;
                        case DOUBLE:
                            output.putDouble(key, (Double) dataMap.get(key));
                            break;
                        case LONG:
                            output.putLong(key, (Long) dataMap.get(key));
                            break;
                        case SQL_TEXT:
                        case STRING:
                            output.putString(key, (String) dataMap.get(key));
                            break;
                        case BYTE:
                            output.putByte(key, (Byte) dataMap.get(key));
                            break;
                        case BYTE_ARRAY:
                        case SQL_BLOB:
                            output.putByteArray(key, (byte[]) dataMap.get(key));
                            break;
                    }
                }
            }
        }
        return output;
    }

    public HashMap<String, ElementType> getTypeIndex(String index) {
        HashMap<String, ElementType> indexes = new HashMap<String, ElementType>();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            ModelHelperMap itemMap = map.get(key);
            if (itemMap.hasMap(index))
                indexes.put(key, itemMap.getMap(index));
        }
        return indexes;
    }

    // private Class<?> clazz;
    //
    // public ModelHelper(Class<?> clazz) {
    // this.clazz = clazz;
    // }
    public static final class MAP_INDEX {
        public static final String UNKNOWN = "UNKNOWN";
        public static final String JSON = "JSON";
        public static final String SQL = "SQL";
        public static final String JAVA = "JAVA";
    }

    public static class ModelHelperMap {
        public HashMap<String, ElementType> types = new HashMap<String, ElementType>();
        public Object defaultValue = null;

        public static ModelHelperMap newInstance(HashMap<String, ElementType> types, Object defaultValue) {
            ModelHelperMap map = new ModelHelperMap();
            map.types = types;
            map.defaultValue = defaultValue;
            return map;
        }

        public static ModelHelperMap newInstance(Object defaultValue) {
            ModelHelperMap map = new ModelHelperMap();
            map.defaultValue = defaultValue;
            return map;
        }

        public ModelHelperMap addMap(String index, ElementType type) {
            types.put(index, type);
            return this;
        }

        public ElementType getMap(String index) {
            return types.get(index);
        }

        public boolean hasMap(String index) {
            return types.containsKey(index);
        }

        public static enum ElementType {
            UNKNOWN, //
            // SQLITE3
            SQL_INTEGER, SQL_REAL, //
            SQL_TEXT, SQL_BLOB, //
            // JSON
            JSON_OBJECT, JSON_ARRAY, //
            // Others
            FLOAT, LONG, DOUBLE, STRING, //
            INTEGER, BYTE, BYTE_ARRAY, //
            BOOLEAN, SHORT //
        }
    }
}
