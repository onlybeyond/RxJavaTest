package com.only.rxtest.first.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.only.rxtest.R;
import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.common.network.ApiConfig;
import com.only.rxtest.common.adapter.JokeAdapter;
import com.only.rxtest.common.model.JokeBean;
import com.only.rxtest.utils.LogUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import static com.only.rxtest.utils.LogUtils.*;

/**
 * Created by only on 16/6/15.
 * 这部的目的不是为了对比两者的优缺点,而是为了让大家通过异步任务对RxJava有一定的了解
 */
public class RxJavaTestActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = makeLogTag(RxJavaTestActivity.class);

    //data
    private LinkedList<JokeBean> jokeBeanList;
    private JokeAdapter jokeAdapter;


    //view
    private RecyclerView rvRx;
    private TextView tvTitle;


    @Override
    public void initView() {
        setContentView(R.layout.activity_rx_test);
        //top
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.arrow_back);
        tvTitle = (TextView) findViewById(R.id.toolbar_title);
        rvRx = (RecyclerView) findViewById(R.id.rv_rx);
        rvRx.setLayoutManager(new LinearLayoutManager(this));
        jokeBeanList = new LinkedList<>();
        jokeAdapter = new JokeAdapter(this, jokeBeanList);
        rvRx.setAdapter(jokeAdapter);

        toolbar.setOnClickListener(this);


    }

    @Override
    public void fillDate() {
        tvTitle.setText("RxJava测试");

    }

    @Override
    public void requestData() {
        super.requestData();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//                okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
                String url = ApiConfig.BASE_URL_JOKE + "list.from" + "?sort=asc&page=2&pagesize=20&time=1418816972&key=" + ApiConfig.JOKE_KEY;
                LOGD(TAG, "---url" + url);
                Request request = null;
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response != null && response.code() == 200) {
                        subscriber.onNext(response.body().string());
                        subscriber.onCompleted();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String str) {
                        parseJson(str);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(TAG + "Error!");
                    }
                });
    }

    public void parseJson(String json) {
        LogUtils.LOGD(TAG, "---json" + json);
        String ret = "";
        if (!TextUtils.isEmpty(json)) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(json).getAsJsonObject();
            int errorCode = jsonObject.get(ApiConfig.ERROR_CODE).getAsInt();
            if (errorCode == ApiConfig.ERROR_CODE_NUM) {
                JsonObject result = jsonObject.get("result").getAsJsonObject();
                JsonArray data = result.get("data").getAsJsonArray();
                JokeBean[] jokeBeen = new Gson().fromJson(data, JokeBean[].class);
                if (jokeBeen != null) {
                    jokeBeanList.addAll(Arrays.asList(jokeBeen));
                    jokeAdapter.notifyDataSetChanged();
                }

            } else {
                String errorReason = jsonObject.get("ERROR_REASON").getAsString();
                if (!TextUtils.isEmpty(errorReason)) {
                    showToast(errorReason);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.toolbar:
                finish();
                break;
            default:
                break;
        }

    }
}
