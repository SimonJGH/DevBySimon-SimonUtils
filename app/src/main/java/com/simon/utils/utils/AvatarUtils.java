package com.simon.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * 上传头像工具 适配7.0
 * Created by Simon on 2017/10/13.
 */
@SuppressWarnings("all")
public class AvatarUtils {
    private Context mContext;
    // 拍照临时文件
    private File tempFile;
    // 拍照
    public int CAMERA_REQUEST_CODE = 1;
    // 相册
    public int GALLERY_REQUEST_CODE = 2;
    // 剪切
    public int CROP_REQUEST_CODE = 3;
    // 头像文件夹
    private String folderPath = Environment.getExternalStorageDirectory() + "/Ifevercheck/avatar/";
    // 头像绝对路径
    public String avatarPath;

    public AvatarUtils(Context context) {
        this.mContext = context;
        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 跳转到系统照相机
     */
    public void takeCamera() {
        avatarPath = folderPath + System.currentTimeMillis() + ".png";
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径,创建临时文件
            tempFile = new File(avatarPath);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            ((Activity) mContext).startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * 打开系统相册
     */
    public void openGallery() {
        avatarPath = folderPath + System.currentTimeMillis() + ".png";
        Intent galleryIntent;
        //当sdk版本低于19时使用此方法
        if (Build.VERSION.SDK_INT < 19) {
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
        } else {
            galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        }
        ((Activity) mContext).startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    /**
     * 拍照回掉
     */
    public void cameraResult() {
        crop(Uri.fromFile(tempFile));
    }

    /**
     * 相册回掉
     *
     * @param data
     */
    public void galleryResult(Intent data) {
        String imgPath = null;
        //当sdk版本低于19时使用此方法
        if (Build.VERSION.SDK_INT <= 19) {// 小米手机适配
            imgPath = data.getDataString();
            if (imgPath.contains("file://")) {
                imgPath = imgPath.replace("file://", "");
            }
            if (imgPath.contains("content")) {
                imgPath = getRealPathFromUri(mContext, Uri.parse(imgPath));
            }
        } else {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = mContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //picturePath就是图片在储存卡所在的位置
            imgPath = cursor.getString(columnIndex);
            cursor.close();
        }
        try {
            copyFileUsingFileChannels(new File(imgPath), new File(avatarPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        crop(Uri.fromFile(new File(avatarPath)));
    }

    /**
     * 得到本地图片旋转压缩
     *
     * @param path
     * @param size
     * @return
     */
    public Bitmap getLocalThumbImg(String path, int size) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts); // 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        // 设置缩放比例1表示不缩放
        newOpts.inSampleSize = 1;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        // 压缩好比例大小后再进行质量压缩
        bitmap = compressImage(bitmap, size, "jpeg");
        int degree = readPictureDegree(path);
        bitmap = rotaingImageView(degree, bitmap);
        return bitmap;
    }

    /**
     * 获取头像路径
     *
     * @return
     */
    public String getAvatarPath() {
        return avatarPath;
    }

    /**
     * 对拍摄照片进行裁剪
     */
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 这里必须设置为true拍照之后才会进行裁剪操作
        intent.putExtra("crop", "true");
        // 1.宽高和比例都不设置时,裁剪框可以自行调整(比例和大小都可以随意调整)
        // 2.只设置裁剪框宽高比(aspect)后,裁剪框比例固定不可调整,只能调整大小
        // 3.裁剪后生成图片宽高(output)的设置和裁剪框无关,只决定最终生成图片大小
        // 4.裁剪框宽高比例(aspect)可以和裁剪后生成图片比例(output)不同,此时, 会以裁剪框的宽为准,
        //  按照裁剪宽高比例生成一个图片,该图和框选部分可能不同,不同的情况可能是截取框选的一部分,
        //  也可能超出框选部分, 向下延伸补足
        // aspectX aspectY 是裁剪框宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪后生成图片的宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        // return-data为true时,会直接返回bitmap数据,但是大图裁剪时会出现问题,推荐下面为false时的方式
        // return-data为false时,不会返回bitmap,但需要指定一个MediaStore.EXTRA_OUTPUT保存图片uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ((Activity) mContext).startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * 复制文件
     *
     * @param oldFile
     * @param newFile
     * @throws IOException
     */
    private void copyFileUsingFileChannels(File oldFile, File newFile) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(oldFile).getChannel();
            outputChannel = new FileOutputStream(newFile).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    /**
     * 图片质量压缩
     *
     * @param image
     * @return
     * @size 图片大小（kb）
     */
    private Bitmap compressImage(Bitmap image, int size, String imageType) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (imageType.equalsIgnoreCase("png")) {
                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else {
                // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            int options = 100;
            // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            Log.i("Simon", "size = " + baos.toByteArray().length / 1024);
            while (baos.toByteArray().length / 1024 > size) {
                baos.reset(); // 重置baos即清空baos
                if (imageType.equalsIgnoreCase("png")) {
                    image.compress(Bitmap.CompressFormat.PNG, options, baos);
                } else {
                    // 这里压缩options%，把压缩后的数据存放到baos中
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
                options -= 10; // 每次都减少10
            }
            FileOutputStream out = new FileOutputStream(new File(avatarPath));
            image.compress(Bitmap.CompressFormat.JPEG, options, out);
            // 把压缩后的数据baos存放到ByteArrayInputStream中
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
            // 把ByteArrayInputStream数据生成图片
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * uri转path
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
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    private static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null)
            return null;
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
