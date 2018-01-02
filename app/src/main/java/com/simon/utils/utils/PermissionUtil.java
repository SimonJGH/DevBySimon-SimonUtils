package com.simon.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Simon on 2017/6/25.
 */

public class PermissionUtil {

    //默认请求码
    public static final int REQUESTCODE = 110;

    /**
     * 检测权限-适用于单个功能模块
     * @param context
     * @param permission
     */
    public static void checkPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            judgePermission(context, permission);
        }
    }

    /**
     * 请求权限-适用于第一次app启动
     * @param context
     * @param permissions
     */
    public static void requestPermission(Context context, String[] permissions) {
        ActivityCompat.requestPermissions((Activity) context, permissions, REQUESTCODE);
    }

    /**
     * 判断是否已拒绝过权限
     *
     * @describe :如果应用之前请求过此权限但用户拒绝，此方法将返回 true;
     * -----------如果应用第一次请求权限或 用户在过去拒绝了权限请求，
     * -----------并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
     */
    private static void judgePermission(Context context, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            requestPermission(context, new String[]{permission});
        } else {
            toAppSetting(context);
        }
    }

    /**
     * 跳转到权限设置界面
     */
    private static void toAppSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }
}
