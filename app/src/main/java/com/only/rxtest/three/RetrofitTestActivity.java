package com.only.rxtest.three;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.only.rxtest.R;
import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.common.adapter.JokeAdapter;
import com.only.rxtest.common.model.JokeBean;
import com.only.rxtest.common.model.Result;
import com.only.rxtest.common.model.ServerResponse;
import com.only.rxtest.common.network.ApiConfig;

import java.util.LinkedList;
import java.util.List;

import com.only.rxtest.three.util.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.only.rxtest.utils.LogUtils.*;

/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class RetrofitTestActivity extends BaseActivity {

    private static String TAG=makeLogTag(RetrofitTestActivity.class);

    //data
    private JokeAdapter adapter;
    private LinkedList<JokeBean> jokeBeanList;

    //view
    private TextView tvTitle;
    private RecyclerView rvRetrofit;


    @Override
    public void initView() {
        setContentView(R.layout.activity_retrofit_test);
        tvTitle = (TextView)findViewById(R.id.toolbar_title);
        rvRetrofit = (RecyclerView)findViewById(R.id.rv_retrofit);
        jokeBeanList = new LinkedList<>();
        adapter = new JokeAdapter(this, jokeBeanList);
        rvRetrofit.setLayoutManager(new LinearLayoutManager(this));
        rvRetrofit.setAdapter(adapter);



    }

    @Override
    public void fillDate() {
        tvTitle.setText("Retrofit测试");


    }

    @Override
    public void requestData() {
        super.requestData();
        long l = System.currentTimeMillis();

        LOGD(TAG,"---time before"+l);
        String time = String.valueOf(l);
       time= time.substring(0,time.length()-3);
        LOGD(TAG,"---time after"+time);

        Call<ServerResponse<Result<List<JokeBean>>>> call = RetrofitUtil.getService()
                .getJoke("desc", 1, 20, time, ApiConfig.JOKE_KEY);
        call.enqueue(new Callback<ServerResponse<Result<List<JokeBean>>>>() {
            @Override
            public void onResponse( Call<ServerResponse<Result<List<JokeBean>>>> call,final Response<ServerResponse<Result<List<JokeBean>>>> response) {

               LOGD(TAG,"---thread out"+ Thread.currentThread().getName());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LOGD(TAG,"---thread in"+ Thread.currentThread().getName());
                        ServerResponse<Result<List<JokeBean>>> body = response.body();
                        Result<List<JokeBean>> result = body.getResult();
                        List<JokeBean> data = result.getData();
                        jokeBeanList.addAll(data);
                        adapter.notifyDataSetChanged();

                    }
                });
            }

            @Override
            public void onFailure(Call<ServerResponse<Result<List<JokeBean>>>> call, Throwable t) {

            }
        });


    }
}
