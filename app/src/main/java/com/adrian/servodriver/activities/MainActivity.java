package com.adrian.servodriver.activities;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.adrian.servodriver.R;
import com.ftdi.j2xx.D2xxManager;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

public class MainActivity extends BaseActivity {

    private Drawer mMenuDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        Button menu = (Button) findViewById(R.id.btn_menu);

        mMenuDrawer = new DrawerBuilder().withActivity(this).withSliderBackgroundDrawableRes(R.mipmap.bg)
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
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuDrawer.openDrawer();
            }
        });
    }

    @Override
    protected void loadData() {

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
}
