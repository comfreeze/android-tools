package net.comfreeze.lib;

import android.support.v4.app.Fragment;

import java.util.LinkedHashMap;
import java.util.Set;

public class FragmentMap {
    private static final String TAG = FragmentMap.class.getSimpleName();

    private LinkedHashMap<Integer, Fragment> map = new LinkedHashMap<Integer, Fragment>();

    public static FragmentMap instance() {
        FragmentMap map = new FragmentMap();
        return map;
    }

    public FragmentMap add(int key, Fragment fragment) {
        this.map.put(key, fragment);
        return this;
    }

    public Fragment get(int key) {
        return this.map.get(key);
    }

    public LinkedHashMap<Integer, Fragment> build() {
        return this.map;
    }

    public Set<Integer> keySet() {
        return this.map.keySet();
    }
}