package com.simon.utils.utils;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Simon
 * @Description 网络连接管理类
 * @date createTime: 2016-4-23
 *
 * 使用说明：离开当前activity或者fragment时要记得调用destroy销毁connManager
 */
@SuppressWarnings("all")
public class NetworkUtils {

    private static ConnectivityManager connManager = null;

    /**
     * 判断网络是否连接（基站或wifi）
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (isNetworkAvailable(context)) {
            int type = getConnectionType(context);
            if (type == 0 || type == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断网络是否可用（所有网络类型）
     *
     * @param context
     * @return
     */
    private static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * WIFI网络是否链接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo != null) {
                return wifiInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 基站网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileInfo != null) {
                return mobileInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 读取当前网络连接类型
     *
     * @param context
     * @return one of TYPE_MOBILE, TYPE_WIFI, TYPE_WIMAX, TYPE_ETHERNET,
     * TYPE_BLUETOOTH, or other types defined by ConnectivityManager
     * int值分别为：0、1、6、9、7
     */
    public static int getConnectionType(Context context) {
        if (context != null) {
            connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 网络断开连接提示
     *
     * @param context
     */
    public static void alertSetNetwork(final Context context) {
        Builder builder = new Builder(context);
        builder.setTitle("提示：网络异常").setMessage("是否对网络进行设置?");

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                try {
                    int sdkVersion = android.os.Build.VERSION.SDK_INT;
                    if (sdkVersion > 10) {
                        intent = new Intent(
                                android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName comp = new ComponentName(
                                "com.android.settings",
                                "com.android.settings.WirelessSettings");
                        intent.setComponent(comp);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * 销毁connManager
     */
    public static void DestroyConnManager() {
        if (connManager != null) {
            connManager = null;
        }
    }
}
