package com.simon.utils.utils.singtonutils;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
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
     * 创建简单版的Dialog
     *
     * @param activity
     * @param msg
     */
    public void createDialogSimple(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(msg).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 创建Dialog
     *
     * @param activity 上下文必须为Activity
     * @param inflate  dialog的展示布局
     * @param gravity  显示位置
     * @param scaleX   x轴缩放比例  x轴缩放比例  0-默认wrapcontent 1-matchparent
     * @param scaleY   y轴缩放比例   y轴缩放比例  0-默认wrapcontent 1-matchparent
     */
    private void createDialogCustom(Activity activity, View inflate, int gravity, Double scaleX, Double scaleY) {
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
        if (scaleX == 0) {
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            lp.width = (int) (display.getWidth() * scaleX);
        }
        if (scaleY == 0) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            lp.height = (int) (display.getHeight() * scaleY);
        }
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
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
