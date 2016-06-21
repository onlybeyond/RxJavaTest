package com.only.rxtest.common.event;

import android.net.Uri;


/**
 * Created with IntelliJ IDEA.
 * User: only beyond [FR]
 * Date: 2015/8/28
 * Email: wenzhi_bai@163.com
 */
public class UploadEvent {
    public UploadStatus status;
    public int id;
    public Uri uri;
    public String key;
    public String hash;
    public int index;
    public String sign;
    public String url;

    public UploadEvent(UploadStatus status, int id, Uri uri, String key, String hash, String url) {
        this.status = status;
        this.id = id;
        this.uri = uri;
        this.key = key;
        this.hash = hash;
                         this.url=url;
    }

    public UploadEvent(UploadStatus status, int id, Uri uri, String key, String hash, int index, String url) {
        this.status = status;
        this.id = id;
        this.uri = uri;
        this.key = key;
        this.hash = hash;
        this.index = index;
        this.url=url;
    }
    public UploadEvent(UploadStatus status, int id, Uri uri, String key, String hash, int index, String sign, String url) {
        this.status = status;
        this.id = id;
        this.uri = uri;
        this.key = key;
        this.hash = hash;
        this.index = index;
        this.sign=sign;
        this.url=url;
    }
}
