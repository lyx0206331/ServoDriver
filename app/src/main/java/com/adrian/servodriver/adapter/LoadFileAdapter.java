package com.adrian.servodriver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.pojo.FileBean;

/**
 * Created by ranqing on 2017/6/24.
 */

public class LoadFileAdapter extends BaseListAdapter<FileBean> {
    public LoadFileAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_load_file, null, false);
            holder = new ViewHolder();
            holder.mFileNameCTV = (CheckedTextView) convertView.findViewById(R.id.ctv_file_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileBean item = (FileBean) getItem(position);
        holder.mFileNameCTV.setText(item.getFileName());
        return convertView;
    }

    class ViewHolder {
        CheckedTextView mFileNameCTV;
    }
}
