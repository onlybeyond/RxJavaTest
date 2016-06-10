package com.only.rxtest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://api.douban.com/v2/movie/top250";

    //view
    private TextView tvContentRx;
    private TextView tvContentTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvContentRx = (TextView) findViewById(R.id.tv_content_rx);
        tvContentTask = (TextView) findViewById(R.id.tv_content_task);

        //AsyncTask get data
        GetDateTask getDateTask = new GetDateTask(new TaskHelper() {
            @Override
            public void handleMessage(String s) {
                String data = parseJson(s);
                tvContentTask.setText(data);
            }
        });
        getDateTask.execute("");
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
                okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
                HashMap<String, String> params = new HashMap<>();
                params.put("start", "0");
                params.put("count", "1");
                FormEncodingBuilder formBodyBuilder = new FormEncodingBuilder();
                Request request = null;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    formBodyBuilder.add(entry.getKey(), entry.getValue() + "");
                }
                RequestBody formBody = formBodyBuilder.build();
                request = new Request.Builder()
                        .url(BASE_URL)
                        .post(formBody)
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
                        String data = parseJson(str);
                        tvContentRx.setText(data);

                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public interface TaskHelper {
        void handleMessage(String s);
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
            okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
//                Drawable drawable = getTheme().getDrawable(R.mipmap.ic_launcher);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("start", "1");
            hashMap.put("count", "1");
            FormEncodingBuilder formBodyBuilder = new FormEncodingBuilder();
            Request request = null;

            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue() + "");
            }
            RequestBody formBody = formBodyBuilder.build();
            request = new Request.Builder()
                    .url(BASE_URL)
                    .post(formBody)
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

    public String parseJson(String json) {
        String ret = "";
        if (!TextUtils.isEmpty(json)) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(json).getAsJsonObject();
            String count = jsonObject.get("count").getAsString();
            String start = jsonObject.get("start").getAsString();
            String total = jsonObject.get("total").getAsString();
            ret = "count:" + count + "start:" + start + "total:" + total;
        }
        return ret;
    }
}
