package com.adrian.servodriver.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.adapter.LoadFileAdapter;
import com.adrian.servodriver.adapter.SaveFileAdapter;
import com.adrian.servodriver.pojo.FileBean;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranqing on 2017/6/24.
 */

public class LoadDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private ListView mFilesLV;
    private Button mLoadBtn;
    private Button mCancelBtn;

    private LoadFileAdapter mAdapter;
    private List<FileBean> data;

    public LoadDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.context = context;
    }

    public LoadDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected LoadDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_load_dialog);

        mFilesLV = (ListView) findViewById(R.id.lv_files);
        mLoadBtn = (Button) findViewById(R.id.btn_load);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);
        mFilesLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAdapter = new LoadFileAdapter(context);
        mFilesLV.setAdapter(mAdapter);

        mLoadBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        data = new ArrayList();
        for (int i = 0; i < 20; i++) {
            FileBean bean = new FileBean();
            bean.setFileName("文件" + i);
            bean.setCreateTime(System.currentTimeMillis() + i);
            data.add(bean);
        }
        mAdapter.setData(data);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mFilesLV.clearChoices();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load:
                if (mFilesLV.getCheckedItemCount() == 0) {
                    ToastUtils.showShortSafe(R.string.choose_one);
                } else {
                    int pos = mFilesLV.getCheckedItemPosition();
                    ToastUtils.showShortSafe("已加载" + data.get(pos).getFileName());
                    dismiss();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
