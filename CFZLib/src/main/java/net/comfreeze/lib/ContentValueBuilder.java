package net.comfreeze.lib;

import android.content.ContentValues;

public class ContentValueBuilder {
    private ContentValues data = new ContentValues();

    public static ContentValueBuilder instance() {
        return new ContentValueBuilder();
    }

    public static ContentValueBuilder instance(ContentValues data) {
        ContentValueBuilder builder = new ContentValueBuilder();
        if (null != data)
            builder.data = new ContentValues(data);
        return builder;
    }

    public static ContentValueBuilder instance(int capacity) {
        ContentValueBuilder builder = new ContentValueBuilder();
        builder.data = new ContentValues(capacity);
        return builder;
    }

    public Object get(String key) {
        if (data.containsKey(key))
            return data.get(key);
        return null;
    }

    public ContentValueBuilder setArg(String key, Object value) {
        if (value instanceof Boolean)
            setArg(key, (Boolean) value);
        else if (value instanceof Byte)
            setArg(key, (Byte) value);
        else if (value instanceof Byte[])
            setArg(key, (byte[]) value);
        else if (value instanceof Double)
            setArg(key, (Double) value);
        else if (value instanceof Float)
            setArg(key, (Float) value);
        else if (value instanceof Integer)
            setArg(key, (Integer) value);
        else if (value instanceof Long)
            setArg(key, (Long) value);
        else if (value instanceof Short)
            setArg(key, (Short) value);
        else if (value instanceof String)
            setArg(key, (String) value);
        return this;
    }

    public ContentValueBuilder setArg(String key, boolean value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Boolean value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, byte value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Byte value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, byte[] value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, double value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Double value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, float value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Float value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, int value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Integer value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, long value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Long value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, short value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, Short value) {
        data.put(key, value);
        return this;
    }

    public ContentValueBuilder setArg(String key, String value) {
        data.put(key, value);
        return this;
    }

    public ContentValues build() {
        return this.data;
    }
}
