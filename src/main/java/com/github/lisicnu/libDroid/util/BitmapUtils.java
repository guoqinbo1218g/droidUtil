package com.github.lisicnu.libDroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.github.lisicnu.libDroid.io.FlushedInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public final class BitmapUtils {

    private final static String TAG = BitmapUtils.class.getSimpleName();

    /**
     * 加载指定路径下的图像文件
     *
     * @param path
     * @return
     */
    public static Bitmap load(String path) {
        try {
            File fi = new File(path);
            if (fi.isDirectory() || !fi.exists()) {
                return null;
            }

            return load(new FileInputStream(path), -1, -1);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    /**
     * 加载指定路径下的图像文件,返回指定大小
     *
     * @param path
     * @return
     */
    public static Bitmap load(String path, int width, int height) {
        try {
            File fi = new File(path);
            if (fi.isDirectory() || !fi.exists()) {
                return null;
            }

            return load(new BufferedInputStream(new FileInputStream(path)), width, height);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    /**
     * <strong>if parameter reqWidth or reqHeight less or equal to zero. means load origin bitmap.
     * </strong>
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        if (options == null) {
            throw new InvalidParameterException("parameters invalid.");
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if ((height > reqHeight || width > reqWidth) && reqWidth > 0 && reqHeight > 0) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

//  GOOGLE DOC USE THIS CODE.
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//            // height and width larger than the requested height and width.
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
        }
        return inSampleSize;
    }

    // BITMAPFACTORY.decodeStream sometimes has problem. when skip method of the
    // InputStream class does not skip the given number of bytes then, the
    // BitmapFactory fails to decode the InputStream.

    public synchronized static Bitmap load(InputStream is, int width, int height) {
        BitmapFactory.Options opt = null;
        try {
            opt = new BitmapFactory.Options();
            if (width > 0 && height > 0) {
                if (is.markSupported()) {
                    is.mark(is.available());

                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is, null, opt);
                    opt.inSampleSize = calculateInSampleSize(opt, width, height);
                    is.reset();
                }
            }

            opt.inJustDecodeBounds = false;
            opt.inPurgeable = true;
            opt.inInputShareable = true;

            return BitmapFactory.decodeStream(new FlushedInputStream(is), null, opt);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.print(e.toString());
                }
            }
            opt = null;
        }
    }

    /**
     * 加载图片, 如果图片资源不存在, 将返回null
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap load(Context context, int resId) {
        // 此方法加载也可以, 但是耗时长
        // return BitmapFactory.decodeResource(context.getResources(), resId);
        try {
            return load(context.getResources().openRawResource(resId), -1, -1);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }

    }

    /**
     * 加载图片, 返回指定大小 如果图片资源不存在, 将返回null
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap load(Context context, int resId, int width, int height) {
        try {
            return load(context.getResources().openRawResource(resId), width, height);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    /**
     * zoom bitmap, if parameters are invalid will return null. <br/>
     * <br/>
     * Note: <br/>
     * this method do not recycle any resource.
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null || width <= 0 || height <= 0)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * write bitmap to file.
     *
     * @param bmp
     * @param fileName
     * @return whether the file has been created success.
     */
    public static boolean writeToFile(Bitmap bmp, String fileName) {
        if (bmp == null || StringUtils.isNullOrEmpty(fileName))
            return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            bmp.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}