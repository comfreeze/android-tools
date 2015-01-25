package net.comfreeze.lib.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.comfreeze.lib.R;

abstract public class CFZListFragment extends Fragment {
    private static final String TAG = CFZListFragment.class.getSimpleName();

    protected OnItemSelectListener listener;

    protected View emptyContainer;
    protected View listContainer;
    protected View listTitleDivider;

    protected TextView title;
    protected TextView intro;

    protected ImageView introIndicator;

    protected ListView list;

    protected Cursor cursor;

    protected ListAdapter adapter;

    protected int layoutId = -1;

    public CFZListFragment() {
        Log.d(TAG, "Initializing");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating view");
        View view = null;
        if (layoutId > -1) {
            view = inflater.inflate(layoutId, container, false);
            emptyContainer = view.findViewById(R.id.empty_container);
            intro = (TextView) view.findViewById(R.id.list_intro);
            introIndicator = (ImageView) view.findViewById(R.id.list_intro_indicator);
            listContainer = view.findViewById(R.id.list_container);
            list = (ListView) view.findViewById(R.id.list);
            list.setOnItemClickListener(getClickListener());
            list.setOnItemLongClickListener(getLongClickListener());
            title = (TextView) view.findViewById(R.id.list_title);
            listTitleDivider = view.findViewById(R.id.list_title_divider);
        }
        return view;
    }

    public interface OnItemSelectListener {
        public void onItemSelected(View v);

        public void onItemLongPressed(View v);

        public void onCancel();
    }

    abstract public CFZListFragment newInstace();

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    protected OnItemClickListener clickListener_select = //
            new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.d(TAG, "Item clicked!");
                    if (null != listener) {
                        listener.onItemSelected(view);
                    }
                }
            };

    protected OnItemLongClickListener longClickListener_select = //
            new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.d(TAG, "Item long clicked!");
                    if (null != listener) {
                        listener.onItemLongPressed(view);
                    }
                    return false;
                }
            };

    public View getListTitleDivider() {
        return listTitleDivider;
    }

    public void setListTitleDivider(View listTitleDivider) {
        this.listTitleDivider = listTitleDivider;
    }

    public TextView getIntro() {
        return intro;
    }

    public void setIntro(TextView intro) {
        this.intro = intro;
    }

    public ImageView getIntroIndicator() {
        return introIndicator;
    }

    public void setIntroIndicator(ImageView introIndicator) {
        this.introIndicator = introIndicator;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public OnItemSelectListener getListener() {
        return listener;
    }

    public void setListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    public View getEmptyContainer() {
        return emptyContainer;
    }

    public void setEmptyContainer(View emptyContainer) {
        this.emptyContainer = emptyContainer;
    }

    public View getListContainer() {
        return listContainer;
    }

    public void setListContainer(View listContainer) {
        this.listContainer = listContainer;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public ListView getList() {
        return list;
    }

    public void setList(ListView list) {
        this.list = list;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public OnItemClickListener getClickListener() {
        return clickListener_select;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener_select = clickListener;
    }

    public OnItemLongClickListener getLongClickListener() {
        return longClickListener_select;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener_select = longClickListener;
    }
}