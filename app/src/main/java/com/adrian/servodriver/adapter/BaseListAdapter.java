package com.adrian.servodriver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranqing on 2017/6/24.
 */

public class BaseListAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> data;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addItem(T t) {
        data.add(t);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
