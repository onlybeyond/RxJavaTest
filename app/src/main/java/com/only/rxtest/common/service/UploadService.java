package com.only.rxtest.common.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Pair;

import com.only.rxtest.common.event.UpdateProgressEvent;
import com.only.rxtest.common.event.UploadEvent;
import com.only.rxtest.common.event.UploadStatus;
import com.only.rxtest.common.network.CloudManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

import static com.only.rxtest.utils.LogUtils.LOGD;
import static com.only.rxtest.utils.LogUtils.makeLogTag;


/**
 * Created with IntelliJ IDEA.
 * User: only beyond [FR]
 * Date: 2015/8/28
 * Email: wenzhi_bai@163.com
 */

/***
 * 上传有四个方法，两个用于图片，两个用于文件，
 * 用于图片的可以添加水印，单个的图片可以用id作为标识可以使用空间的id;
 * 多个文件可以可以使用sign或者id作为标识，不需要sign时可以为空，id 不需要时
 * 可以为－1；sign 可以使用类型加时间戳进行标识
 */

public class UploadService extends IntentService {
    private static final String TAG = makeLogTag(UploadService.class);
    private static final String EXTRA_URI = "uri";
    private static final String EXTRA_BATCH_URIS = "batch_uris";
    private static final String EXTRA_ID = "id";
    private static final String EXTRA_SHOOT_ID = "id";
    private static final String EXTRA_VIEW_POSITION = "view_position";
    private static final String EXTRA_QUALITY = "quality";
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_IMAGE= "image";
    private static final String EXTRA_FILE = "file";
    private static final String EXTRA_SIGN = "sign";
    // Indicate that it's a full screen watermark.
    private static final String EXTRA_WATERMARK_POSITION = "watermark_position";
    private static final int MAX_LENGTH = 1080;

