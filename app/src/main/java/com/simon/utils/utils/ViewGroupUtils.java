package com.simon.utils.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.R.attr.scaleX;
import static android.R.attr.scaleY;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * @author Simon
 * @Description ViewGroups相关的辅助类
 * @date createTime: 2016-11-28
 */
public class ViewGroupUtils {

    /**
     * 动态设置view的高度 屏幕宽度为全屏
     *
     * @param act
     * @param view
     * @param w
     * @param h
     */
    public void setViewHeight(Activity act, View view, int w, int h) {
        WindowManager wm = ((Activity) act).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        LayoutParams lp = view.getLayoutParams();
        lp.height = width * h / w;
        view.setLayoutParams(lp);
    }

    /**
     * 动态设置view的高度 屏幕宽度不为全屏
     *
     * @param act
     * @param view
     * @param w
     * @param h
     * @param ww
     */
    public void setViewHeight(Activity act, View view, int w, int h, int ww) {
        WindowManager wm = ((Activity) act).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        LayoutParams lp = view.getLayoutParams();
        lp.height = (width - ww) * h / w;
        view.setLayoutParams(lp);
    }

    /**
     * 不改变控件位置，修改控件大小
     *
     * @param v
     * @param W
     * @param H
     */
    public static void changeWH(View v, int W, int H) {
        LayoutParams params = (LayoutParams) v.getLayoutParams();
        params.width = W;
        params.height = H;
        v.setLayoutParams(params);
    }

    /**
     * 修改控件的宽
     *
     * @param view
     * @param width
     */
    public static void changeWidth(View view, int width) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.width = width;
        view.setLayoutParams(params);
    }

    /**
     * 修改控件的高
     *
     * @param view
     * @param height
     */
    public static void changeHeight(View view, int height) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

}
