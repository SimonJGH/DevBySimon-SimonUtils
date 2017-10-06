package com.simon.utils;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.simon.utils.activity.BaseActivity;
import com.simon.utils.customview.SlidingMenu;
import com.simon.utils.fragment.LeftMenuFragment;
import com.simon.utils.fragment.RightHomeFragment;
import com.simon.utils.widget.recycler.SDRecyclerView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("all")
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity  {

    @ViewInject(R.id.iv_toggle)
    ImageView mIv_toogle;
    @ViewInject(R.id.sliding_menu)
    SlidingMenu mSliding_menu;
    @ViewInject(R.id.rv_left_menu)
    SDRecyclerView mRv_left_menu;

    private static boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment leftMenuFragment = new LeftMenuFragment();
        fm.beginTransaction().replace(R.id.fl_left_content, leftMenuFragment).commit();
        fm.beginTransaction().replace(R.id.fl_right_content, new RightHomeFragment()).commit();

    }

    @Event(value = {R.id.iv_toggle})
    private void clickButton(View view) {
        switch (view.getId()) {
            case R.id.iv_toggle:
                slidingMenuToggle();
                break;
        }
    }

    /**
     * slidingmenu 开关
     */
    public void slidingMenuToggle() {
        mSliding_menu.toggle();
    }

    /*双击退出*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();// 这里也可以弹出对话框
        }
        return false;
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "在按一次退出程序", Toast.LENGTH_SHORT).show();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }

}
