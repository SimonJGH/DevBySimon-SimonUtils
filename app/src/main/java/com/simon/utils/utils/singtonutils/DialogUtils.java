package com.simon.utils.utils.singtonutils;

import android.app.Activity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.R.color;

/**
 * 作者：${Simon} on 2016/11/22 0022 17:29
 * <p>
 * 描述：DialogUtils
 * 使用说明：
 * ①：context 需为 Activity
 */
@SuppressWarnings("all")
public class DialogUtils {

    private Window window;
    private AlertDialog dialog;

    private DialogUtils() {
    }

    public static DialogUtils getInstance() {
        return SafeMode.mDialog;
    }

    public static class SafeMode {
        private static final DialogUtils mDialog = new DialogUtils();
    }

    /**
     * 创建Dialog
     *
     * @param activity 上下文必须为Activity
     * @param inflate  dialog的展示布局
     * @param gravity  显示位置
     * @param scaleX   x轴缩放比例
     * @param scaleY   y轴缩放比例
     */
    private void createDialog(Activity activity, View inflate, int gravity, Double scaleX, Double scaleY) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        /*
         * 一般在广播中使用dialog.getWindow().setType(WindowManager.LayoutParams.
		 * TYPE_SYSTEM_ALERT);
		 */
        dialog.show();
        dialog.setContentView(inflate);
        // 获取窗口
        window = dialog.getWindow();
        // 设置对话框背景为透明
        window.setBackgroundDrawableResource(color.transparent);
        // 设置窗口位置
        window.setGravity(gravity);
        // 获取窗口属性
        WindowManager.LayoutParams lp = window.getAttributes();
        // 获取窗口管理者
        WindowManager windowManager = ((Activity) activity).getWindowManager();
        // 获取真机参数
        Display display = windowManager.getDefaultDisplay();
        // 设置窗口大小
        lp.width = (int) (display.getWidth() * scaleX);
        lp.height = (int) (display.getHeight() * scaleY);
        // 给窗口设置属性
        window.setAttributes(lp);
    }

    /**
     * 设置过场动画
     *
     * @param animStyle 具体样式点击setAnimations看详情
     */
    /*
     * <!-- 底部dialog --> <style name="dialogWindowAnim"
	 * parent="android:Animation"> <item
	 * name="android:windowEnterAnimation">@anim/dialog_enter</item> <item
	 * name="android:windowExitAnimation">@anim/dialog_exit</item> </style>
	 */
    private void setAnimations(int animStyle) {
        window.setWindowAnimations(animStyle);
    }

    /**
     * 退出dialog
     */
    private void exitDialog() {
        dialog.dismiss();
    }
}
