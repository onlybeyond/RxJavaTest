package com.only.rxtest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.first.activity.AsyncTaskTestActivity;
import com.only.rxtest.first.activity.RxJavaTestActivity;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;




public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String BASE_URL = "https://api.douban.com/v2/movie/";

    //view
    private TextView tvRx;
    private TextView tvTask;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        tvRx = (TextView) findViewById(R.id.tv_rx);
        tvTask = (TextView) findViewById(R.id.tv_async);

        tvRx.setOnClickListener(this);
        tvTask.setOnClickListener(this);

    }

    @Override
    public void fillDate() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_rx:
                //async test page
                openActivity(AsyncTaskTestActivity.class);
                break;
            case R.id.tv_async:
                //RxJava test page
                openActivity(RxJavaTestActivity.class);
                break;
            default:
                break;
        }

    }
}
