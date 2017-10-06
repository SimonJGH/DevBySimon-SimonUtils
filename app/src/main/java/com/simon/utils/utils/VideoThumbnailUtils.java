package com.simon.utils.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.provider.MediaStore.Images;

/**
 * 作者：${Simon} on 2016/11/23 0023 18:29
 * <p>
 * 描述：使用文档：获取视频缩略图属于耗时操作，因此要新建一个子线程，而图片的展示是在主线程进行的，所以在得到缩略图后要回归到主线程1、handler2、runOnUiThread
 */
   /* new Thread() {
        public void run() {
            bitmap = VideoThumbnailUtils.createNetVideoThumbnail(
                    "http://www.ydsimon.net.cn/mp4/tianyou.mp4", 200, 200);
            if (bitmap != null) {
                // mHandler.sendEmptyMessage(MSG_UPDATE_VIEW);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        iv_show.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }.start();*/
@SuppressWarnings("all")
public class VideoThumbnailUtils {

    /**
     * 获取网络视频缩略图
     *
     * @param url    视频地址
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @return bitmap
     */
    public static Bitmap createNetVideoThumbnail(String url, int width,
                                                 int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /**
     * 获取本地视频缩略图
     *
     * @param filePath 本地视频路径
     * @return bitmap
     */
    public static Bitmap createLocalVideoThumbnail(String filePath) {
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame")
                        .invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture")
                        .invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmap != null)
                        return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(
                        instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
