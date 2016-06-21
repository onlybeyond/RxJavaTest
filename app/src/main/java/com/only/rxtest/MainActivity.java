package com.only.rxtest;

import android.view.View;
import android.widget.TextView;

import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.first.activity.AsyncTaskTestActivity;
import com.only.rxtest.first.activity.RxJavaTestActivity;

import com.only.rxtest.second.activity.RxJavaSecondTestActivity;
import com.only.rxtest.three.OkHttpTestActivity;
import com.only.rxtest.three.RetrofitTestActivity;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String BASE_URL = "https://api.douban.com/v2/movie/";

    //view
    private TextView tvRx;
    private TextView tvTask;
    private TextView tvRetrofit;
    private TextView tvOkHttp;
    private TextView tvRxSecond;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        tvRx = (TextView) findViewById(R.id.tv_rx);
        tvTask = (TextView) findViewById(R.id.tv_async);
        tvRetrofit = (TextView)findViewById(R.id.tv_retrofit);
        tvOkHttp = (TextView)findViewById(R.id.tv_http);
        tvRxSecond = (TextView)findViewById(R.id.tv_rx_second);

        tvRx.setOnClickListener(this);
        tvTask.setOnClickListener(this);
        tvRetrofit.setOnClickListener(this);
        tvOkHttp.setOnClickListener(this);
        tvRxSecond.setOnClickListener(this);

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
            case R.id.tv_retrofit:
                openActivity(RetrofitTestActivity.class);
                break;
            case R.id.tv_http:
                openActivity(OkHttpTestActivity.class);
                break;
            case R.id.tv_rx_second:
                openActivity(RxJavaSecondTestActivity.class);
            default:
                break;
        }

    }
}
