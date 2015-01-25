package net.comfreeze.lib.ui.fragment;

import android.support.v4.app.Fragment;

public class CFZFragmentBase<T> extends Fragment {
    private static final String TAG = CFZFragmentBase.class.getSimpleName();

    public static <T extends CFZFragmentBase> CFZFragmentBase instance(CFZFragmentBase<?> targetClass) {
        return (T) targetClass.instance();
    }

    public T instance() {
        try {
            return (T) this.getClass().newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}