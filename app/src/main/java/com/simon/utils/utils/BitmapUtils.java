package com.simon.utils.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

/**
 * @author Simon
 * @Description Bitmap相关的辅助类
 * @date createTime: 2016-3-28
 * <p>
 * 使用说明：注意储存的读取权限！！！
 */
@SuppressWarnings("all")
public class BitmapUtils {

    /**
     * uri转path
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 保存Bitmap到SD卡
     *
     * @param bitmap
     * @param outPath
     * @param maxSize
     */
    public static void compressBitmapToSDCard(Bitmap bitmap, String outPath, int maxSize) {
        Log.i("Simon", "Bitmap质量压缩开始");
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            // scale
            int options = 100;
            // Store the bitmap into output stream(no compress)
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
            // Compress by loop
            while (os.toByteArray().length / 1024 > maxSize) {
                // Clean up os
                os.reset();
                // interval 10
                options -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
            }
            // Generate compressed image file
            FileOutputStream fos = null;
            fos = new FileOutputStream(outPath);
            fos.write(os.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Simon", "Bitmap质量压缩Exception = " + e.getMessage());
        }
        Log.i("Simon", "Bitmap质量压缩结束");
    }

    /**
     * 从SD卡获取Bitmap
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getBitmapFromSDCard(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    /**
     * 保存网络图片到SD
     *
     * @param PicUrl
     * @param SDUrl
     */
    public void saveNetImageToSDCard(String PicUrl, String SDUrl) {
        Bitmap bitmap = null;
        File file = new File(SDUrl);
        FileOutputStream out;
        try {
            URL pictureUrl = new URL(PicUrl);
            InputStream in = pictureUrl.openStream();
            bitmap = BitmapFactory.decodeStream(in);
            out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            in.close();
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressBitmap(bitmap);//再进行质量压缩
    }

    /**
     * 通过uri获取文件
     *
     * @param uri
     */
    public static File getFileFromUri(Context ac, Uri uri) {
        if (uri.getScheme().toString().compareTo("content") == 0) {
            ContentResolver cr = ac.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);// 根据Uri从数据库中找
            if (cursor != null) {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路径
                cursor.close();
                if (filePath != null) {
                    return new File(filePath);
                }
            }
        } else if (uri.getScheme().toString().compareTo("file") == 0) {
            return new File(uri.toString().replace("file://", ""));
        }
        return null;
    }

    /**
     * 本地图片质量压缩
     *
     * @param inPath
     * @param outPath
     */
    public static void compressLocalImage(final String inPath, final String outPath) {
        Log.i("Simon", "本地图片质量压缩开始");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileInputStream fis = new FileInputStream(inPath);
                    int size = fis.available() / 1024;
                    int scale = 0;
                    if (size < 100 && size > 50) {// 图片小于100k 压缩效果为50k以内
                        scale = 50;
                    } else if (size < 500) {// 图片小于500k 压缩效果为100k以内
                        scale = 100;
                    } else if (size < 2500) {// 图片小于2500k 压缩效果为120k以内
                        scale = 120;
                    } else if (size < 5000) {// 图片小于5000k 压缩效果为150k以内
                        scale = 150;
                    } else {// 图片大于5000k 压缩效果为200k以内
                        scale = 200;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(inPath);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    // scale
                    int options = 100;
                    // Store the bitmap into output stream(no compress)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                    // Compress by loop
                    while (os.toByteArray().length / 1024 > scale) {
                        // Clean up os
                        os.reset();
                        // interval 10
                        options -= 10;
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
                    }
                    // Generate compressed image file
                    FileOutputStream fos = new FileOutputStream(outPath);
                    fos.write(os.toByteArray());
                    fos.flush();
                    fis.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Simon", "本地图片质量压缩Exception = " + e.getMessage());
                }
            }
        }).start();
        Log.i("Simon", "本地图片质量压缩结束");
    }

    /**
     * Bitmap质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bm) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            bm.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 按指定大小压缩图片
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

    /**
     * Bitmap转成byte[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();// 转为byte数组
    }

    /**
     * byte[]转Bitmap
     *
     * @param bitmapArray
     * @return
     */
    public static Bitmap convertByteToBitmap(byte[] bitmapArray) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param
     */
    public static Bitmap convertStringToBitmap(String string) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

}
