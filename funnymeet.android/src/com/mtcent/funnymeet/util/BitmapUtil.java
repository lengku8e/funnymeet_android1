package com.mtcent.funnymeet.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import com.mtcent.funnymeet.SOApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2015/8/9.
 */
public class BitmapUtil {
    public static Bitmap getThumbImage(String imageUrl, int maxWH) {
        Bitmap bitmap = null;

        if (imageUrl != null) {
            try {
                Bitmap _bitmap = null;

                if (StrUtil.isLocalFile(imageUrl)) {
                    String localName = StrUtil.getLocalFileName(imageUrl);
                    _bitmap = getBitmapFromFile(localName);
                } else {
                    File bitmapFile = getSaveBitmapFile(imageUrl);
                    _bitmap = getBitmapFromFile(bitmapFile);
                    if (_bitmap == null) {
                        _bitmap = downloadBitmap(imageUrl);
                        saveBitmap(_bitmap, bitmapFile);
                    }
                }

                bitmap = _bitmap;
                if (_bitmap != null && maxWH > 0) {
                    float w = _bitmap.getWidth();
                    float h = _bitmap.getHeight();

                    if (w > maxWH || h > maxWH) {
                        if (h > 0 && h < w) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(maxWH / w, maxWH / w); // 长和宽放大缩小的比例
                            bitmap = Bitmap.createBitmap(_bitmap, 0, 0,
                                    (int) w, (int) h, matrix, true);
                            _bitmap.recycle();
                        } else if (w > 0 && w < h) {
                            Matrix matrix = new Matrix();
                            matrix.postScale(maxWH / h, maxWH / h); // 长和宽放大缩小的比例
                            bitmap = Bitmap.createBitmap(_bitmap, 0, 0,
                                    (int) w, (int) h, matrix, true);
                            _bitmap.recycle();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static Bitmap downloadBitmap(String bitmapUrl) {
        Bitmap bitmap = null;

        try {
            // 构造URL
            URL url = new URL(bitmapUrl);
            // 打开连接
            URLConnection con = url.openConnection();
            // 获得文件的长度
            int contentLength = con.getContentLength();

            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File bitmapFile = BitmapUtil.getSaveBitmapFile(bitmapUrl);
            OutputStream os = new FileOutputStream(bitmapFile);

            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
            bitmap = BitmapUtil.getBitmapFromFile(bitmapFile);

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap getBitmapFromFile(String name) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(SOApplication.getAppContext()
                    .getAssets().open(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromFile(File bitmapFile) {
        Bitmap pngBM = null;
        if (!bitmapFile.exists()) {
            return null;
        }
        try {
            pngBM = BitmapFactory.decodeStream(new FileInputStream(bitmapFile));
            if (pngBM != null
                    && (pngBM.getWidth() <= 0 || pngBM.getHeight() <= 0)) {
                pngBM = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return pngBM;
    }


    public static void saveBitmap(Bitmap bitmap, File bitmapFile) {
        try {
            if (bitmap != null && bitmap.getWidth() > 0
                    && bitmap.getHeight() > 0) {
                FileOutputStream out = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getSaveBitmapFile(String url) {
        // 同一个URL，我们默认7天内不会更换。
        long today = System.currentTimeMillis();
        today /= (1000 * 60 * 60 * 24 * 7);
        String hash = StrUtil.md5(url);
        String path = Environment.getExternalStorageDirectory()
                + "/sohuodong/imagetemp/" + today;
        File file = new File(path);
        // 判断文件目录是否存在
        if (!file.exists()) {
            // 删除所有旧的文件夹
            deleteChildDir();
            file.mkdirs();
        }
        File saveFile = new File(path, String.valueOf(hash));

        return saveFile;
    }

    private static void deleteChildDir() {
        String path = Environment.getExternalStorageDirectory()
                + "/sohuodong/imagetemp";
        File file = new File(path);
        // 判断文件目录是否存在
        if (file.exists()) {
            RecursionDeleteFile(file);
        }
    }

    private static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
