package com.simon.utils.utils;

import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 作者：${Simon} on 2016/10/29 0029 15:58
 * <p/>
 * 邮箱：2217403339@qq.com
 *
 * 使用说明：写入zip时需要配合StreamUtils使用！
 */
public class ZipUtils {

    private static String resourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Simon_Pad";

    /**
     * 读assets压缩文件
     *
     * @param context
     * @param zipName pad.zip
     */
    public static void readZipUtils(final Context context, final String zipName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = context.getResources().getAssets().open(zipName);
                    ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                    while (true) {
                        ZipEntry nextEntry = zipInputStream.getNextEntry();
                        if (nextEntry == null) {
                            break;
                        }
                        if (!nextEntry.isDirectory()) {
                            String name = nextEntry.getName();
                            String content = IOUtils.toString(zipInputStream);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    /**
     * 写zip文件
     *
     * @param fileName Simon.txt
     * @param zipName  Simon.zip
     */
    public static void writeZipUtils(String fileName, String zipName) {
        String zipPath = resourcePath + "/" + zipName;
        byte[] buf = new byte[1024];
        try {
            // 创建zip文件
            File zipFile = new File(zipPath);
            // 获取文件输入流
            File file = new File(resourcePath + "/" + fileName);
            FileInputStream fis = new FileInputStream(file);
            // 添加zip输出流
            FileOutputStream fos = new FileOutputStream(zipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.putNextEntry(new ZipEntry(file.getName()));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = fis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            fis.close();
            zos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
