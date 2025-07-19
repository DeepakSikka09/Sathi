package com.intsig.imageprocessdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * @Class:
 * @Description:
 * @author: lling(www.cnblogs.com / liuling)
 * @Date: 2015/10/20
 */
public class Utils {
    private static final int UNCONSTRAINED = -1;

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);


        if (file.exists()) {
            Log.d("createFile", "创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            Log.d("createFile", "创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在  
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录  
            Log.d("createFile", "目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                Log.d("createFile", "创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件  
        try {
            if (file.createNewFile()) {
                Log.d("createFile", "创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                Log.d("createFile", "创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("createFile", "创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound == 0) {
            float lowerF = (float) Math.sqrt(w * h / maxNumOfPixels);
            float upperF = (float) Math.min(w / minSideLength, h / minSideLength);
            if (lowerF >= 0.5f && upperF <= 0.5f) {
                return 2;
            }
        }
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static Bitmap loadBitmap(String path, int minSideLength, int maxNumOfPixels, Bitmap.Config config, boolean rotate, int[] mCurrentThumbBounds) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap b = null;
        try {
            System.gc();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                return null;
            } else {
                Log.d("loadBitmap", "loadBitmap(orginal) path:" + path + " " + options.outWidth + "x" + options.outHeight);
            }
            int samplesize = computeSampleSize(options, minSideLength, maxNumOfPixels);

            options.inSampleSize = samplesize;


            for (int i = 0; i < mCurrentThumbBounds.length; i++) {
                mCurrentThumbBounds[i] = (int) (mCurrentThumbBounds[i] * 1 / samplesize);
            }

            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = config;
            b = BitmapFactory.decodeFile(path, options);
            if (b == null)
                return null;

            if (rotate) {
                int orientation = getOrientation(path);
                if (orientation != 1) {
                    Matrix m = new Matrix();
                    m.postRotate(getRotation(orientation));
                    Bitmap newBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                    if (newBitmap != null && !newBitmap.equals(b)) {//前后两张图片不一样，则回收前面一张图，并把新图片资源指向前面图的变量；
                        b.recycle();
                        b = newBitmap;
                    }
                }
            }
        } catch (OutOfMemoryError ex) {
            Log.d("loadBitmap", "loadBitmap OOM:", ex);
            System.gc();
        } catch (Exception e) {
            Log.d("loadBitmap", "image read error:" + e);
        }
        return b;
    }

    public static int getRotation(int orientation) {
        switch (orientation) {
            case 1:
                return 0;
            case 8:
                return 270;
            case 3:
                return 180;
            case 6:
                return 90;
            default:
                return 0;
        }
    }

    public static int getOrientation(String file) {
        int orientation = 1;
        if (!TextUtils.isEmpty(file)) {
            try {
                ExifInterface exif = new ExifInterface(file);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (NoClassDefFoundError e) {
                Log.d("getOrientation", "image read error:" + e);
            } catch (IOException e) {
                Log.d("getOrientation", "image read error:" + e);
            } catch (ExceptionInInitializerError e) {
                Log.d("getOrientation", "image read error:" + e);
            } catch (NullPointerException e) {
                Log.d("getOrientation", "image read error:" + e);
            } catch (RuntimeException e) {
                Log.d("getOrientation", "image read error:" + e);
            } catch (StackOverflowError e) {
                Log.d("getOrientation", "image read error:" + e);
            }
        }
        return orientation;
    }
}
