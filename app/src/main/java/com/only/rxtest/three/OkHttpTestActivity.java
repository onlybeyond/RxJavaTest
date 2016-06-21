package com.only.rxtest.three;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.only.rxtest.R;
import com.only.rxtest.base.BaseActivity;
import com.only.rxtest.common.adapter.JokeAdapter;
import com.only.rxtest.common.model.JokeBean;
import com.only.rxtest.common.network.ApiConfig;
import com.only.rxtest.utils.LogUtils;
//import com.squareup.okhttp.Callback;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.only.rxtest.utils.LogUtils.*;

/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class OkHttpTestActivity extends BaseActivity{

    private static String TAG=makeLogTag(OkHttpTestActivity.class);

    //data
    private LinkedList<JokeBean> jokeBeanList;
    private JokeAdapter jokeAdapter;
    private TextView tvTitle;

    public interface TaskHelper {
        void handleMessage(String s);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_http_test);
        tvTitle = (TextView)findViewById(R.id.toolbar_title);
        RecyclerView rvHttp=(RecyclerView)findViewById(R.id.rv_http);
        rvHttp.setLayoutManager(new LinearLayoutManager(this));
        jokeBeanList = new LinkedList<>();
        jokeAdapter = new JokeAdapter(this, jokeBeanList);
        rvHttp.setAdapter(jokeAdapter);

    }

    @Override
    public void fillDate() {
        tvTitle.setText("okHttp测试");


    }

    @Override
    public void requestData() {
        super.requestData();

                String ret = "";
                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//                okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
                String url=ApiConfig.BASE_URL_JOKE+"list.from"+"?sort=asc&pagesize=20&time=1418816972&key="+ApiConfig.JOKE_KEY;
                LOGD(TAG,"---url"+url);
                Request request = null;
                request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                 okHttpClient.newCall(request).enqueue(new Callback() {
                     @Override
                     public void onFailure(Call call, IOException e) {

                     }

                     @Override
                     public void onResponse(Call call, Response response) throws IOException {
                         final String retStr=response.body().string();
                         LOGD(TAG,"---thread out"+Thread.currentThread().getName());
                         if (response != null && response.code() == 200) {
                             //这里没有新开线程,也没有使用异步任务,因为Okhttp本身有异步执行模式,但通过打印能
                             //能够发现回调也是在子线程中,因此这里用了runOnUiThread()方法
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     LOGD(TAG,"---thread in"+Thread.currentThread().getName());
                                     parseJson(retStr);
                                 }
                             });
                         }
                     }
                 });
    }

    public void parseJson(String json) {
        LogUtils.LOGD(TAG,"---json"+json);
        String ret = "";
        if (!TextUtils.isEmpty(json)) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(json).getAsJsonObject();
            int errorCode = jsonObject.get(ApiConfig.ERROR_CODE).getAsInt();
            if(errorCode==ApiConfig.ERROR_CODE_NUM){
                JsonObject result = jsonObject.get("result").getAsJsonObject();
                JsonArray data = result.get("data").getAsJsonArray();
                JokeBean[] jokeBeen = new Gson().fromJson(data, JokeBean[].class);
                if(jokeBeen!=null){
                    jokeBeanList.addAll(Arrays.asList(jokeBeen));
                    jokeAdapter.notifyDataSetChanged();
                }

            }else {
                String errorReason = jsonObject.get("ERROR_REASON").getAsString();
                if(!TextUtils.isEmpty(errorReason)) {
                    showToast(errorReason);
                }
            }
        }
    }

}
