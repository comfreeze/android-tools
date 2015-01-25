package net.comfreeze.lib.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

abstract public class DatabaseTable {
    private static final String TAG = DatabaseTable.class.getSimpleName();

    protected SQLiteDatabase db;
    public Context currentContext;

    public DatabaseTable(Context context, SQLiteDatabase database) {
        Log.d(TAG, "DatabaseTable loading...");
        currentContext = context;
        db = database;
    }

    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    public int count(String table) {
        String sql = String.format("SELECT COUNT(1) AS id_count FROM %s", table);
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        log("number of records: " + count);
        cursor.close();
        return count;
    }

    public int getNextId(String table, String column) {
        if (column == null) {
            column = "id";
        }
        Log.d(TAG, "DatabaseTable: getting next unique ID for " + table);
        String query = "SELECT MAX(" + column + ") AS max_id FROM " + table;
        Cursor cursor = db.rawQuery(query, null);
        int id = 1;
        if (cursor.moveToFirst()) {
            Log.d(TAG, "Resulting columns: " + cursor.getColumnCount());
            id = cursor.getInt(0) + 1;
        }
        if (id < 1) {
            id = 1;
        }
        Log.d(TAG, "DatabaseTable: resulting unique ID: " + id);
        return id;
    }

    public Cursor rawQuery(String query) {
        return db.rawQuery(query, null);
    }

    public Cursor rawQuery(String query, String[] selectionArgs) {
        return db.rawQuery(query, selectionArgs);
    }

    protected void log(String message) {
        Log.d(TAG, "[DB] " + message);
    }
}
