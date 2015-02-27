package net.comfreeze.lib.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SectionIndexer;
import android.widget.TextView;

import net.comfreeze.lib.views.HeaderListView;

abstract public class HeaderListAdapter extends CursorAdapter implements SectionIndexer, OnScrollListener, IHeaderListAdapter {
    private static final String TAG = HeaderListAdapter.class.getSimpleName();
    public SectionIndexer indexer;
    public int headerId = -1;
    public int dividerId = -1;
    private int backgroundId;

    public HeaderListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    public void onScroll(AbsListView list, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (list instanceof HeaderListView) {
            ((HeaderListView) list).configureHeaderView(firstVisibleItem);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public HeaderListAdapter setIndexer(SectionIndexer indexer) {
        this.indexer = indexer;
        return this;
    }

    public int getPositionForSection(int section) {
        if (indexer == null)
            return -1;
        return indexer.getPositionForSection(section);
    }

    public int getSectionForPosition(int position) {
        if (indexer == null)
            return -1;
        return indexer.getSectionForPosition(position);
    }

    public Object[] getSections() {
        if (indexer == null)
            return new String[]{""};
        else
            return indexer.getSections();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        bindSectionHeader(view, cursor.getPosition());
    }

    public HeaderState getHeaderState(int position) {
        if (indexer == null || getCount() == 0 || position < 0)
            return HeaderState.GONE;

        // The header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        int section = getSectionForPosition(position);
        int nextSectionPosition = getPositionForSection(section + 1);

        if (nextSectionPosition != -1 && position == nextSectionPosition - 1)
            return HeaderState.PUSHED_UP;

        return HeaderState.VISIBLE;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }

    public void setHeaderBackgroundDrawableId(int id) {
        this.backgroundId = id;
    }

    private void bindSectionHeader(View itemView, int position) {
        final TextView headerView = (TextView) itemView.findViewById(getHeaderId());
        final View dividerView = itemView.findViewById(dividerId);

        final int section = getSectionForPosition(position);
        if (getPositionForSection(section) == position) {
            String title = (String) indexer.getSections()[section];
            if (null != headerView)
                headerView.setText(title);
            toggleVisibility(headerView, View.VISIBLE);
            toggleVisibility(dividerView, View.GONE);
        } else {
            toggleVisibility(headerView, View.GONE);
            toggleVisibility(dividerView, View.VISIBLE);
        }

        // move the divider for the last item in a section
        if (getPositionForSection(section + 1) - 1 == position) {
            toggleVisibility(dividerView, View.GONE);
        } else {
            toggleVisibility(dividerView, View.VISIBLE);
        }
    }

    private void toggleVisibility(View view, int visible) {
        if (null != view)
            view.setVisibility(visible);
    }
}