package com.only.rxtest.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by only on 16/6/20.
 * Email: onlybeyond99@gmail.com
 */
public class AndroidUtils {
    public static final DateFormat IMG_FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    public static final String PREFIX_IMAGE = "IMG_";
    public static final String EXTENSION_JPEG = ".jpg";
    public static final String FILENAME_NOMEDIA = ".nomedia";


    public static boolean deleteFile(String path) {
        if (path == null) {
            return false;
        } else {
            File delete = new File(path);
            if (delete.exists()) {
                return delete.delete();
            } else {
                return false;
            }
        }
    }

    /**
     * Saving cache files.
     * @param context
     * @return
     */
    public static File createCacheFile(Context context) {
        String timeStamp = IMG_FILE_NAME_FORMAT.format(new Date());
        String imageFileName = PREFIX_IMAGE + timeStamp + EXTENSION_JPEG;
        return new File(getCacheDir(context), imageFileName);
    }
    public static File getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File cacheDir = context.getExternalCacheDir();
            File noMedia = new File(cacheDir, FILENAME_NOMEDIA);
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cacheDir;
        } else {
            return context.getCacheDir();
        }
    }

}
