package com.simon.utils.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * @author Simon
 * @Description SDCardUtils
 * @date createTime: 2016-11-23
 */
public class SDCardUtils {
    /**
     * 判断SDCard是否挂载
     *
     * @return
     */
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SDCard的根目录路径
     *
     * @return
     */
    public static String getSDCardBasePath() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 获取SDCard的完整空间大小
     *
     * @return
     */
    public static long getSDCardTotalSize() {
        long size = 0;
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBasePath());
            if (Build.VERSION.SDK_INT >= 18) {
                size = statFs.getTotalBytes();
            } else {
                size = statFs.getBlockCount() * statFs.getBlockSize();
            }
            return size / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 获取SDCard的可用空间大小
     *
     * @return
     */
    public static long getSDCardAvailableSize() {
        long size = 0;
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBasePath());
            if (Build.VERSION.SDK_INT >= 18) {
                size = statFs.getAvailableBytes();
            } else {
                size = statFs.getAvailableBlocks() * statFs.getBlockSize();
            }
            return size / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 获取SDCard的剩余空间大小
     *
     * @return
     */
    public static long getSDCardFreeSize() {
        long size = 0;
        if (isSDCardMounted()) {
            StatFs statFs = new StatFs(getSDCardBasePath());
            if (Build.VERSION.SDK_INT >= 18) {
                size = statFs.getFreeBytes();
            } else {
                size = statFs.getFreeBlocks() * statFs.getBlockSize();
            }
            return size / 1024 / 1024;
        } else {
            return 0;
        }
    }

    /**
     * 从sdcard中删除文件
     *
     * @return
     */
    public static boolean removeFileFromSDCard(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                file.delete();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
