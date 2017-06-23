package com.adrian.servodriver.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.servodriver.R;
import com.adrian.servodriver.adapter.ParamListAdapter;
import com.adrian.servodriver.pojo.ParamBean;
import com.blankj.utilcode.util.ToastUtils;
import com.ftdi.j2xx.D2xxManager;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sysu.zyb.panellistlibrary.PanelListLayout;

public class MainActivity extends BaseActivity {

    private Drawer mMenuDrawer;
    private ImageButton mMenuIB;

    private PanelListLayout mRootPLL;
    private ListView mContentLV;

    private List<ParamBean> contentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.picton_blue));
        mMenuIB = (ImageButton) findViewById(R.id.ib_menu);

        mMenuDrawer = new DrawerBuilder().withActivity(this).withSliderBackgroundDrawableRes(R.mipmap.menu_bg)
                .withDisplayBelowStatusBar(false).withTranslucentStatusBar(false)
                .withHeader(R.layout.layout_menu_header).withHeaderDivider(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.write_eeprom).withIcon(Octicons.Icon.oct_pencil),
                        new PrimaryDrawerItem().withName(R.string.coder_zero).withIcon(Octicons.Icon.oct_file_binary),
                        new PrimaryDrawerItem().withName(R.string.load_param).withIcon(Octicons.Icon.oct_zap),
                        new PrimaryDrawerItem().withName(R.string.save_param).withIcon(Octicons.Icon.oct_sign_in),
                        new PrimaryDrawerItem().withName(R.string.warning).withIcon(Octicons.Icon.oct_alert).withBadge("3").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.firmware_update).withIcon(Octicons.Icon.oct_ruby),
                        new PrimaryDrawerItem().withName(R.string.state_monitor).withIcon(Octicons.Icon.oct_eye),
                        new PrimaryDrawerItem().withName(R.string.auto_adjust).withIcon(Octicons.Icon.oct_settings),
                        new PrimaryDrawerItem().withName(R.string.recovery_setings).withIcon(Octicons.Icon.oct_sync),
                        new PrimaryDrawerItem().withName(R.string.fft).withIcon(Octicons.Icon.oct_graph),
                        new PrimaryDrawerItem().withName(R.string.help).withIcon(Octicons.Icon.oct_question),
                        new PrimaryDrawerItem().withName(R.string.about).withIcon(Octicons.Icon.oct_info)
                ).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                }).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem instanceof Nameable) {
                                Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                            }

                            if (drawerItem instanceof Badgeable) {
                                Badgeable badgeable = (Badgeable) drawerItem;
                                if (badgeable.getBadge() != null) {
                                    //note don't do this if your badge contains a "+"
                                    //only use toString() if you set the test as String
                                    int badge = Integer.valueOf(badgeable.getBadge().toString());
                                    if (badge > 0) {
                                        badgeable.withBadge(String.valueOf(badge - 1));
                                        mMenuDrawer.updateItem(drawerItem);
                                    }
                                }
                            }
                        }
                        return false;
                    }
                }).build();
        mMenuIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuDrawer.openDrawer();
            }
        });

        mRootPLL = (PanelListLayout) findViewById(R.id.pll_grid);
        mContentLV = (ListView) findViewById(R.id.lv_content);

        //设置listView为多选模式，长按自动触发
        mContentLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mContentLV.setMultiChoiceModeListener(new MultiChoiceModeCallback());

        //listView的点击监听
        mContentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showShortSafe("你选中的position为：" + position);
//                Toast.makeText(MainActivity.this, "你选中的position为：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void loadData() {
        initDataList();

        ParamListAdapter adapter = new ParamListAdapter(this, mRootPLL, mContentLV, R.layout.item_param, contentList);
        adapter.initAdapter();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (mMenuDrawer != null && mMenuDrawer.isDrawerOpen()) {
            mMenuDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 初始化content数据
     */
    private void initDataList() {
        String[] params = getResources().getStringArray(R.array.param_name);
        for (int i = 0; i < params.length; i++) {
            ParamBean data = new ParamBean();
            data.setParamName(params[i]);
            data.setMinValue("0.00");
            data.setMaxValue("0.00");
            data.setDefValue("0.00");
            data.setCurValue("TL05");
            data.setUnit("--");
            data.setType((int) (Math.random() * params.length % 3));
            contentList.add(data);
        }
    }

    /**
     * 多选模式的监听器
     */
    private class MultiChoiceModeCallback implements ListView.MultiChoiceModeListener {

        private View actionBarView;
        private TextView tv_selectedCount;

        /**
         * 进入ActionMode时调用
         * 可设置一些菜单
         *
         * @param mode
         * @param menu
         * @return
         */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            // menu
//            getMenuInflater().inflate(R.menu.menu_multichoice,menu);
//            // actionBar
//            if (actionBarView == null){
//                actionBarView = LayoutInflater.from(MainActivity.this).inflate(R.layout.actionbar_listviewmultichoice,null);
//                tv_selectedCount = (TextView) actionBarView.findViewById(R.id.id_tv_selectedCount);
//            }
//            mode.setCustomView(actionBarView);
            return true;
        }

        /**
         * 和onCreateActionMode差不多的时机调用，不写逻辑
         * @param mode
         * @param menu
         * @return
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /**
         * 当ActionMode的菜单项被点击时
         *
         * @param mode
         * @param item
         * @return
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()){
//                case R.id.id_menu_selectAll:
//                    for (int i = 0; i<lv_content.getAdapter().getCount();i++){
//                        lv_content.setItemChecked(i,true);
//                    }
//                    tv_selectedCount.setText(String.valueOf(lv_content.getAdapter().getCount()));
//                    break;
//                case R.id.id_menu_draw:
//                    //draw
//                    SparseBooleanArray booleanArray = lv_content.getCheckedItemPositions();
//                    Log.d("ybz", booleanArray.toString());
//
//                    List<Integer> checkedItemPositionList = new ArrayList<>();
//                    for (int i = 0; i<contentList.size();i++){
//                        if (lv_content.isItemChecked(i)){
//                            checkedItemPositionList.add(i);
//                            Log.d("ybz", "被选中的item： " + i);
//                        }
//                    }
//
//                    StringBuilder checkedItemString = new StringBuilder();
//                    for (int i = 0; i<checkedItemPositionList.size();i++){
//                        checkedItemString.append(checkedItemPositionList.get(i)+",");
//                    }
//
//                    Toast.makeText(MainActivity.this, "你选中的position有："+checkedItemString, Toast.LENGTH_SHORT).show();
//                    break;
//            }
            return true;
        }

        /**
         * 退出ActionMode时调用
         *
         * @param mode
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mContentLV.clearChoices();
        }

        /**
         * 当item的选中状态发生改变时调用
         * @param mode
         * @param position
         * @param id
         * @param checked
         */
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectedCount = mContentLV.getCheckedItemCount();
            tv_selectedCount.setText(String.valueOf(selectedCount));
            ((ArrayAdapter) mContentLV.getAdapter()).notifyDataSetChanged();
        }
    }
}
