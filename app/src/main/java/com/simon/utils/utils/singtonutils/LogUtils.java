package com.simon.utils.utils.singtonutils;

/**
 * @author Simon
 * @Description LogUtil的辅助类
 * @date createTime: 2016-4-23
 */
@SuppressWarnings("all")
public class LogUtils {

    private LogUtils() {
    }

    public static LogUtils getInstance() {
        return SafeMode.mLog;
    }

    public static class SafeMode {
        private static final LogUtils mLog = new LogUtils();
    }

    /**
     * whether show the debug logs,if you are debugging please change the state
     * IS_SHOW_LOG = true.
     */
    private boolean IS_SHOW_LOG = true;
    private String logITag = "Simon";

    private void d(String logTag, String logText) {
        if (IS_SHOW_LOG) {
            android.util.Log.d(logTag, logText);
        }
    }

    private void e(String logTag, String logText) {
        if (IS_SHOW_LOG) {
            android.util.Log.e(logTag, logText);
        }

    }

    private void w(String logTag, String logText) {
        if (IS_SHOW_LOG) {
            android.util.Log.w(logTag, logText);
        }

    }

    private void v(String logTag, String logText) {
        if (IS_SHOW_LOG) {
            android.util.Log.v(logTag, logText);
        }

    }

    private void i(String logText) {
        if (IS_SHOW_LOG) {
            android.util.Log.i(logITag, logText);
        }

    }

    /**
     * @param matrixName
     * @param a          必须是一个 4x4 matrix 矩阵
     * @return the debug string
     */
    private String floatMatrixToString(String matrixName, float[] a) {
        String s = "";
        s += "Matrix: " + matrixName + "\n";
        s += "\t " + a[0] + "," + a[1] + "," + a[2] + "," + a[3] + " \n";
        s += "\t " + a[4] + "," + a[5] + "," + a[6] + "," + a[7] + " \n";
        s += "\t " + a[8] + "," + a[9] + "," + a[10] + "," + a[11] + " \n";
        s += "\t " + a[12] + "," + a[13] + "," + a[14] + "," + a[15] + " \n";
        return s;
    }

}
