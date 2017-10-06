package com.simon.utils.utils.singtonutils;

import android.widget.Toast;

import com.simon.utils.MyApplication;

/**
 * Created by Simon on 2017/10/2.
 * <p>
 * 使用说明：需要获得MyApplication的context
 */

public class ToastUtils {

    private ToastUtils() {
    }

    public static ToastUtils getInstance() {
        return SafeMode.mToast;
    }

    /**
     * static final 保证了实例的唯一和不可改变
     */
    private static class SafeMode {
        private static final ToastUtils mToast = new ToastUtils();
    }

    /**
     * 吐司--短
     *
     * @param msg
     */
    public void showShortToast(String msg) {
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 吐司--长
     *
     * @param msg
     */
    public void showLongToast(String msg) {
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
    }
}
