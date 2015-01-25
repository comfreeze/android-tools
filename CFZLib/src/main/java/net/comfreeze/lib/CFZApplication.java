package net.comfreeze.lib;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.AudioManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.comfreeze.lib.audio.SoundManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

abstract public class CFZApplication extends Application {
    public static final String TAG = "CFZApplication";
    public static final String PACKAGE = "com.rastermedia";
    public static final String KEY_DEBUG = "debug";
    public static final String KEY_SYNC_PREFEX = "sync_";

    public static SharedPreferences preferences = null;

    protected static Context context = null;

    protected static CFZApplication instance;

    public static LocalBroadcastManager broadcast;

    public static boolean silent = true;

    @Override
    public void onCreate() {
        if (!silent)
            Log.d(TAG, "Initializing");
        instance = this;
        setContext();
        setPreferences();
        broadcast = LocalBroadcastManager.getInstance(context);
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        if (!silent)
            Log.d(TAG, "Low memory!");
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        unsetContext();
        if (!silent)
            Log.d(TAG, "Terminating");
        super.onTerminate();
    }

    abstract public void setPreferences();

    abstract public void setContext();

    abstract public void unsetContext();

    abstract public String getProductionKey();

    public static CFZApplication getInstance(Class<?> className) {
        // log("Returning current application instance");
        if (instance == null) {
            synchronized (className) {
                if (instance == null)
                    try {
                        instance = (CFZApplication) className.newInstance();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
            }
        }
        return instance;
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String dir : children) {
                if (!dir.equals("lib")) {
                    deleteDir(new File(appDir, dir));
                }
            }
        }
        preferences.edit().clear().commit();
    }

    private static boolean deleteDir(File dir) {
        if (dir != null) {
            if (!silent)
                Log.d(PACKAGE, "Deleting: " + dir.getAbsolutePath());
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success)
                        return false;
                }
            }
            return dir.delete();
        }
        return false;
    }

    public static int dipsToPixel(float value, float scale) {
        return (int) (value * scale + 0.5f);
    }

    public static long timezoneOffset() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET));
    }

    public static long timezoneOffset(String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        return (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET));
    }

    public static Resources res() {
        return context.getResources();
    }

    public static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public static SoundManager getSoundManager(CFZApplication application) {
        return (SoundManager) SoundManager.instance(application);
    }

    public static void setSyncDate(String key, long date) {
        preferences.edit().putLong(KEY_SYNC_PREFEX + key, date).commit();
    }

    public static long getSyncDate(String key) {
        return preferences.getLong(KEY_SYNC_PREFEX + key, -1L);
    }

    public static boolean needSync(String key, long limit) {
        return (System.currentTimeMillis() - getSyncDate(key) > limit);
    }

    public static String hash(final String algorithm, final String s) {
        try {
            // Create specified hash
            MessageDigest digest = java.security.MessageDigest.getInstance(algorithm);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create hex string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isProduction(Context context) {
        boolean result = false;
        try {
            ComponentName component = new ComponentName(context, instance.getClass());
            PackageInfo info = context.getPackageManager().getPackageInfo(component.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] sigs = info.signatures;
            for (int i = 0; i < sigs.length; i++)
                if (!silent)
                    Log.d(TAG, "Signing key: " + sigs[i].toCharsString());
            String targetKey = getProductionKey();
            if (null == targetKey)
                targetKey = "";
            if (targetKey.equals(sigs[0].toCharsString())) {
                result = true;
                if (!silent)
                    Log.d(TAG, "Signed with production key");
            } else {
                if (!silent)
                    Log.d(TAG, "Not signed with production key");
            }
        } catch (PackageManager.NameNotFoundException e) {
            if (!silent)
                Log.e(TAG, "Package exception", e);
        }
        return result;
    }


    public static class LOG {
        // Without Throwables
        public static void v(String tag, String message) {
            if (!silent) Log.v(tag, message);
        }

        public static void d(String tag, String message) {
            if (!silent) Log.d(tag, message);
        }

        public static void i(String tag, String message) {
            if (!silent) Log.i(tag, message);
        }

        public static void w(String tag, String message) {
            if (!silent) Log.w(tag, message);
        }

        public static void e(String tag, String message) {
            if (!silent) Log.e(tag, message);
        }

        public static void wtf(String tag, String message) {
            if (!silent) Log.wtf(tag, message);
        }

        // With Throwables
        public static void v(String tag, String message, Throwable t) {
            if (!silent) Log.v(tag, message, t);
        }

        public static void d(String tag, String message, Throwable t) {
            if (!silent) Log.d(tag, message, t);
        }

        public static void i(String tag, String message, Throwable t) {
            if (!silent) Log.i(tag, message, t);
        }

        public static void w(String tag, String message, Throwable t) {
            if (!silent) Log.w(tag, message, t);
        }

        public static void e(String tag, String message, Throwable t) {
            if (!silent) Log.e(tag, message, t);
        }

        public static void wtf(String tag, String message, Throwable t) {
            if (!silent) Log.wtf(tag, message, t);
        }
    }
}
