package net.comfreeze.lib.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.comfreeze.lib.adapter.IHeaderListAdapter;

public class HeaderListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = HeaderListView.class.getSimpleName();
    private static final int MAX_ALPHA = 255;
    public static boolean silent = false;
    private IHeaderListAdapter adapter;
    private View headerView;
    private boolean headerViewVisible;
    private int headerViewWidth;
    private int headerViewHeight;

    public HeaderListView(Context context) {
        super(context);
    }

    public HeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public HeaderListView(Context context, AttributeSet attrs, int defStyle) {
//	super(context, attrs, defStyle);
//    }

    public HeaderListView setHeaderView(View view) {
        headerView = view;

        // Disable vertical fading when the header is present
        if (headerView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
        return this;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (!silent)
            Log.d(TAG, "Setting adapter");
        this.adapter = (IHeaderListAdapter) adapter;
        if (adapter instanceof OnScrollListener)
            this.setOnScrollListener((OnScrollListener) adapter);
        else
            this.setOnScrollListener(this);
    }

    public void onScroll(AbsListView list, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        configureHeaderView(firstVisibleItem);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (headerView != null && !isInEditMode()) {
            if (!silent)
                Log.d(TAG, "Header view found, measuring!");
            measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
            headerViewWidth = headerView.getMeasuredWidth();
            headerViewHeight = headerView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (headerView != null && !isInEditMode()) {
            if (!silent)
                Log.d(TAG, "Header view found, laying out!");
            // headerView.requestLayout();
            headerView.layout(0, 0, headerViewWidth, headerViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (headerView == null) {
            if (!silent)
                Log.d(TAG, "Header view not found, skipping configuration!");
            return;
        }
        if (!silent)
            Log.d(TAG, "Header view found, configuring!");
        // Default to hidden
        headerViewVisible = false;
        if (null == adapter)
            return;

        switch (adapter.getHeaderState(position)) {
            case GONE:
                if (!silent)
                    Log.d(TAG, "Header view hiding!");
                headerViewVisible = false;
                break;

            case VISIBLE:
                if (!silent)
                    Log.d(TAG, "Header view showing!");
                adapter.configureHeader(headerView, position, MAX_ALPHA);
                if (headerView.getTop() != 0) {
                    headerView.layout(0, 0, headerViewWidth, headerViewHeight);
                }
                headerViewVisible = true;
                break;

            case PUSHED_UP:
                if (!silent)
                    Log.d(TAG, "Header view pushed up!");
                View firstView = getChildAt(0);
                if (firstView != null) {
                    int bottom = firstView.getBottom();
                    int headerHeight = headerView.getHeight();
                    int y;
                    int alpha;
                    if (bottom < headerHeight) {
                        y = (bottom - headerHeight);
                        alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                    } else {
                        y = 0;
                        alpha = MAX_ALPHA;
                    }
                    adapter.configureHeader(headerView, position, alpha);
                    if (headerView.getTop() != y) {
                        // headerView.requestLayout();
                        headerView.layout(0, y, headerViewWidth, headerViewHeight + y);
                    }
                    headerViewVisible = true;
                } else if (!silent)
                    Log.e(TAG, "First child was null!");
                break;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!isInEditMode()) {
            if (headerViewVisible) {
                drawChild(canvas, headerView, getDrawingTime());
            }
        }
    }
}