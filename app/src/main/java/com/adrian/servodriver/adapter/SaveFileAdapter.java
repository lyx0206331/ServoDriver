package com.adrian.servodriver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.pojo.FileBean;

/**
 * Created by ranqing on 2017/6/24.
 */

public class SaveFileAdapter extends BaseListAdapter<FileBean> {
    public SaveFileAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_save_file, null, false);
            holder = new ViewHolder();
            holder.mFileNameTV = (TextView) convertView.findViewById(R.id.tv_file_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileBean item = (FileBean) getItem(position);
        holder.mFileNameTV.setText(item.getFileName());
        return convertView;
    }

    class ViewHolder {
        TextView mFileNameTV;
    }
}
