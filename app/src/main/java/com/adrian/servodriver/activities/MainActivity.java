package com.adrian.servodriver.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.adrian.servodriver.R;
import com.adrian.servodriver.adapter.ParamListAdapter;
import com.adrian.servodriver.pojo.ParamBean;
import com.adrian.servodriver.views.ExceptDialog;
import com.adrian.servodriver.views.LoadDialog;
import com.adrian.servodriver.views.SaveDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import sysu.zyb.panellistlibrary.PanelListLayout;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long lastPressTime;

    private Drawer mMenuDrawer;
    private ImageButton mMenuIB;
    private FrameLayout mWarningFL;
    private ImageView mWaringPointIV;

    private PanelListLayout mRootPLL;
    private ListView mContentLV;
    private LinearLayout mWriteLL;
    private LinearLayout mZeroLL;
    private LinearLayout mSaveLL;
    private LinearLayout mLoadLL;
    private LinearLayout mEepromLL;
    private LinearLayout mRecoveryLL;
    private FloatingActionButton mAddFAB;
    private FloatingActionButton mWriteFAB;
    private FloatingActionButton mZeroFAB;
    private FloatingActionButton mSaveFAB;
    private FloatingActionButton mLoadFAB;
    private FloatingActionButton mEepromFAB;
    private FloatingActionButton mRecoveryFAB;

    private List<ParamBean> contentList = new ArrayList<>();

    private Animation fabOpenAnimation;
    private Animation fabCloseAnimation;
    private boolean isFabMenuOpen = false;

    private SaveDialog mSaveFileDialog;
    private LoadDialog mLoadParamDialog;
    private ExceptDialog mExceptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTransparent(this);
        mMenuIB = (ImageButton) findViewById(R.id.ib_menu);
        mWarningFL = (FrameLayout) findViewById(R.id.fl_warning);
        mWaringPointIV = (ImageView) findViewById(R.id.iv_warning_point);

        mMenuDrawer = new DrawerBuilder().withActivity(this).withSliderBackgroundDrawableRes(R.mipmap.menu_bg)
                .withDisplayBelowStatusBar(true).withTranslucentStatusBar(false)
                .withHeader(R.layout.layout_menu_header).withHeaderDivider(true)
                .addDrawerItems(
//                        new PrimaryDrawerItem().withName(R.string.write_eeprom).withIcon(Octicons.Icon.oct_pencil),
//                        new PrimaryDrawerItem().withName(R.string.coder_zero).withIcon(Octicons.Icon.oct_file_binary),
//                        new PrimaryDrawerItem().withName(R.string.load_param).withIcon(Octicons.Icon.oct_zap),
//                        new PrimaryDrawerItem().withName(R.string.save_param).withIcon(Octicons.Icon.oct_sign_in),
                        new PrimaryDrawerItem().withName(R.string.warning).withIcon(Octicons.Icon.oct_alert).withSelectedColorRes(R.color.menu_bg).withBadge("1").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.firmware_update).withIcon(Octicons.Icon.oct_ruby).withSelectedColorRes(R.color.menu_bg),
                        new PrimaryDrawerItem().withName(R.string.state_monitor).withIcon(Octicons.Icon.oct_eye).withSelectedColorRes(R.color.menu_bg),
                        new PrimaryDrawerItem().withName(R.string.auto_adjust).withIcon(Octicons.Icon.oct_settings).withSelectedColorRes(R.color.menu_bg),
//                        new PrimaryDrawerItem().withName(R.string.recovery_setings).withIcon(Octicons.Icon.oct_sync),
                        new PrimaryDrawerItem().withName(R.string.fft).withIcon(Octicons.Icon.oct_graph).withSelectedColorRes(R.color.menu_bg),
                        new PrimaryDrawerItem().withName(R.string.help).withIcon(Octicons.Icon.oct_question).withSelectedColorRes(R.color.menu_bg),
                        new PrimaryDrawerItem().withName(R.string.about).withIcon(Octicons.Icon.oct_info).withSelectedColorRes(R.color.menu_bg)
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
//                            LogUtils.e("MENU", "pos" + position);
                            switch (position) {
                                case 1:
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
                                    goWarningPage();
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    startActivity(StateMonitorActivity.class);
                                    break;
                                case 4:
                                    startActivity(AutoAdjustActivity.class);
                                    break;
                                case 5:
                                    startActivity(FFTActivity.class);
                                    break;
                                case 6:
                                    startActivity(HelpActivity.class);
                                    break;
                                case 7:
                                    startActivity(AboutActivity.class);
                                    break;
                                default:
                                    break;
                            }
//                            if (drawerItem instanceof Nameable) {
//                                Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
//                            }

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
        mMenuIB.setOnClickListener(this);
        mWarningFL.setOnClickListener(this);

        mRootPLL = (PanelListLayout) findViewById(R.id.pll_grid);
        mContentLV = (ListView) findViewById(R.id.lv_content);

        //设置listView为多选模式，长按自动触发
//        mContentLV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        mContentLV.setMultiChoiceModeListener(new MultiChoiceModeCallback());

