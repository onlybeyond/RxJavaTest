package com.only.rxtest.common.event;

/**
 * Created with IntelliJ IDEA.
 * User: only beyond [FR]
 * Date: 2015/8/28
 * Email: wenzhi_bai@163.com
 */
public class UpdateProgressEvent {
    private static final int defaultId = -1;
    public int id;
    public int progress;
    public String sign;

    public UpdateProgressEvent(int progress) {
        this.id = defaultId;
        this.progress = progress;
    }

    public UpdateProgressEvent(int id, int progress, String sign) {
        this.id = id;
        this.progress = progress;
        this.sign = sign;
    }
}
