package net.comfreeze.lib.views;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author james
 * @version %I%
 * @package ComFreeze Android Tools
 * @serial 8/9/13
 */
public class GestureHelper implements View.OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    public static final String TAG = GestureHelper.class.getSimpleName();
    private GestureDetector detector;
    private GestureHelperActionListener listener;
    private View view;

    public GestureHelper(Context context, GestureHelperActionListener listener) {
        this.detector = new GestureDetector(context, this);
        this.listener = listener;
    }

    public boolean onTouchEvent(View v, MotionEvent ev) {
        this.view = v;
        return detector.onTouchEvent(ev);
    }

    public boolean onTouch(View v, MotionEvent ev) {
        return onTouchEvent(v, ev);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (null != listener)
            return listener.onAction(ACTION.SINGLE_TAP, view, e);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (null != listener)
            return listener.onAction(ACTION.DOUBLE_TAP, view, e);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if (null != listener)
            return listener.onAction(ACTION.DOUBLE_TAP_EVENT, view, e);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (null != listener)
            return listener.onAction(ACTION.DOWN, view, e);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        if (null != listener)
            listener.onAction(ACTION.SHOW_PRESS, view, e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (null != listener)
            return listener.onAction(ACTION.SINGLE_TAP_UP, view, e);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (null != listener)
            return listener.onAction(ACTION.SCROLL, view, e1, e2, distanceX, distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (null != listener)
            listener.onAction(ACTION.LONG_PRESS, view, e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (null != listener)
            listener.onAction(ACTION.FLING, view, e1, e2, velocityX, velocityY);
        return false;
    }

    public static enum ACTION {
        SINGLE_TAP, DOUBLE_TAP, DOUBLE_TAP_EVENT, DOWN, LONG_PRESS, SHOW_PRESS, SINGLE_TAP_UP, SCROLL, FLING
    }

    public interface GestureHelperActionListener {
        public boolean onAction(ACTION action, View v, MotionEvent e);

        public boolean onAction(ACTION action, View v, MotionEvent e1, MotionEvent e2, float v1, float v2);
    }
}
