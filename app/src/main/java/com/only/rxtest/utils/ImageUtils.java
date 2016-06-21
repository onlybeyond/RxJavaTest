package com.only.rxtest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by only on 16/6/20.
 * Email: onlybeyond99@gmail.com
 */
public class ImageUtils {

    public static boolean copyBitmapFile(Context context, Uri photoUri, String outputPath) throws IOException {
        // Load image from path
        InputStream input = context.getContentResolver().openInputStream(photoUri);


        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        // API 21 以下 以防小内存手机 OOM
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        // compress it
        Bitmap bitmapOrigin = BitmapFactory.decodeStream(input,null,opt);
        if (bitmapOrigin == null) return false;
        // save to file
        FileOutputStream output = new FileOutputStream(outputPath);
        return bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 100, output);
    }
}
