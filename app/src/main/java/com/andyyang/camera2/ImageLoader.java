package com.andyyang.camera2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;

import static android.R.attr.path;


/**
 * Created by AndyYang
 * date:2018/3/13.
 * mail:andyyang2014@126.com
 */

public class ImageLoader {

    private static ImageLoader loader;

    public static void loadImage(Context context, String url, ImageView view) {
        if (loader == null) {
            synchronized (ImageLoader.class) {
                if (loader == null) {
                    loader = new ImageLoader();
                }
            }
        }
        Glide.with(context)
                .load(url)
                .centerCrop()
                .crossFade()
                .into(view);
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) throws Exception {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "message");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();

        // 其次把文件插入到系统图库
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                file.getAbsolutePath(), fileName, null);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    public static void saveImageToGallery(Context context, File file) throws Exception {
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                file.getAbsolutePath(), file.getName(), null);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

}