//        //listView的点击监听
//        mContentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToastUtils.showShortSafe("你选中的position为：" + position);
//                Toast.makeText(MainActivity.this, "你选中的position为：" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        mWriteLL = (LinearLayout) findViewById(R.id.ll_commit);
        mZeroLL = (LinearLayout) findViewById(R.id.ll_zero);
        mSaveLL = (LinearLayout) findViewById(R.id.ll_save);
        mLoadLL = (LinearLayout) findViewById(R.id.ll_load);
        mEepromLL = (LinearLayout) findViewById(R.id.ll_eeprom);
        mRecoveryLL = (LinearLayout) findViewById(R.id.ll_recovery);
        mAddFAB = (FloatingActionButton) findViewById(R.id.fab_add);
        mWriteFAB = (FloatingActionButton) findViewById(R.id.fab_commit);
        mZeroFAB = (FloatingActionButton) findViewById(R.id.fab_zero);
        mSaveFAB = (FloatingActionButton) findViewById(R.id.fab_save);
        mLoadFAB = (FloatingActionButton) findViewById(R.id.fab_load);
        mEepromFAB = (FloatingActionButton) findViewById(R.id.fab_eeprom);
        mRecoveryFAB = (FloatingActionButton) findViewById(R.id.fab_recovery);
        mAddFAB.setOnClickListener(this);
        mWriteFAB.setOnClickListener(this);
        mZeroFAB.setOnClickListener(this);
        mSaveFAB.setOnClickListener(this);
        mLoadFAB.setOnClickListener(this);
        mEepromFAB.setOnClickListener(this);
        mRecoveryFAB.setOnClickListener(this);

        getAnimations();
    }

    private void goWarningPage() {
        IDrawerItem drawerItem = mMenuDrawer.getDrawerItem(1);
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
        mWaringPointIV.setVisibility(View.GONE);
        startActivity(WarnActivity.class);
    }

    @Override
    protected void loadData() {
        initDataList();

        ParamListAdapter adapter = new ParamListAdapter(this, mRootPLL, mContentLV, R.layout.item_param, contentList);
        adapter.initAdapter();
    }

    @Override
    public void onBackPressed() {
        if (mMenuDrawer != null && mMenuDrawer.isDrawerOpen()) {
            mMenuDrawer.closeDrawer();
        } else if (isFabMenuOpen) {
            collapseFabMenu();
        } else if (System.currentTimeMillis() - lastPressTime > 3000) {
            ToastUtils.showShortSafe(R.string.quit_prompt);
            lastPressTime = System.currentTimeMillis();
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

    private void getAnimations() {

        fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);

        fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);

    }

    private void expandFabMenu() {

        ViewCompat.animate(mAddFAB).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        mZeroLL.startAnimation(fabOpenAnimation);
        mWriteLL.startAnimation(fabOpenAnimation);
        mSaveLL.startAnimation(fabOpenAnimation);
        mLoadLL.startAnimation(fabOpenAnimation);
        mEepromLL.startAnimation(fabOpenAnimation);
        mRecoveryLL.startAnimation(fabOpenAnimation);
        mZeroFAB.setClickable(true);
        mWriteFAB.setClickable(true);
        mSaveFAB.setClickable(true);
        mLoadFAB.setClickable(true);
        mEepromFAB.setClickable(true);
        mRecoveryFAB.setClickable(true);
        isFabMenuOpen = true;


    }

    private void collapseFabMenu() {

        ViewCompat.animate(mAddFAB).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        mZeroLL.startAnimation(fabCloseAnimation);
        mWriteLL.startAnimation(fabCloseAnimation);
        mSaveLL.startAnimation(fabCloseAnimation);
        mLoadLL.startAnimation(fabCloseAnimation);
        mEepromLL.startAnimation(fabCloseAnimation);
        mRecoveryLL.startAnimation(fabCloseAnimation);
        mZeroFAB.setClickable(false);
        mWriteFAB.setClickable(false);
        mSaveFAB.setClickable(false);
        mLoadFAB.setClickable(false);
        mEepromFAB.setClickable(false);
        mRecoveryFAB.setClickable(false);
        isFabMenuOpen = false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_menu:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                mMenuDrawer.openDrawer();
                break;
            case R.id.fl_warning:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                goWarningPage();
                break;
            case R.id.fab_add:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                } else {
                    expandFabMenu();
                }
                break;
            case R.id.fab_commit:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                ToastUtils.showShortSafe(R.string.write);
                showExcDialog("写入伺服驱动器失败");
                break;
            case R.id.fab_zero:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                ToastUtils.showShortSafe(R.string.coder_zero);
                break;
            case R.id.fab_save:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                if (mSaveFileDialog == null) {
                    mSaveFileDialog = new SaveDialog(this);
                }
                mSaveFileDialog.show();
                break;
            case R.id.fab_load:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                if (mLoadParamDialog == null) {
                    mLoadParamDialog = new LoadDialog(this);
                }
                mLoadParamDialog.show();
                break;
            case R.id.fab_eeprom:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                ToastUtils.showShortSafe(R.string.write_eeprom);
                break;
            case R.id.fab_recovery:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                ToastUtils.showShortSafe(R.string.recovery_setings);
                break;
        }
    }

    private void showExcDialog(String content) {
        if (mExceptDialog == null) {
            mExceptDialog = new ExceptDialog(this);
        }
//        mExceptDialog.setContent(content);
        mExceptDialog.show();
    }

    /**
     * 多选模式的监听器
     */
    private class MultiChoiceModeCallback implements ListView.MultiChoiceModeListener {

//        private View actionBarView;
//        private TextView tv_selectedCount;

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
//            tv_selectedCount.setText(String.valueOf(selectedCount));
            ((ArrayAdapter) mContentLV.getAdapter()).notifyDataSetChanged();
        }
    }
}
