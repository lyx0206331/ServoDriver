package com.adrian.servodriver.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.pojo.ParamBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sysu.zyb.panellistlibrary.PanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

/**
 * Created by qing on 2017/6/22.
 */

public class ParamListAdapter extends PanelListAdapter {

    private Context context;

    private ListView lv_content;
    private int contentResourceId;
    private List<ParamBean> contentList = new ArrayList<>();

    public ParamListAdapter(Context context, PanelListLayout pl_root, ListView lv_content) {
        super(context, pl_root, lv_content);
    }

    public ParamListAdapter(Context context, PanelListLayout pl_root, ListView lv_content,
                            int contentResourceId, List<ParamBean> contentList) {
        super(context, pl_root, lv_content);
        this.context = context;
        this.lv_content = lv_content;
        this.contentResourceId = contentResourceId;
        this.contentList = contentList;
    }

    /**
     * 调用父类方法进行同步控制
     * <p>
     * 自行编写Adapter并进行setAdapter
     */
    @Override
    public void initAdapter() {
        setTitle("名称\\值");//设置表的标题
        setTitleHeight(100);//设置表标题的高
        setTitleWidth(250);//设置表标题的宽
        setRowDataList(getRowDataList());//设置横向表头的内容
        setColumnDataList(getColumnDataList());

        // set自己写的contentAdapter
        ContentAdapter contentAdapter = new ContentAdapter(context, contentResourceId, contentList);
        lv_content.setAdapter(contentAdapter);

        super.initAdapter();//一定要在设置完后调用父类的方法
    }

    /**
     * 重写父类的该方法，返回数据的个数即可
     *
     * @return size of content
     */
    @Override
    protected int getCount() {
        return contentList.size();
    }

    /**
     * 生成一份横向表头的内容
     *
     * @return List<String>
     */
    private List<String> getRowDataList() {
        List<String> rowDataList = new ArrayList<>();
        String[] values = context.getResources().getStringArray(R.array.value_name);
        for (String value :
                values) {
            rowDataList.add(value);
        }
        return rowDataList;
    }

    private List<String> getColumnDataList() {
        List<String> colDataList = new ArrayList<>();
        String[] values = context.getResources().getStringArray(R.array.param_name);
        for (String value :
                values) {
            colDataList.add(value);
        }
        return colDataList;
    }

    /**
     * content部分的adapter
     */
    private class ContentAdapter extends ArrayAdapter {

        private List<ParamBean> contentList;
        private int resourceId;

        ContentAdapter(Context context, int resourceId, List<ParamBean> contentList) {
            super(context, resourceId);
            this.contentList = contentList;
            this.resourceId = resourceId;
        }

        @Override
        public int getCount() {
            return contentList.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ParamBean data = contentList.get(position);
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.mMinValueTV.setText(data.getMinValue());
            viewHolder.mMaxValueTV.setText(data.getMaxValue());
            viewHolder.mDefValueTV.setText(data.getDefValue());
            viewHolder.mCurValueET.setText(data.getCurValue());
            viewHolder.mUnit.setText(data.getUnit());

            if (lv_content.isItemChecked(position)) {
                view.setBackgroundColor(context.getResources().getColor(R.color.french_pass));
            } else {
                view.setBackgroundColor(context.getResources().getColor(R.color.colorDeselected));
            }

            return view;
        }

        private class ViewHolder {
            TextView mMinValueTV;
            TextView mMaxValueTV;
            TextView mDefValueTV;
            EditText mCurValueET;
            TextView mUnit;

            ViewHolder(View itemView) {
                mMinValueTV = (TextView) itemView.findViewById(R.id.tv_min_value);
                mMaxValueTV = (TextView) itemView.findViewById(R.id.tv_max_value);
                mDefValueTV = (TextView) itemView.findViewById(R.id.tv_def_value);
                mCurValueET = (EditText) itemView.findViewById(R.id.et_cur_value);
                mUnit = (TextView) itemView.findViewById(R.id.tv_unit);
            }
        }
    }
}
