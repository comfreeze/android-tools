package net.comfreeze.lib.views;

import android.view.LayoutInflater;
import android.view.View;

abstract public class CFZViewHelper<E> {
    private static final String TAG = CFZViewHelper.class.getSimpleName();
    public static boolean silent = true;
    public View view;
    public Object data;
    public LayoutInflater inflater;
    private Class<E> clazz;

    public CFZViewHelper() {
        this.clazz = (Class<E>) this.getClass();
    }

    public E setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
        return (E) this;
    }

    public LayoutInflater getInflater() {
        return this.inflater;
    }

    public E setView(View view) {
        this.view = view;
        initView();
        this.view.setTag(this);
        return (E) this;
    }

    public View getView() {
        return this.view;
    }

    public E loadData(Object data) {
        this.data = data;
        processData(this.data);
        return (E) this;
    }

    abstract public E processData(Object data);

    abstract public E initView();
}