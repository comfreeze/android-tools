package net.comfreeze.lib.db;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

abstract public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public static DatabaseHelper instance = null;

    protected static Context context;

    public static boolean silent = true;

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        setContext(context);
        if (!silent)
            Log.d(TAG, "DatabaseHelper loading...");
    }

    @Override
    protected void finalize() throws Throwable {
        unsetContext();
        super.finalize();
    }

    public DatabaseHelper setContext(Context context) {
        DatabaseHelper.context = context;
        return this;
    }

    public DatabaseHelper unsetContext() {
        DatabaseHelper.context = null;
        return this;
    }

    /**
     * Called during creation cycle to generate primary database. This is called
     * after executing database_create from string or string-array resources if
     * found.
     *
     * @param db
     */
    abstract protected void initialize(SQLiteDatabase db);

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!silent)
            Log.d(TAG, "Initializing database");
        executeScript(db, "database_create");
        initialize(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (!silent)
            Log.d(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        for (int i = oldVersion; i <= newVersion; i++)
            executeScript(db, "database_" + i + "_upgrade");
    }

//    @Override
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//	if (!silent)
//	    Log.d(TAG, "Downgrading database from " + oldVersion + " to " + newVersion);
//	for (int i = newVersion; i >= oldVersion; i--)
//	    executeScript(db, "database_" + i + "_downgrade");
//    }

    /**
     * Executes a script from string or string-array resources if one can be
     * loaded. This function will attempt to use internal SQLiteOpenHelper
     * functions to connect the database.
     *
     * @param scriptName The name found in the resources file.
     * @return Returns the number of successful scripts processed.
     */
    public int executeScript(String scriptName) {
        return executeScript(getWritableDatabase(), scriptName);
    }

    /**
     * Executes a script from string or string-array resources if one can be
     * loaded.
     *
     * @param db         An existing SQLiteDatabase connection.
     * @param scriptName The name found in the resources file.
     * @return Returns the number of successful scripts processed.
     */
    public int executeScript(SQLiteDatabase db, String scriptName) {
        return executeScript(db, scriptName, null);
    }

    /**
     * Executes a script from string or string-array resources if one can be
     * loaded. This function will attempt to use internal SQLiteOpenHelper
     * functions to connect the database.
     *
     * @param scriptName The name found in the resources file.
     * @param arguments  An object array of bound query parameters
     * @return Returns the number of successful scripts processed.
     */
    public int executeScript(String scriptName, Object[] arguments) {
        return executeScript(getWritableDatabase(), scriptName, arguments);
    }

    /**
     * Executes a script from string or string-array resources if one can be
     * loaded.
     *
     * @param db         An existing SQLiteDatabase connection.
     * @param scriptName The name found in the resources file.
     * @param arguments  A string array of bound query variables.
     * @return Returns the number of successful scripts processed.
     */
    public int executeScript(SQLiteDatabase db, String scriptName, Object[] arguments) {
        if (!silent)
            Log.d(TAG, "Executing script: " + scriptName);
        int result = 0;
        String upgradeSQL;
        final Resources resources = context.getResources();
        int identifier = resources.getIdentifier(scriptName, "string", context.getPackageName());
        if (identifier != 0) {
            upgradeSQL = resources.getString(identifier);
            if (null != upgradeSQL)
                executeRawSql(db, upgradeSQL, arguments);
        } else {
            identifier = resources.getIdentifier(scriptName, "array", context.getPackageName());
            if (identifier != 0) {
                String[] updateScripts = resources.getStringArray(identifier);
                if (null != updateScripts) {
                    for (String sql : updateScripts)
                        executeRawSql(db, sql, arguments);
                }
            }
        }
        return result;
    }

    /**
     * Runs a single RAW sql operation with a given query string and provided
     * string array for bound values if not included directly in the source
     * query string.
     *
     * @param db   An existing SQLiteDatabase connection.
     * @param sql  The name found in the resources file.
     * @param args A string array of bound query variables.
     */
    private void executeRawSql(SQLiteDatabase db, String sql, Object[] args) {
        if (!silent)
            Log.d(TAG, "Running raw SQL: " + sql);
        try {
            if (null == args)
                db.execSQL(sql);
            else
                db.execSQL(sql, args);
        } catch (SQLiteException e) {
            Log.d(TAG, "Exception", e);
        }
    }

    public ArrayList<String> tables() {
        ArrayList<String> tables = new ArrayList<String>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Log.d(TAG, "Table data: " + name);
            tables.add(name);
        }
        return tables;
    }

    public static DatabaseHelper getInstance(Class<?> className, Context context, String name, int version) {
        if (instance == null) {
            synchronized (className) {
                if (instance == null) {
                    try {
                        instance = (DatabaseHelper) className.newInstance();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    public void describe(SQLiteDatabase db) {
        String sql = "SELECT * FROM sqlite_master WHERE type='table'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "Table: " + cursor.getString(1) + " Create Statement: " + cursor.getString(4));
            } while (cursor.moveToNext());
        }
    }

    protected void dropTable(SQLiteDatabase db, String name) {
        db.execSQL(String.format("DROP TABLE IF EXISTS \"%s\"", name));
    }
}
