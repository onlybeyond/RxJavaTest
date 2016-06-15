package com.only.rxtest.utils;

import com.only.rxtest.common.network.ApiConfig;
import com.only.rxtest.common.network.ApiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.only.rxtest.utils.LogUtils.*;

/**
 * Created by only on 16/6/14.
 */
public class RetrofitUtil {

    private static String TAG=makeLogTag(RetrofitUtil.class);
    private static OkHttpClient okHttpClient;
    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String name = Thread.currentThread().getName();

                LOGD(TAG,"---thread name"+name);
            }
        });
        okHttpClient=new OkHttpClient.Builder().addInterceptor(interceptor).build();

    }



    /**
     * get
     * @return
     */
 public static ApiService getService(){
     Retrofit retrofit = getRetrofit();
     return retrofit.create(ApiService.class);
 }

    /**
     * create retrofit object
     * @return
     */
  private static Retrofit getRetrofit(){
     Retrofit retrofit=new Retrofit.Builder()
             .baseUrl(ApiConfig.BASE_URL_JOKE)
             .client(okHttpClient)
             .addConverterFactory(GsonConverterFactory.create())
             .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
             .build();
      return retrofit;
    }
}
