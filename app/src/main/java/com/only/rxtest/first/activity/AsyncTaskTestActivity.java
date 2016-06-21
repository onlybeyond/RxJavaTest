package com.only.rxtest.first.activity;

import android.os.AsyncTask;
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
import com.only.rxtest.common.adapter.JokeAdapter;
import com.only.rxtest.common.model.JokeBean;
import com.only.rxtest.utils.LogUtils;


import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import com.only.rxtest.common.network.ApiConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.only.rxtest.utils.LogUtils.*;


/**
 * Created by only on 16/6/14.
 *  这部的目的不是为了对比两者的优缺点,而是为了让大家通过异步任务对RxJava有一定的了解
 */
public class AsyncTaskTestActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG=makeLogTag(AsyncTaskTestActivity.class);

    //data
    private LinkedList<JokeBean> jokeBeanList;
    private JokeAdapter jokeAdapter;

    //view
    private Toolbar toolbar;
    private TextView tvTitle;




    public interface TaskHelper {
        void handleMessage(String s);
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_test_async);
        //top
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tvTitle = (TextView)findViewById(R.id.toolbar_title);

        RecyclerView rvAsync=(RecyclerView)findViewById(R.id.rv_async);


        rvAsync.setLayoutManager(new LinearLayoutManager(this));
        jokeBeanList = new LinkedList<>();
        jokeAdapter = new JokeAdapter(this, jokeBeanList);
        rvAsync.setAdapter(jokeAdapter);

        toolbar.setOnClickListener(this);
    }

    @Override
    public void fillDate() {

        tvTitle.setText("异步请求测试");
        toolbar.setNavigationIcon(R.mipmap.arrow_back);

    }

    @Override
    public void requestData() {
        //AsyncTask get data
        GetDateTask getDateTask = new GetDateTask(new TaskHelper() {
            @Override
            public void handleMessage(String s) {
                 parseJson(s);
            }
        });
        getDateTask.execute("");

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
    class GetDateTask extends AsyncTask<String, Integer, String> {

        private TaskHelper taskHelper;

        public GetDateTask(TaskHelper taskHelper) {
            this.taskHelper = taskHelper;
        }


        @Override
        protected String doInBackground(String... params) {

            String ret = "";
            OkHttpClient okHttpClient = new OkHttpClient();
//            okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//            okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
            String url=ApiConfig.BASE_URL_JOKE+"list.from"+"?sort=asc&pagesize=20&time=1418816972&key="+ApiConfig.JOKE_KEY;
            LOGD(TAG,"---url"+url);
            Request request = null;
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response != null && response.code() == 200) {
                    ret = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                taskHelper.handleMessage(s);
            }
        }
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
