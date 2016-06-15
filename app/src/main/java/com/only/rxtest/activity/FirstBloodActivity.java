package com.only.rxtest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.only.rxtest.R;
import com.only.rxtest.medol.MovieBean;
import com.only.rxtest.common.network.ApiService;
import com.only.rxtest.utils.RetrofitUtil;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.only.rxtest.utils.LogUtils.*;

/**
 * Created by only on 16/6/14.
 */
public class FirstBloodActivity extends AppCompatActivity {

    private static String TAG=makeLogTag(FirstBloodActivity.class);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_blood);
        TextView tvBloodContent = (TextView) findViewById(R.id.tv_first_blood_content);
        ApiService service = RetrofitUtil.getService();
        service.getMovie(0,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieBean>() {
                    @Override
                    public void onCompleted() {
                        LOGD(TAG,"---thread  complete");

                    }

                    @Override
                    public void onError(Throwable e) {
                        LOGD(TAG,"---thread name e"+e.toString());

                    }

                    @Override
                    public void onNext(MovieBean movieBean) {
                        String name = Thread.currentThread().getName();
                        LOGD(TAG,"---thread name"+name+"---title"+movieBean.getTitle());
                    }
                });
    }
}
