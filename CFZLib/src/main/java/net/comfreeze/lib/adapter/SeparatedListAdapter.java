package net.comfreeze.lib.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SeparatedListAdapter extends BaseAdapter {
    private static final String TAG = SeparatedListAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;

    private LayoutInflater inflater;
    private LinkedHashMap<String, Adapter> sections;
    private InsertableAdapter headers;

    public SeparatedListAdapter(Context context) {
        this(context, android.R.layout.simple_list_item_1);
    }

    public SeparatedListAdapter(Context context, int headerLayout) {
        sections = new LinkedHashMap<String, Adapter>();
        headers = new HeaderAdapter(headerLayout);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SeparatedListAdapter(Context context, InsertableAdapter headerAdapter) {
        sections = new LinkedHashMap<String, Adapter>();
        headers = headerAdapter;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SeparatedListAdapter addSection(String sectionName, Adapter adapter) {
        if (null != sectionName && null != adapter) {
            // TODO: Add empty adapter option, providing basic empty state row
            headers.add(sectionName);
            sections.put(sectionName, adapter);
        }
        return this;
    }

    public Adapter getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public int getCount() {
        int total = 0;
        for (Adapter adapter : sections.values()) {
            total += adapter.getCount() + 1;
        }
        return total;
    }

    public Object getItem(int position) {
        // Log.d(TAG, "getItem: " + position);
        for (Object section : sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;
            if (position == 0)
                return section;
            if (position < size)
                return adapter.getItem(position - 1);
            position -= size;
        }
        return null;
    }

    public long getItemId(int position) {
        if (getItemViewType(position) == TYPE_HEADER)
            return (long) position;
        else
            for (Object section : sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;
                if (position == 0)
                    return (long) position;
                if (position < size)
                    return adapter.getItemId(position - 1);
                position -= size;
            }
        return (long) position;
    }

    public int getViewTypeCount() {
        int total = 1;
        for (Adapter adapter : sections.values()) {
            total += adapter.getViewTypeCount();
        }
        Log.d(TAG, "View type count: " + total);
        return total;
    }

    @Override
    public int getItemViewType(int position) {
        // Log.d(TAG, "getItemViewType: " + position);
        int type = 1;
        for (Object section : sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;
            if (position == 0)
                return TYPE_HEADER;
            if (position < size)
                return type + adapter.getItemViewType(position - 1);
            // Log.d(TAG, "getItemViewType: next adapter");
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        // Log.d(TAG, "Item view type: " + getItemViewType(position));
        return (getItemViewType(position) > TYPE_HEADER);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.d(TAG, "getView: " + position);
        int sectionId = 0;
        for (Object section : sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;
            if (position == 0)
                return headers.getView(sectionId, convertView, parent);
            if (position < size)
                return adapter.getView(position - 1, convertView, parent);
            position -= size;
            sectionId++;
        }
        return null;
    }

    private class HeaderAdapter extends BaseAdapter implements InsertableAdapter {
        private ArrayList<String> collection;
        private int layout;

        @SuppressWarnings("unused")
        public HeaderAdapter() {
            this(android.R.layout.simple_list_item_1);
        }

        public HeaderAdapter(int layout) {
            collection = new ArrayList<String>();
            this.layout = layout;
        }

        public void add(String header) {
            collection.add(header);
        }

        public int getCount() {
            return collection.size();
        }

        public String getItem(int position) {
            return collection.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Log.d(TAG, "HeaderAdapter: getView: " + position);
            View view = inflater.inflate(layout, parent, false);
            String item = getItem(position);
            if (item != null && !item.equals("hidden")) {
                if (((TextView) view.findViewById(android.R.id.text1)) != null) {
                    ((TextView) view.findViewById(android.R.id.text1)).setText(item);
                }
            } else if (item.equals("hidden")) {
                LayoutParams params = view.getLayoutParams();
                params.height = 1;
                view.setLayoutParams(params);
                view.setVisibility(View.GONE);
            }
            return view;
        }
    }

    public static interface InsertableAdapter extends ListAdapter {
        public void add(String header);
    }
}