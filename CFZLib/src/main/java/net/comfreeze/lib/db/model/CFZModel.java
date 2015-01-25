package net.comfreeze.lib.db.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

abstract public class CFZModel {
    private static final String TAG = CFZModel.class.getSimpleName();

    protected static FieldColumnMap[] map = new FieldColumnMap[]{};

    protected LinkedHashMap<String, Object> columnData = new LinkedHashMap<String, Object>();
    protected LinkedHashMap<String, Object> fieldData = new LinkedHashMap<String, Object>();

    abstract public Uri getContentUri();

    public static FieldColumnMap[] getMap() {
        if (null == map)
            map = new FieldColumnMap[]{};
        return map;
    }

    public static void addMap(FieldColumnMap mapping) {
        map[map.length] = mapping;
    }

    public FieldColumnMap.DataType getColumnType(String column) {
        FieldColumnMap result = findColumn(column);
        if (null != result)
            return result.columnType;
        return null;
    }

    public FieldColumnMap.DataType getFieldType(String field) {
        FieldColumnMap result = findField(field);
        if (null != result)
            return result.fieldType;
        return null;
    }

    public FieldColumnMap findColumn(String column) {
        for (FieldColumnMap item : map)
            if (item.columnName.equals(column))
                return item;
        return null;
    }

    public FieldColumnMap findField(String field) {
        for (FieldColumnMap item : map)
            if (item.fieldName.equals(field))
                return item;
        return null;
    }

    public Object getColumn(String column) {
        if (columnData.containsKey(column))
            return columnData.get(column);
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends CFZModel> T setColumn(String column, Object value) {
        FieldColumnMap mapping = findColumn(column);
        if (null != mapping) {
            columnData.put(mapping.columnName, value);
            fieldData.put(mapping.fieldName, value);
        }
        return (T) this;
    }

    public Object getField(String field) {
        if (columnData.containsKey(field))
            return columnData.get(field);
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends CFZModel> T setField(String field, Object value) {
        FieldColumnMap mapping = findField(field);
        if (null != mapping) {
            columnData.put(mapping.columnName, value);
            fieldData.put(mapping.fieldName, value);
        }
        return (T) this;
    }

    public static <T extends CFZModel> T loadCursor(Class<T> model, Cursor cursor, HashMap<String, Integer> columnMap) {
        T instance = null;
        try {
            instance = model.newInstance();
            if (null != cursor) {
                for (FieldColumnMap mapping : map) {
                    try {
                        int columnIndex = (null != columnMap && columnMap.containsKey(mapping.columnName) ? columnMap.get(mapping.columnName) : cursor.getColumnIndex(mapping.columnName));
                        if (columnIndex > -1) {
                            switch (mapping.columnType) {
                                case INTEGER:
                                    instance.setColumn(mapping.columnName, cursor.getInt(columnIndex));
                                    break;
                                case REAL:
                                    instance.setColumn(mapping.columnName, cursor.getFloat(columnIndex));
                                    break;
                                case TEXT:
                                    instance.setColumn(mapping.columnName, cursor.getString(columnIndex));
                                    break;
                                default:
                                case BLOB:
                                    instance.setColumn(mapping.columnName, cursor.getBlob(columnIndex));
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cursor Exception", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        return instance;
    }

    public static <T extends CFZModel> ArrayList<T> loadCursorResult(Class<T> model, Cursor cursor) {
        ArrayList<T> collection = new ArrayList<T>();
        HashMap<String, Integer> columnMap = new HashMap<String, Integer>();
        if (null != cursor) {
            for (FieldColumnMap mapping : map)
                columnMap.put(mapping.columnName, cursor.getColumnIndex(mapping.columnName));
            while (cursor.moveToNext())
                collection.add(T.loadCursor(model, cursor, columnMap));
        }
        return (ArrayList<T>) collection;
    }

    public static <T extends CFZModel> T loadObject(Class<T> model, JSONObject object) {
        T instance = null;
        try {
            instance = model.newInstance();
            for (FieldColumnMap mapping : map) {
                try {
                    if (object.has(mapping.fieldName))
                        instance.setField(mapping.fieldName, object.get(mapping.fieldName));
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        return instance;
    }

    public static <T extends CFZModel> ArrayList<T> loadArray(Class<T> model, JSONArray array) {
        ArrayList<T> collection = new ArrayList<T>();
        try {
            if (null != array) {
                for (int i = 0; i < array.length(); i++)
                    collection.add(T.loadObject(model, array.getJSONObject(i)));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    public <T extends CFZModel> T upsert(ContentResolver cr, String primaryKey) {
        Uri uri = getContentUri();
        String[] projection = null;
        String selection = primaryKey + " = ?";
        String[] selectionArgs = new String[]{(String) getColumn(primaryKey)};
        String sortOrder = null;
        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        if (null != cursor) {
            cursor.close();
            cr.update(uri, getContentValues(), selection, selectionArgs);
        } else {
            cr.insert(uri, getContentValues());
        }
        return (T) this;
    }

    public JSONObject getObject() {
        JSONObject output = new JSONObject();
        for (FieldColumnMap mapping : map) {
            try {
                if (fieldData.containsKey(mapping.fieldName))
                    output.putOpt(mapping.fieldName, fieldData.get(mapping.fieldName));
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception", e);
            }
        }
        return output;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        for (FieldColumnMap mapping : map) {
            switch (mapping.columnType) {
                case TEXT:
                    values.put(mapping.columnName, (String) getColumn(mapping.columnName));
                    break;
                case INTEGER:
                    values.put(mapping.columnName, (Integer) getColumn(mapping.columnName));
                    break;
                case REAL:
                    values.put(mapping.columnName, (Float) getColumn(mapping.columnName));
                    break;
                default:
                case BLOB:
                    values.put(mapping.columnName, (byte[]) getColumn(mapping.columnName));
                    break;
                case NULL:
                    break;
            }
        }
        return values;
    }
}