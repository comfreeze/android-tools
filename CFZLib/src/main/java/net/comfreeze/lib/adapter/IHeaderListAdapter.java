package net.comfreeze.lib.adapter;

import android.view.View;

public interface IHeaderListAdapter {
    public static enum HeaderState {
        GONE, // 0
        VISIBLE, // 1
        PUSHED_UP // 2
    }

    /**
     * Computes the desired state of the pinned header for the given position of
     * the first visible list item.
     */
    HeaderState getHeaderState(int position);

    /**
     * Return an ID for locating the header layout.
     * @return int
     */
    abstract public int getHeaderId();
    
    /**
     * Configures the header view to match the first visible list item.
     *
     * @param header   header view.
     * @param position position of the first visible list item.
     * @param alpha    fading of the header view, between 0 and 255.
     */
    void configureHeader(View header, int position, int alpha);

    /**
     * Configures the header view background drawable when alpha is applied
     */
    void setHeaderBackgroundDrawableId(int resourceId);
}