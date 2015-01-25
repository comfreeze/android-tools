package net.comfreeze.lib.api.helper;

import android.database.Cursor;

import net.comfreeze.lib.api.helper.ModelHelper.ModelHelperMap.ElementType;

public abstract class CursorHelper {
    private static final String TAG = CursorHelper.class.getSimpleName();

    public CursorHelper() {
    }

    @SuppressWarnings("incomplete-switch")
    public static Object get(Cursor source, String key, ElementType type) {
        Object result = null;
        if (null != type && null != key) {
            switch (type) {
                case SQL_INTEGER:
                case INTEGER:
                    result = getInteger(source, key);
                    break;
                case SQL_REAL:
                case FLOAT:
                    result = getFloat(source, key);
                    break;
                case DOUBLE:
                    result = getDouble(source, key);
                    break;
                case LONG:
                    result = getLong(source, key);
                    break;
                case SQL_TEXT:
                case STRING:
                    result = getString(source, key);
                    break;
                case BOOLEAN:
                    result = getBoolean(source, key);
                    break;
                case SHORT:
                    result = getShort(source, key);
                    break;
                case SQL_BLOB:
                    result = getByteArray(source, key);
                    break;
            }
        }
        return result;
    }

    public static double getDouble(Cursor source, String key) {
        double realValue = -1;
        if (source.getColumnIndex(key) > -1) {
            realValue = source.getDouble(source.getColumnIndex(key));
            return realValue;
        }
        return -1;
    }

    public static short getShort(Cursor source, String key) {
        short value = -1;
        if (source.getColumnIndex(key) > -1) {
            value = source.getShort(source.getColumnIndex(key));
            return value;
        }
        return -1;
    }

    public static String getString(Cursor source, String key) {
        String value = null;
        if (source.getColumnIndex(key) > -1) {
            value = source.getString(source.getColumnIndex(key));
            return value;
        }
        return null;
    }

    public static long getLong(Cursor source, String key) {
        long value = -1;
        if (source.getColumnIndex(key) > -1) {
            value = source.getLong(source.getColumnIndex(key));
            return value;
        }
        return -1;
    }

    public static float getFloat(Cursor source, String key) {
        float value = -1;
        if (source.getColumnIndex(key) > -1) {
            value = source.getFloat(source.getColumnIndex(key));
            return value;
        }
        return -1;
    }

    public static int getInteger(Cursor source, String key) {
        int value = -1;
        if (source.getColumnIndex(key) > -1) {
            value = source.getInt(source.getColumnIndex(key));
            return value;
        }
        return -1;
    }

    public static boolean getBoolean(Cursor source, String key) {
        boolean value = false;
        if (source.getColumnIndex(key) > -1) {
            value = source.getInt(source.getColumnIndex(key)) == 1 ? true : false;
            return value;
        }
        return value;
    }

    public static byte[] getByteArray(Cursor source, String key) {
        byte[] value = null;
        if (source.getColumnIndex(key) > -1) {
            value = source.getBlob(source.getColumnIndex(key));
        }
        return value;
    }
}