package net.comfreeze.lib.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.util.Log;

import java.util.ArrayList;

abstract public class SupportFragmentActivity extends FragmentActivity {
    private static final String TAG = SupportFragmentActivity.class.getSimpleName();
    public static boolean silent = false;
    protected static FragmentTransaction transaction;
    protected Object state = null;
    protected ArrayList<Object> stateHistory = new ArrayList<Object>();
    protected int defaultIntentFlags = Intent.FLAG_ACTIVITY_NO_ANIMATION;

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        if (!silent)
            Log.d(TAG, "Custom back logic");
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            popState();
        } else {
            overridePendingTransition(0, 0);
            finish();
        }
    }

    public Object getState() {
        if (!silent)
            Log.d(TAG, "Returning active state");
        return state;
    }

    public ArrayList<Object> getStateHistory() {
        if (null == stateHistory)
            stateHistory = new ArrayList<Object>();
        return stateHistory;
    }

    public void popState() {
        if (!silent)
            Log.d(TAG, "Removing last state from history and setting active");
        if (getStateHistory().size() > 0)
            state = getStateHistory().remove(getStateHistory().size() - 1);
        else
            state = null;
        updateState();
    }

    public void pushState(Object newState) {
        if (!silent)
            Log.d(TAG, "Adding new state to history and setting active");
        getStateHistory().add(state);
        state = newState;
        updateState();
    }

    abstract public void updateState();

    public void startActivity(Class<?> className) {
        startActivity(className, null, null, defaultIntentFlags);
    }

    public void startActivity(Class<?> className, String action) {
        startActivity(className, action, null, defaultIntentFlags);
    }

    public void startActivity(Class<?> className, Bundle extras) {
        startActivity(className, null, extras, defaultIntentFlags);
    }

    public void startActivity(Class<?> className, String action, Bundle extras) {
        startActivity(className, action, extras, defaultIntentFlags);
    }

    public void startActivity(Class<?> className, int flags) {
        startActivity(className, null, null, flags);
    }

    public void startActivity(Class<?> className, String action, int flags) {
        startActivity(className, action, null, flags);
    }

    public void startActivity(Class<?> className, Bundle extras, int flags) {
        startActivity(className, null, extras, flags);
    }

    public void startActivity(Class<?> className, String action, Bundle extras, int flags) {
        if (!silent)
            Log.d(TAG, "Starting activity: " + className.getSimpleName());
        Intent intent = IntentBuilder.from(this).getIntent().setFlags(defaultIntentFlags | flags);
        if (null != action)
            intent.setAction(action);
        if (null != className)
            intent.setClass(getBaseContext(), className);
        if (null != extras)
            intent.putExtras(extras);
        // Intent intent = new Intent(this, className);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // intent.addFlags(flags);
        // if (null != extras)
        // intent.putExtras(extras);
        startActivity(intent);
    }

    public void startService(Class<?> className) {
        startService(className, null, null, 0);
    }

    public void startService(Class<?> className, String action) {
        startService(className, action, null, 0);
    }

    public void startService(Class<?> className, Bundle extras) {
        startService(className, null, extras, 0);
    }

    public void startService(Class<?> className, String action, Bundle extras) {
        startService(className, action, extras, 0);
    }

    public void startService(Class<?> className, int flags) {
        startService(className, null, null, flags);
    }

    public void startService(Class<?> className, String action, int flags) {
        startService(className, action, null, flags);
    }

    public void startService(Class<?> className, Bundle extras, int flags) {
        startService(className, null, extras, flags);
    }

    public void startService(Class<?> className, String action, Bundle extras, int flags) {
        if (!silent)
            Log.d(TAG, "Starting service: " + className.getSimpleName());
        Intent intent = IntentBuilder.from(this).getIntent().setFlags(flags);
        if (null != className)
            intent.setClass(getBaseContext(), className);
        if (null != action)
            intent.setAction(action);
        if (null != extras)
            intent.putExtras(extras);
        // Intent intent = new Intent(this, className);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // intent.addFlags(flags);
        // if (null != extras)
        // intent.putExtras(extras);
        startService(intent);
    }

    public void removeFragment(Fragment fragment) {
        if (!silent)
            Log.d(TAG, "Removing fragment");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    public void hideFragment(Fragment fragment) {
        if (!silent)
            Log.d(TAG, "Hiding fragment");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    public void showFragment(Fragment fragment) {
        if (!silent)
            Log.d(TAG, "Showing fragment");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.show(fragment);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    public SupportFragmentActivity groupFragment(int id, Fragment fragment) {
        if (!silent)
            Log.d(TAG, "Loading fragment");
        return groupFragment(id, fragment, true);
    }

    public SupportFragmentActivity startTransaction() {
        if (!silent)
            Log.d(TAG, "Initializing transaction");
        transaction = getSupportFragmentManager().beginTransaction();
        return this;
    }

    public SupportFragmentActivity groupFragment(int id, Fragment fragment, boolean animate) {
        if (!silent)
            Log.d(TAG, "Group adding fragment");
        if (null == transaction)
            transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id, fragment);
        return this;
    }

    public int commitTransaction(boolean addToBackStack) {
        if (!silent)
            Log.d(TAG, "Commiting transaction");
        if (null != transaction) {
            if (addToBackStack)
                transaction.addToBackStack(null);
            else
                transaction.disallowAddToBackStack();
            return transaction.commit();
        }
        return 0;
    }

    public SupportFragmentActivity animateTransaction(int in, int out) {
        if (!silent)
            Log.d(TAG, "Animating transaction");
        if (null != transaction)
            transaction.setCustomAnimations(in, out);
        return this;
    }

    public SupportFragmentActivity animateTransaction(int in1, int out1, int in2, int out2) {
        if (!silent)
            Log.d(TAG, "Animating transaction");
        if (null != transaction)
            transaction.setCustomAnimations(in1, out1, in2, out2);
        return this;
    }

    public void addFragment(int id, Fragment fragment) {
        if (null != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(id, fragment);
            // transaction.setCustomAnimations(R.anim.simple_in,
            // R.anim.simple_out, R.anim.simple_in, R.anim.simple_out);
            transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }

    public void attachFragment(Fragment fragment) {
        if (null != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.attach(fragment);
            // transaction.setCustomAnimations(R.anim.simple_in,
            // R.anim.simple_out, R.anim.simple_in, R.anim.simple_out);
            transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }

    public void detachFragment(Fragment fragment) {
        if (null != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(fragment);
            // transaction.setCustomAnimations(R.anim.simple_in,
            // R.anim.simple_out, R.anim.simple_in, R.anim.simple_out);
            transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }

    public void loadFragment(int id, Fragment fragment) {
        if (!silent)
            Log.d(TAG, "Loading fragment");
        loadFragment(id, fragment, true, null);
    }

    public void loadFragment(int id, Fragment fragment, boolean addToBackStack) {
        if (!silent)
            Log.d(TAG, "Loading fragment");
        loadFragment(id, fragment, addToBackStack, null);
    }

    public void loadFragment(int id, Fragment fragment, boolean addToBackStack, String tag) {
        if (!silent)
            Log.d(TAG, "Loading fragment");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, fragment, tag);
        if (addToBackStack)
            ft.addToBackStack(null);
        else
            ft.disallowAddToBackStack();
        ft.commit();
    }

    public void showDialogFragment(DialogFragment fragment) {
        showDialogFragment(fragment, null);
    }

    public void showDialogFragment(DialogFragment fragment, String tag) {
        if (!silent)
            Log.d(TAG, "Loading dialog fragment");
        fragment.show(getSupportFragmentManager(), tag);
    }
}