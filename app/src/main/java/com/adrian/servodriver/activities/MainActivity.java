package com.adrian.servodriver.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adrian.servodriver.R;
import com.adrian.servodriver.adapter.ParamListAdapter;
import com.adrian.servodriver.pojo.ParamBean;
import com.adrian.servodriver.theme_picker.ThemeResourceHelper;
import com.adrian.servodriver.utils.CommUtil;
import com.adrian.servodriver.utils.D2xxUtil;
import com.adrian.servodriver.views.ExceptDialog;
import com.adrian.servodriver.views.LoadDialog;
import com.adrian.servodriver.views.SaveDialog;
import com.adrian.servodriver.views.WarningDialog;
import com.blankj.utilcode.util.ToastUtils;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.List;

import sysu.zyb.panellistlibrary.PanelListLayout;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long lastPressTime;

    private RelativeLayout mMainRootRL;
    private RelativeLayout mSegmentBgRL;
    private TextView mSegmentTextTV;
    private TextView mRebootTextTV;
    private TextView mImmediatelyTextTV;
    private TextView mReadOnlyTextTV;
    private ImageView mRebootIV;
    private ImageView mImmediatelyIV;
    private ImageView mReadOnlyIV;
    private Drawer mMenuDrawer;
    private ImageButton mMenuIB;
    private FrameLayout mWarningFL;
    private TextView mWaringPointTV;
    private BubbleSeekBar mSegmentNumBSB;

    private PanelListLayout mRootPLL;
    private ListView mContentLV;
    private LinearLayout mWriteLL;
    private LinearLayout mZeroLL;
    private LinearLayout mSaveLL;
    private LinearLayout mLoadLL;
    private LinearLayout mEepromLL;
    private LinearLayout mRecoveryLL;
    private LinearLayout mFabMenusLL;
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

    private Animation pointAnimation;
    private boolean hasError = false;

    private SaveDialog mSaveFileDialog;
    private LoadDialog mLoadParamDialog;
    private ExceptDialog mExceptDialog;
    private WarningDialog mWarningDialog;

    private ParamListAdapter adapter;
    private ThemeResourceHelper helper;

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
        helper = ThemeResourceHelper.getInstance(this);
        mMainRootRL = (RelativeLayout) findViewById(R.id.main_bg);
        mMenuIB = (ImageButton) findViewById(R.id.ib_menu);
        mWarningFL = (FrameLayout) findViewById(R.id.fl_warning);
        mWaringPointTV = (TextView) findViewById(R.id.tv_warning_point);
        mSegmentNumBSB = (BubbleSeekBar) findViewById(R.id.bsb_segment_num);
        mSegmentBgRL = (RelativeLayout) findViewById(R.id.rl_color_label);
        mSegmentTextTV = (TextView) findViewById(R.id.tv_seg_num);
        mRebootTextTV = (TextView) findViewById(R.id.tv_reboot);
        mImmediatelyTextTV = (TextView) findViewById(R.id.tv_immediately);
        mReadOnlyTextTV = (TextView) findViewById(R.id.tv_read_only);
        mRebootIV = (ImageView) findViewById(R.id.iv_reboot);
        mImmediatelyIV = (ImageView) findViewById(R.id.iv_immediately);
        mReadOnlyIV = (ImageView) findViewById(R.id.iv_read_only);

        setWarningCode("Error30");

        mSegmentNumBSB.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                ToastUtils.showShortSafe("段号：" + progress);
            }
        });

        int selectedColor = helper.getColorByAttr(R.attr.menu_drawer_selected_color);
        int textColor = helper.getColorByAttr(R.attr.menu_drawer_text_color);
        mMenuDrawer = new DrawerBuilder().withActivity(this).withSliderBackgroundColor(helper.getColorByAttr(R.attr.menu_drawer_bg_color))
                .withDisplayBelowStatusBar(true).withTranslucentStatusBar(false)
                .withHeader(helper.getIdentifierByAttrId(R.attr.menu_header_bg)).withHeaderDivider(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.firmware_update).withTextColor(textColor).withIcon(Octicons.Icon.oct_ruby).withSelectedColor(selectedColor),
                        new PrimaryDrawerItem().withName(R.string.state_monitor).withTextColor(textColor).withIcon(Octicons.Icon.oct_eye).withSelectedColor(selectedColor),
                        new PrimaryDrawerItem().withName(R.string.auto_adjust).withTextColor(textColor).withIcon(Octicons.Icon.oct_settings).withSelectedColor(selectedColor),
                        new PrimaryDrawerItem().withName(R.string.fft).withTextColor(textColor).withIcon(Octicons.Icon.oct_graph).withSelectedColor(selectedColor),
                        new PrimaryDrawerItem().withName(R.string.help).withTextColor(textColor).withIcon(Octicons.Icon.oct_question).withSelectedColor(selectedColor),
                        new PrimaryDrawerItem().withName(R.string.about).withTextColor(textColor).withIcon(Octicons.Icon.oct_info).withSelectedColor(selectedColor)
                ).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        setWarningCode("Error40");
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
                                    showExcDialog(getString(R.string.usb_read_error));
                                    break;
                                case 2:
                                    startActivity(StateMonitorActivity.class);
                                    break;
                                case 3:
                                    startActivity(AutoAdjustActivity.class);
                                    break;
                                case 4:
                                    startActivity(FFTActivity.class);
                                    break;
                                case 5:
                                    startActivity(HelpActivity.class);
                                    break;
                                case 6:
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
//        mContentLV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
        mFabMenusLL = (LinearLayout) findViewById(R.id.ll_fab_menus);
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
        mWaringPointTV.setVisibility(View.GONE);
        startActivity(WarnActivity.class);
    }

    @Override
    protected void loadData() {
        initDataList();

        adapter = new ParamListAdapter(this, mRootPLL, mContentLV, R.layout.item_param, contentList);
        adapter.initAdapter(getThemeTag());
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

    @Override
    protected void onDestroy() {
        D2xxUtil.getInstance().onDestroy();
        super.onDestroy();
    }

    /**
     * 初始化content数据
     */
    private void initDataList() {
        String[] params = getResources().getStringArray(R.array.param_name);
        for (int i = 0; i < params.length; i++) {
            ParamBean data = new ParamBean();
            data.setIndexId(i);
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

    private void setWarningCode(String errorCode) {
        startPointAnim(false);
        mWaringPointTV.setText(errorCode);
        hasError = !TextUtils.isEmpty(errorCode);
        startPointAnim(hasError);
        mWaringPointTV.setVisibility(hasError ? View.VISIBLE : View.GONE);
    }

    private void startPointAnim(boolean isDoing) {
        if (pointAnimation == null) {
            pointAnimation = AnimationUtils.loadAnimation(this, R.anim.point_flash);
        }
        if (isDoing) {
            mWaringPointTV.startAnimation(pointAnimation);
        } else {
            mWaringPointTV.clearAnimation();
        }
    }

    private void getAnimations() {

        fabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);

        fabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);

    }

    private void expandFabMenu() {

        ViewCompat.animate(mAddFAB).rotation(45.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        mZeroFAB.setClickable(true);
        mWriteFAB.setClickable(true);
        mSaveFAB.setClickable(true);
        mLoadFAB.setClickable(true);
        mEepromFAB.setClickable(true);
        mRecoveryFAB.setClickable(true);
        mFabMenusLL.startAnimation(fabOpenAnimation);
        isFabMenuOpen = true;


    }

    private void collapseFabMenu() {

        ViewCompat.animate(mAddFAB).rotation(0.0F).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        mZeroFAB.setClickable(false);
        mWriteFAB.setClickable(false);
        mSaveFAB.setClickable(false);
        mLoadFAB.setClickable(false);
        mEepromFAB.setClickable(false);
        mRecoveryFAB.setClickable(false);
        mFabMenusLL.startAnimation(fabCloseAnimation);
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
//                if (isFabMenuOpen) {
//                    collapseFabMenu();
//                }
//                goWarningPage();
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                if (hasError) {
                    setWarningCode(null);
                    showWarningDialog("Err30", "EEROM写入时错误", "EEROM芯片损坏", "重新上电检查故障不消失请更换驱动器！");
                } else {
                    CommUtil.showToast("无错误日志!");
                }
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
                if (D2xxUtil.getInstance().isConnected()) {
                    List<ParamBean> data = adapter.getModifiedBeans();
                    if (data != null && data.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (ParamBean bean :
                                data) {
                            sb.append(bean.getIndexId() + "--" + bean.getCurValue() + " ");
                        }
                        ToastUtils.showShortSafe("写入数据：" + sb.toString());
                        adapter.cleanModifiedData();
                    } else {
                        ToastUtils.showShortSafe("无修改数据!");
                    }
                }
//                showExcDialog(getString(R.string.write_error));
                break;
            case R.id.fab_zero:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
//                ToastUtils.showShortSafe(R.string.coder_zero);
                showExcDialog(getString(R.string.set_coder_zero_error));
                break;
            case R.id.fab_save:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                mSaveFileDialog = null;
                if (mSaveFileDialog == null) {
                    mSaveFileDialog = new SaveDialog(this);
                }
                mSaveFileDialog.show();
                break;
            case R.id.fab_load:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
                mLoadParamDialog = null;
                if (mLoadParamDialog == null) {
                    mLoadParamDialog = new LoadDialog(this);
                }
                mLoadParamDialog.show();
                break;
            case R.id.fab_eeprom:
                if (isFabMenuOpen) {
                    collapseFabMenu();
                }
//                ToastUtils.showShortSafe(R.string.write_eeprom);
                showExcDialog(getString(R.string.write_eeprom_error));
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
        mExceptDialog = null;
        if (mExceptDialog == null) {
            mExceptDialog = new ExceptDialog(this);
        }
        mExceptDialog.show();
        mExceptDialog.setContent(content);
    }

    private void showWarningDialog(String code, String content, String reason, String solution) {
        mWarningDialog = null;
        if (mWarningDialog == null) {
            mWarningDialog = new WarningDialog(this);
        }
        mWarningDialog.show();
        mWarningDialog.setErrCode(code).setErrContent(content).setErrReason(reason).setSolution(solution);
    }

    @Override
    public void notifyByThemeChanged() {
//        ThemeResourceHelper helper = ThemeResourceHelper.getInstance(this);
        helper.setBackgroundResourceByAttr(mMainRootRL, R.attr.base_bg);

        helper.setBackgroundResourceByAttr(mSegmentBgRL, R.attr.segment_bg);
        helper.setTextColorByAttr(mSegmentTextTV, R.attr.segment_font_color);
        helper.setTextColorByAttr(mRebootTextTV, R.attr.state_font_color);
        helper.setTextColorByAttr(mImmediatelyTextTV, R.attr.state_font_color);
        helper.setTextColorByAttr(mReadOnlyTextTV, R.attr.state_font_color);
        helper.setImageResourceByAttr(mRebootIV, R.attr.reboot_state_icon);
        helper.setImageResourceByAttr(mImmediatelyIV, R.attr.immediately_state_icon);
        helper.setImageResourceByAttr(mReadOnlyIV, R.attr.read_only_state_icon);

        helper.setFloatingActionBtnBgTint(mAddFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mAddFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mWriteFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mWriteFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mSaveFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mSaveFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mLoadFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mLoadFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mZeroFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mZeroFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mEepromFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mEepromFAB, R.attr.fab_ripple);

        helper.setFloatingActionBtnBgTint(mRecoveryFAB, R.attr.fab_bg);
        helper.setFloatingActionBtnRipple(mRecoveryFAB, R.attr.fab_ripple);

        updateMenuDrawer();
        updateSegmentSeekbar();
    }

    /**
     * 更新侧滑菜单样式
     */
    private void updateMenuDrawer() {
        mMenuDrawer.setHeader(helper.getViewByAttr(R.attr.menu_header_bg));
        mMenuDrawer.getRecyclerView().setBackgroundColor(helper.getColorByAttr(R.attr.menu_drawer_bg_color));
        List<IDrawerItem> items = mMenuDrawer.getDrawerItems();
        for (IDrawerItem item :
                items) {
            ((PrimaryDrawerItem) item).withTextColor(helper.getColorByAttr(R.attr.menu_drawer_text_color)).withSelectedColor(helper.getColorByAttr(R.attr.menu_drawer_selected_color))/*.withSelectedColorRes(selectedColorId)*/;
        }
        mMenuDrawer.setItems(items);
    }

    private void updateSegmentSeekbar() {
        mSegmentNumBSB.getConfigBuilder().trackColor(helper.getColorByAttr(R.attr.bubble_seekbar_track)).secondTrackColor(helper.getColorByAttr(R.attr.bubble_seekbar_second_track))
                .bubbleColor(helper.getColorByAttr(R.attr.bubble_seekbar_color)).bubbleTextColor(helper.getColorByAttr(R.attr.bubble_seekbar_text_color)).build();
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
