package com.only.rxtest.second.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.only.rxtest.R;
import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.common.adapter.ImageAdapter;
import com.only.rxtest.common.event.UploadEvent;
import com.only.rxtest.common.event.UploadStatus;
import com.only.rxtest.common.network.CloudManager;
import com.only.rxtest.common.service.UploadService;
import com.only.rxtest.first.activity.RxJavaTestActivity;
import com.only.rxtest.utils.AndroidUtils;
import com.only.rxtest.utils.ImageUtils;
import com.only.rxtest.utils.LogUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import me.iwf.photopicker.utils.PhotoPickerIntent;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.only.rxtest.utils.LogUtils.*;


/**
 * Created by only on 16/6/16.
 * Email: onlybeyond99@gmail.com
 * 这部分是为了比较RxJava和IntentService的区别,代码写的很操,没有封装,也没有优化代码,想到是更直观(其实就是懒)ui也没有太好的展示.
 不过等笔者把网站搭好,就会給大家好看的图片
 * 直接用手机拍照使用的是RxJava选取照片使用的IntentService
 */
public class RxJavaSecondTestActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG=makeLogTag(RxJavaTestActivity.class);
    private static final int REQ_TAKE_PHOTO = 1;
    private static final int REQ_PICK_PHOTO = 2;
    private String upFileId;

    //data
    private String filePath;

    //view
    private TextView tvTitle;
    private TextView tvUpload;
    private ImageAdapter imageAdapter;
    private ImageView ivTest;
    private boolean isUpLoader;


    @Override
    public void initView()
    {
        setContentView(R.layout.activity_rx_second);
        setRegisterEvent(true);
        tvTitle = (TextView)findViewById(R.id.toolbar_title);
        tvUpload = (TextView)findViewById(R.id.toolbar_menu);
        RecyclerView rvImage=(RecyclerView)findViewById(R.id.rv_rx_second);
        ivTest = (ImageView)findViewById(R.id.iv_test);
        rvImage.setLayoutManager(new GridLayoutManager(this,2));
        imageAdapter = new ImageAdapter();
        rvImage.setAdapter(imageAdapter);
        tvUpload.setOnClickListener(this);
    }

    @Override
    public void fillDate() {
       tvTitle.setText("RxJava上传图片");
       tvUpload.setText("上传图片");
        tvUpload.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    Glide.with(this).load(filePath).into(ivTest);
                    upFileId=String.valueOf( System.currentTimeMillis());
                    //RxJava上传
                    CloudManager.init(this);
                    Observable.create(new Observable.OnSubscribe<String>() {
                                          @Override
                                          public void call(Subscriber<? super String> subscriber) {
                                              CloudManager.getInstance().uploadImage( Uri.parse(filePath),
                                                      new RxUploadComplete(subscriber),
                                                      new UploadOptions(null, null, false, new RxUploadProgress(),
                                                              null));
                                          }
                                      }).observeOn(Schedulers.io())
                            .subscribeOn(AndroidSchedulers.mainThread())
                     .subscribe(new Observer<String>() {
                         @Override
                         public void onCompleted() {

                         }

                         @Override
                         public void onError(Throwable e) {

                         }

                         @Override
                         public void onNext(String s) {
                           LOGD(TAG,"---next"+s);
                             showToast("上传成功");
                         }
                     });


                }
                break;
            }
            case REQ_PICK_PHOTO: {
                if (resultCode == RESULT_OK) {
                    Uri pickedMediaUri = data.getData();
                    try {
                        //照片复制
                        ImageUtils.copyBitmapFile(this, pickedMediaUri, filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(this).load(filePath).into(ivTest);
                    upFileId=String.valueOf( System.currentTimeMillis());
//                    IntentService 上传
                    UploadService.startServiceForFile(this,  Uri.parse(filePath), -1,upFileId);
                }
                    break;

            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * EventBus 接受上传状态
     * @param event
     */
    public void onEventMainThread(UploadEvent event) {
        if (event.status == UploadStatus.SUCCESS) {
            isUpLoader =true;
            showToast("上传成功");
        } else {
            isUpLoader =false;
            showToast("上传失败");

        }
        boolean result = AndroidUtils.deleteFile(event.uri.getPath());
        LogUtils.LOGD(TAG, "Delete file: " + event.uri.getPath() + (result ? " success!" : " failed!"));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.toolbar_menu:
                openDialog();
                break;
            default:
                break;
        }
    }

    public void openDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.upload_image)
                .setItems(R.array.media_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                takePhoto();
                                break;
                            }
                            case 1: {
                                pickPhoto();
                                break;
                            }
                            case 2: {
                                break;
                            }
                        }
                    }
                });
        builder.show();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = AndroidUtils.createCacheFile(this);
//            array.put(viewId, Uri.fromFile(photoFile));
            filePath=photoFile.getAbsolutePath();
            LOGD(TAG,"---file path"+filePath);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQ_TAKE_PHOTO);
        }
    }

    /**
     * 选取图片
     */
    private void pickPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ComponentName handlers = photoPickerIntent.resolveActivity(getPackageManager());
        if (handlers != null) {
            File photoFile = AndroidUtils.createCacheFile(this);
            filePath=photoFile.getAbsolutePath();
            LOGD(TAG,"---file path"+filePath);
            startActivityForResult(photoPickerIntent, REQ_PICK_PHOTO);
        }
    }

    private class RxUploadComplete implements UpCompletionHandler{

        Subscriber<? super String> subscriber;

        public RxUploadComplete(Subscriber<? super String> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void complete(String s, ResponseInfo info, JSONObject response) {
            LOGD(TAG, "---Upload success: key = " + s + ", rep = " + response);
            String key = null;
            String hash = null;
            try {
                key = response.getString("key");
                hash = response.getString("hash");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Pair<String, String> pair = new Pair<>(key, hash);
            String url = CloudManager.generateRemoteUri(pair.first);
            subscriber.onNext(url);
        }
    }

     class RxUploadProgress implements UpProgressHandler {
         public RxUploadProgress() {
         }

         @Override
        public void progress(String key, double percent) {

            LOGD(TAG,"---sign:"+"---p:"+percent);
        }
    }
    }
