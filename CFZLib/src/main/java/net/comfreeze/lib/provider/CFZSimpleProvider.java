package net.comfreeze.lib.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

abstract public class CFZSimpleProvider extends ContentProvider {
    public static final int _URI_ID_RAW_QUERY = Integer.MAX_VALUE - 1;
    public static final int _URI_ID_EXECUTE_SCRIPT = Integer.MAX_VALUE - 2;
    public static final String _PATH_RAW_QUERY = "raw_query";
    public static final String _PATH_EXECUTE_SCRIPT = "exec_script";
    protected static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String TAG = CFZSimpleProvider.class.getSimpleName();
    public static boolean silent = true;
    protected SQLiteOpenHelper helper;
    protected SQLiteDatabase connection;
    protected SparseArray<String> paths;
    protected HashMap<String, String> pathTableMap;

    public CFZSimpleProvider() {
        this // Construct all path matches here
    /* */.addPath(_URI_ID_RAW_QUERY, _PATH_RAW_QUERY, null)
	/* */.addPath(_URI_ID_EXECUTE_SCRIPT, _PATH_EXECUTE_SCRIPT, null);
    }

    /**
     * Helper method for building WHERE IN queries
     *
     * @param selection
     * @param args
     * @return
     */
    public static Pair<String, String[]> queryIn(String selection, String[] args) {
        Pair<String, String[]> result = null;
        if (selection.contains("IN?")) {
            StringBuilder builder = new StringBuilder();
            int params = StringUtils.countMatches(selection, "?");
            int length = args.length;
            if (params > 1)
                length = length - (params - 1);
            if (params >= 1) {
                for (int i = 0; i < length; i++) {
                    builder.append((i == 0 ? "" : ", ") + "?");
                }
                if (builder.toString().length() > 0)
                    result = new Pair(selection.replace("IN?", "IN(" + builder.toString() + ")"), args);
                else
                    result = new Pair(selection.replace("IN?", "1"), args);
            }
        } else
            result = new Pair(selection.replace("IN?", "1"), args);
        return result;
    }

    abstract public SQLiteOpenHelper getHelper();

    abstract public String getAuthority();

    @Override
    public String getType(Uri uri) {
        // Not used
        return null;
    }

    @Override
    public boolean onCreate() {
        helper = getHelper();
        return true;
    }

    public CFZSimpleProvider addURI(String authority, String path, int id) {
        URI_MATCHER.addURI(authority, path, id);
        return this;
    }

    public CFZSimpleProvider addTableMap(String path, String table) {
        if (null == pathTableMap)
            pathTableMap = new HashMap<String, String>();
        pathTableMap.put(path, table);
        return this;
    }

    public CFZSimpleProvider addPath(int id, String path, String table) {
        if (null == paths)
            paths = new SparseArray<String>();
        paths.append(id, path);
        addTableMap(path, table);
        return addURI(getAuthority(), path, id);
    }

    public CFZSimpleProvider addPaths(int id, String[] newPaths, String table) {
        if (null == paths)
            paths = new SparseArray<String>();
        for (String path : newPaths) {
            paths.append(id, path);
            addTableMap(path, table);
            addURI(getAuthority(), path, id);
        }
        return this;
    }

    public String getTable(Uri uri) {
        String table = null;
        if (null != uri) {
            int id = URI_MATCHER.match(uri);
            return pathTableMap.get(paths.get(id));
        }
        return table;
    }

    public SQLiteDatabase getConnection() {
        if (null == connection)
            connection = getHelper().getWritableDatabase();
        return connection;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!silent)
            Log.d(TAG, "Query: " + uri.toString());
        switch (URI_MATCHER.match(uri)) {
            case _URI_ID_RAW_QUERY:
                return getConnection().rawQuery(selection, selectionArgs);
            case _URI_ID_EXECUTE_SCRIPT:
                try {
                    Method exec = getHelper().getClass().getMethod("executeScript", SQLiteDatabase.class, String.class, String[].class);
                    exec.invoke(getHelper(), getConnection(), selection, selectionArgs);
                } catch (NoSuchMethodException e) {
                    if (!silent)
                        Log.e(TAG, "NoSuchMethodException", e);
                } catch (InvocationTargetException e) {
                    if (!silent)
                        Log.e(TAG, "InvocationTargetException", e);
                } catch (IllegalAccessException e) {
                    if (!silent)
                        Log.e(TAG, "IllegalAccessException", e);
                }
                return null;
        }
        String table = getTable(uri);
        String groupBy = null;
        String having = null;
        SQLiteDatabase db = getConnection();
        if (null != selection) {
            if (selection.contains(" GROUP BY ")) {
                String[] parts = selection.split(" GROUP BY ");
                selection = parts[0];
                groupBy = parts[1];
            }
            if (selection.contains(" HAVING ")) {
                String[] parts = selection.split(" HAVING ");
                selection = parts[0];
                having = parts[1];
            }
        }
        String orderBy = sortOrder;
        Cursor cursor = db.query(table, projection, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (!silent)
            Log.d(TAG, "Insert: " + uri.toString());
        long id = getConnection().insertWithOnConflict(getTable(uri), "_id", values, SQLiteDatabase.CONFLICT_REPLACE);
        if (id > -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, "" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (!silent)
            Log.d(TAG, "Update: " + uri.toString());
        int rows = getConnection().update(getTable(uri), values, selection, selectionArgs);
        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (!silent)
            Log.d(TAG, "Delete: " + uri.toString());
        int rows = getConnection().delete(getTable(uri), selection, selectionArgs);
        if (rows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
