package net.comfreeze.lib.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ResizingView extends LinearLayout {
    OnResizeListener resizeListener = null;

    public ResizingView(Context context) {
        super(context);
    }

    public ResizingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnResizeListener(OnResizeListener onResizeListener) {
        resizeListener = onResizeListener;
    }

    private void onResizeEvent(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if (resizeListener != null) {
            resizeListener.onResize(oldWidth, oldHeight, newWidth, newHeight);
        }
    }

    public void toggleElement(int id, int visibility) {
        View v = (View) getRootView().findViewById(id);
        if (v != null) {
            Log.d("ResizingView", "Element toggled successfully!");
            v.setVisibility(visibility);
        } else {
            Log.d("ResizingView", "Element toggling failed, element not found!");
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("ResizingView", String.format("Resizing from %1$dx%2$d to %3$dx%4$d", oldw, oldh, w, h));
        super.onSizeChanged(w, h, oldw, oldh);
        new Handler().post(new Runnable() {
            public void run() {
                requestLayout();
            }
        });
        onResizeEvent(oldw, oldh, w, h);

    }

    public interface OnResizeListener {
        public abstract void onResize(int oldWidth, int oldHeight, int newWidth, int newHeight);
    }
}