    /***
     * one image
     * @param context
     * @param uri
     * @param id
     * @param quality
     */
    public static void startServiceForImage(Context context, Uri uri, int id, int quality) {
        CloudManager.init(context);
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_QUALITY, quality);
        intent.putExtra(EXTRA_TYPE, EXTRA_IMAGE);
        context.startService(intent);

    }


    /***
     * many image
     * @param context
     * @param uris
     * @param sign
     * @param position
     */
        public static void startServiceForImage(Context context, Uri[] uris, String sign, int position) {
        CloudManager.init(context);
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(EXTRA_BATCH_URIS, uris);
        intent.putExtra(EXTRA_SHOOT_ID,sign);
        intent.putExtra(EXTRA_VIEW_POSITION, position);
        context.startService(intent);
    }
    /***
     * one file
     * @param context
     * @param uri
     * @param id
     * @param sign
     */
    public static void startServiceForFile(Context context,Uri uri,int id,String sign){
        CloudManager.init(context);
        Intent intent=new Intent(context,UploadService.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_URI,uri);
        intent.putExtra(EXTRA_TYPE,EXTRA_FILE);
        intent.putExtra(EXTRA_SIGN,sign);
        context.startService(intent);
    }

    /***
     * many file
     * @param context
     * @param uris
     * @param id
     * @param sign
     */
    public static void startServiceForFile(Context context,Uri[] uris,int id,String sign){
        CloudManager.init(context);
        Intent intent=new Intent(context,UploadService.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_BATCH_URIS,uris);
        intent.putExtra(EXTRA_TYPE,EXTRA_FILE);
        intent.putExtra(EXTRA_SIGN,sign);
        context.startService(intent);
    }



    public UploadService() {
        super("UploadIntentService");
    }

    /**
     * Compress image and add watermark to the image should be done at the same method {@link IntentService#onHandleIntent}  When upload a single image.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        String type = intent.getStringExtra(EXTRA_TYPE);
        String sign=intent.getStringExtra(EXTRA_SIGN);
        int id = intent.getIntExtra(EXTRA_ID, -1);
        Parcelable[] parcelableArrayExtra = intent.getParcelableArrayExtra(EXTRA_BATCH_URIS);
        Uri[] batchUris = null;
        if (parcelableArrayExtra != null) {
            batchUris = new Uri[parcelableArrayExtra.length];
            int i = 0;
            for (Parcelable parcelable : parcelableArrayExtra) {
                batchUris[i] = (Uri) parcelable;
                i++;
            }
        }
        if(type.equals(EXTRA_IMAGE)){
            if(batchUris==null||batchUris.length==0){
                // one image
                Uri mediaUri = intent.getParcelableExtra(EXTRA_URI);
                LOGD(TAG,"---upload" + mediaUri.getPath());
                int quality = intent.getIntExtra(EXTRA_QUALITY, 100);
//                WatermarkPosition position = (WatermarkPosition) intent.getSerializableExtra(EXTRA_WATERMARK_POSITION);
//                LOGD(TAG, "Media path: " + (mediaUri == null ? "mediaUri is null" : mediaUri.getPath()));
//                if (position == WatermarkPosition.FULL_SCREEN) {
//                    ImageUtils.watermark(getApplicationContext(), mediaUri == null ? "" : mediaUri.getPath(), R.mipmap.watermarker, MAX_LENGTH, MAX_LENGTH, quality);
//                } else if (position == WatermarkPosition.CENTER) {
//                    ImageUtils.watermarkAtRightBottom(getApplicationContext(), mediaUri == null ? "" : mediaUri.getPath(), R.mipmap.watermarker, MAX_LENGTH, MAX_LENGTH, quality);
//                } else {
//                    ImageUtils.compress(mediaUri == null ? "" : mediaUri.getPath(), MAX_LENGTH, MAX_LENGTH, quality);
//                }
//                ImageUtils.compress(mediaUri == null ? "" : mediaUri.getPath(), MAX_LENGTH, MAX_LENGTH, quality);
                CloudManager.getInstance().uploadImage(mediaUri,
                        new UpCompletionCallback(this, mediaUri, id,sign, type),
                        new UploadOptions(null, null, false, new UploadProgressCallback(id,sign),
                                null));

            }else {
                // many image
                for (int i = 0; i <batchUris.length ; i++) {
                    //add waterMark

                    Uri mediaUri = intent.getParcelableExtra(EXTRA_URI);
                    int quality = intent.getIntExtra(EXTRA_QUALITY, 100);
//                    WatermarkPosition position = (WatermarkPosition) intent.getSerializableExtra(EXTRA_WATERMARK_POSITION);
//                    LOGD(TAG, "Media path: " + (mediaUri == null ? "mediaUri is null" : mediaUri.getPath()));
//                    if (position == WatermarkPosition.FULL_SCREEN) {
//                        ImageUtils.watermark(getApplicationContext(), mediaUri == null ? "" : mediaUri.getPath(), R.mipmap.watermarker, MAX_LENGTH, MAX_LENGTH, quality);
//                    } else if (position == WatermarkPosition.CENTER) {
//                        ImageUtils.watermarkAtRightBottom(getApplicationContext(), mediaUri == null ? "" : mediaUri.getPath(), R.mipmap.watermarker, MAX_LENGTH, MAX_LENGTH, quality);
//                    } else {
//                        ImageUtils.compress(mediaUri == null ? "" : mediaUri.getPath(), MAX_LENGTH, MAX_LENGTH, quality);
//                    }
//                    ImageUtils.compress(mediaUri == null ? "" : mediaUri.getPath(), MAX_LENGTH, MAX_LENGTH, quality);
                }
                CloudManager.getInstance().uploadImage(batchUris[0],
                        new UpCompletionCallback(this, batchUris, 1, batchUris.length, -1,sign, type),
                        new UploadOptions(null, null, false, new UploadProgressCallback(this, 1, batchUris.length,0,sign),
                                null));

            }

        }else if(type.equals(EXTRA_FILE)){

            if(batchUris==null||batchUris.length==0){
                Uri mediaUri = intent.getParcelableExtra(EXTRA_URI);
                LOGD(TAG,"---upload" + mediaUri.getPath());

                CloudManager.getInstance().uploadImage(mediaUri,
                        new UpCompletionCallback(this,mediaUri,id,sign,type),
                        new UploadOptions(null, null, false, new UploadProgressCallback(id,sign),
                                null));

            } else {
                CloudManager.getInstance().uploadImage(batchUris[0],
                        new UpCompletionCallback(this, batchUris,1, batchUris.length,id,sign,type),
                        new UploadOptions(null, null, false, new UploadProgressCallback(this, 1, batchUris.length, 0, sign),
                                null));

            }

        }



    }

    private static ContentValues convertCursor(Context context, Cursor data) {
        ContentValues contentValues = null;
        if (data != null && data.getCount() > 0) {
            if (data.moveToFirst()) {
            }
        }
        return contentValues;
    }

    private static void updateDisplayUrl(ContentValues contentValues, int index, int total, String displayUrl) {
        switch (index) {
            case 1: {
//                contentValues.put(PosterContract.Shoots.SHOOT_POSTER_URL, displayUrl);
                break;
            }

        }
    }

    private static void updateProgress(ContentValues contentValues, int progress) {
    }

    private static void updateLocalRecords(Context context, ContentValues contentValues) {

    }

    static class UpCompletionCallback implements UpCompletionHandler {
        private static final int defaultIndex = 1;
        private static final int defaultTotal = 1;
        private static final int defaultId = -1;
        private int id;
        private Uri[] uris;
        private int index;  // Indicate that the index image have been uploaded
        private int total;
        private Context context;
        private String type;
        private String name;
        private String sign;


        public UpCompletionCallback(Context context, Uri[] uris,int index,int total,int id,String sign,String type) {
            this.context = context;
            this.uris = uris;
            this.index = index;
            this.total = total;
            this.type=type;
            this.sign=sign;
            this.id=id;
        }
        public UpCompletionCallback(Context context,Uri uris,int id,String sign,String type){

            this.context=context;
            this.uris=new Uri[1];
            this.uris[0]=uris;
            this.index=defaultIndex;
            this.total=defaultTotal;
            this.id=id;
            this.sign=sign;
            this.type=type;
        }



        @Override
        public void complete(String s, ResponseInfo info, JSONObject response) {
            LOGD(TAG, "---Upload success: key = " + s + ", rep = " + response);
            String key = null;
            String hash = null;
            if (info.isOK()) {
                try {
                    key = response.getString("key");
                     hash = response.getString("hash");
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new UploadEvent(UploadStatus.FAILED, id, uris[index - 1], null, null, index-1,null));
                }
                Pair<String, String> pair = new Pair<>(key, hash);
                String url = CloudManager.generateRemoteUri(pair.first);
//                urlFinishList.add(url);
                if (index < total) {
//                    EventBus.getDefault().post(new UpdateProgressNodeEvent(UploadStatus.SUCCESS, (int) ((float)index / total) * 100, uris[index - 1], key, hash, index));
                    EventBus.getDefault().post(new UploadEvent(UploadStatus.SUCCESS, id, uris[index - 1], key, hash, index-1,sign,url));
                    ++index;
                    CloudManager.getInstance().uploadImage(uris[index - 1],
                            new UpCompletionCallback(context, uris, index, uris.length,id,sign,type),
                            new UploadOptions(null, null, false, new UploadProgressCallback(context, index, uris.length, (int) ((index - 1) / (float) uris.length * 100),sign),
                                    null));

                } else {
                    EventBus.getDefault().post(new UploadEvent(UploadStatus.SUCCESS, id, uris[index - 1], key, hash, index-1,sign,url));
                    // It's time to post a request.
                }
            } else {

                EventBus.getDefault().post(new UploadEvent(UploadStatus.FAILED, id, uris[index - 1], null, null, index, sign));
            }
        }
    }

    static class UploadProgressCallback implements UpProgressHandler {
        private static final int defaultIndex = 1;
        private static final int defaultTotal = 1;
        private static final int defaultId = -1;
        private int id;
        private int index;  // Indicate th,at the index image have been uploaded
        private int total;
        private int finishedProgress;
        private Context context;
        private String sign;

        public UploadProgressCallback(int id) {
            this.id = id;
            this.index = defaultIndex;
            this.total = defaultTotal;
        }
        public UploadProgressCallback(int id,String sign) {
            this.id = id;
            this.index = defaultIndex;
            this.total = defaultTotal;
            this.sign=sign;
        }


        public UploadProgressCallback(Context context, int index, int total, int finishedProgress,String sign) {
            this.context = context;
            this.id = defaultId;
            this.index = index;
            this.total = total;
            this.sign=sign;
            this.finishedProgress = finishedProgress;
        }

        @Override
        public void progress(String s, double v) {
            int progress = (int) (v * 100 / total) + finishedProgress;
            LOGD(TAG,"---sign:"+sign+"---p:"+progress);
            LOGD(TAG, String.format("Uploading %d/100", progress));
//            Toast.makeText(context,"progress"+progress,Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new UpdateProgressEvent(id, progress, sign));
        }
    }
}
