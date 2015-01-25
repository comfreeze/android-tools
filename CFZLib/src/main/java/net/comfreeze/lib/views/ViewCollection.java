package net.comfreeze.lib.views;

import android.view.View;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class ViewCollection<E> {
    private static final String TAG = ViewCollection.class.getSimpleName();

    public static boolean silent = true;

    private Class<E> clazz;

    public LinkedHashMap<String, E> map = new LinkedHashMap<String, E>();

    public ViewCollection() {
        this.clazz = (Class<E>) this.getClass();
    }

    public E find(View parent, String name) {
        return (E) parent.findViewWithTag(name);
    }

    public ViewCollection add(String name, E view) {
        if (null != name && null != view)
            map.put(name, view);
        return this;
    }

    public ViewCollection add(String name, View parent, int resId) {
        return add(name, parent, resId, false);
    }

    public ViewCollection add(String name, View parent, int resId, boolean tag) {
        E target = (E) parent.findViewById(resId);
        if (null == target)
            target = find(parent, name);
        if (null != name && null != target)
            map.put(name, target);
        if (null != target && tag)
            ((View) target).setTag(name);
        return this;
    }

    public boolean contains(String name) {
        return map.containsKey(name);
    }

    public boolean contains(E value) {
        return map.containsValue(value);
    }

    public E get(String name) {
        if (map.containsKey(name))
            return (E) map.get(name);
        return null;
    }

    public int count() {
        return map.size();
    }

    public ViewCollection clear() {
        map.clear();
        return this;
    }

    public Collection<E> getViews() {
        return map.values();
    }

    public Set<String> getNames() {
        return map.keySet();
    }

    public static final class InvalidCollectionNameException extends Exception {
    }

    ;

    public static final class InvalidViewException extends Exception {
    }

    ;

    public static final class InvalidResourceException extends Exception {
    }

    ;
}