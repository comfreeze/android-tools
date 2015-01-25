package net.comfreeze.lib.audio;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import net.comfreeze.lib.CFZApplication;

import java.util.HashMap;

public class SoundManager {
    private static final String TAG = SoundManager.class.getSimpleName();

    private static SoundManager instance;

    private final SoundPool pool;
    private final HashMap<Object, Integer> poolMap;
    private final AudioManager manager;
    private final CFZApplication application;

    public static boolean silent = false;

    public static int streamMax = 4;
    public static int streamType = AudioManager.STREAM_MUSIC;
    public static int streamQuality = 0;

    public SoundManager(CFZApplication application) {
        this.application = application;
        this.pool = new SoundPool(streamMax, streamType, streamQuality);
        this.poolMap = new HashMap<Object, Integer>();
        this.manager = CFZApplication.getAudioManager(application);
    }

    public static synchronized SoundManager instance(CFZApplication application) {
        if (null == instance) {
            if (!silent)
                Log.d(TAG, "Creating instance");
            instance = new SoundManager(application);
        }
        return instance;
    }

    public void addSound(Object name, int resourceId) {
        if (!silent)
            Log.d(TAG, "Adding sound");
        this.poolMap.put(name, this.pool.load(this.application, resourceId, 1));
    }

    public void playSound(Object name) {
        if (!silent)
            Log.d(TAG, "Playing sound: " + name);
        float streamVolume = manager.getStreamVolume(streamType);
        streamVolume = streamVolume / manager.getStreamMaxVolume(streamType);
        this.pool.play(this.poolMap.get(name), streamVolume, streamVolume, 1, 0, 1f);
    }

    public void playLoopedSound(Object name) {
        if (!silent)
            Log.d(TAG, "Playing looped sound: " + name);
        float streamVolume = manager.getStreamVolume(streamType);
        streamVolume = streamVolume / manager.getStreamMaxVolume(streamType);
        this.pool.play(this.poolMap.get(name), streamVolume, streamVolume, 1, -1, 1f);
    }
}