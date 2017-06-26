package com.adrian.servodriver.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.pojo.StateBean;

/**
 * Created by ranqing on 2017/6/27.
 */

public class StateMonitorAdapter extends BaseListAdapter<StateBean> {
    public StateMonitorAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_state_monitor, null, false);
            holder = new ViewHolder();
            holder.mNameTV = (TextView) convertView.findViewById(R.id.tv_state_name);
            holder.mValueTV = (TextView) convertView.findViewById(R.id.tv_state_value);
            holder.mUnitTV = (TextView) convertView.findViewById(R.id.tv_state_unit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        StateBean item = (StateBean) getItem(position);
        holder.mNameTV.setText(item.getStateName());
        holder.mValueTV.setText(item.getStateValue() + "");
        holder.mUnitTV.setText(item.getStateUnit());
        return convertView;
    }

    class ViewHolder {
        TextView mNameTV;
        TextView mValueTV;
        TextView mUnitTV;
    }
}
