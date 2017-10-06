package com.simon.utils.utils;

import android.content.Context;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by ${Simon} on 2016/8/5 0005.
 * picasso清理缓存工具
 */
public class PicassoCacheUtils {
    private static final String PICASSO_CACHE = "picasso-cache";

    /**
     * 获取图片缓存大小
     *
     * @param context
     * @return 图片缓存大小
     */
    public static String getDiskCacheSize(Context context) {
        int sizeSum = 0;
        File diskfile = createDefaultCacheDir(context);
        String[] diskfileList = diskfile.list();
        for (int i = 0; i < diskfileList.length; i++) {
            File filesize = new File(diskfile + "/" + diskfileList[i]);
            sizeSum = sizeSum + (int) filesize.length();
            System.out.println(sizeSum);
        }
        DecimalFormat df = new DecimalFormat(".00");
        double sizeSumKB = sizeSum / 1024;
        if (sizeSumKB < 1) {
            return String.valueOf(sizeSum) + "B";
        } else {
            double sizeSumMB = sizeSumKB / 1024;
            if (sizeSumMB < 1) {
                return String.valueOf((int) sizeSumKB) + "KB";
            } else {
                return String.valueOf(df.format(sizeSumMB)) + "MB";
            }
        }
    }

    /**
     * 清理图片缓存
     *
     * @param context
     */
    public static void cleanDiskCache(Context context) {
        String path = context.getApplicationContext().getCacheDir() +
                PICASSO_CACHE;
        delFolder(path);
    }

    /*创建默认文加件*/
    private static File createDefaultCacheDir(Context context) {
        File cache = new File(
                context.getApplicationContext().getCacheDir(),
                PICASSO_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    /*删除文件夹*/
    private static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*删除指定文件夹下所有文件*/
    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]); // 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]); // 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


}
