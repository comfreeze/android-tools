package net.comfreeze.lib.db.helper;

import net.comfreeze.lib.CFZApplication;

import android.database.Cursor;

import java.util.LinkedHashMap;

/**
 * Created by james on 8/27/13.
 */
public class HelperCursor {
    public static final String TAG = HelperCursor.class.getSimpleName();
    private LinkedHashMap<String, Integer> columns;

    public static <T extends HelperCursor> T factory(Class clazz, Cursor cursor) {
        T helper = null;
        try {
            helper = (T) clazz.newInstance();
            helper.map(cursor);
        } catch (InstantiationException e) {
            CFZApplication.LOG.e(TAG, "ROW HELPER EXCEPTION", e);
        } catch (IllegalAccessException e) {
            CFZApplication.LOG.e(TAG, "ROW HELPER EXCEPTION", e);
        }
        return helper;
    }

    public String[] getColumns() {
        return columns.keySet().toArray(new String[columns.size()]);
    }

    public Integer getInt(Cursor cursor, String name, int def) {
        if (columns.containsKey(name))
            return cursor.getInt(columns.get(name));
        return def;
    }

    public Long getLong(Cursor cursor, String name, long def) {
        if (columns.containsKey(name))
            return cursor.getLong(columns.get(name));
        return def;
    }

    public String getString(Cursor cursor, String name, String def) {
        if (columns.containsKey(name))
            return cursor.getString(columns.get(name));
        return def;
    }

    public byte[] getByte(Cursor cursor, String name, byte[] def) {
        if (columns.containsKey(name))
            return cursor.getBlob(columns.get(name));
        return def;
    }

    public Double getDouble(Cursor cursor, String name, double def) {
        if (columns.containsKey(name))
            return cursor.getDouble(columns.get(name));
        return def;
    }

    public Float getFloat(Cursor cursor, String name, float def) {
        if (columns.containsKey(name))
            return cursor.getFloat(columns.get(name));
        return def;
    }

    public Short getShort(Cursor cursor, String name, short def) {
        if (columns.containsKey(name))
            return cursor.getShort(columns.get(name));
        return def;
    }

    public HelperCursor addColumn(String name, int index) {
        if (null == columns)
            columns = new LinkedHashMap<String, Integer>();
        columns.put(name, index);
        return this;
    }

    public void map(Cursor cursor) {
        if (null != cursor) {
            String[] columnNames = cursor.getColumnNames();
            for (String name : columnNames) {
                addColumn(name, cursor.getColumnIndex(name));
            }
        }
    }
}
