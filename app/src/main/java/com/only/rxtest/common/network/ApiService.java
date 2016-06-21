package com.only.rxtest.common.network;

import com.only.rxtest.common.model.JokeBean;
import com.only.rxtest.common.model.Result;
import com.only.rxtest.common.model.MovieBean;
import com.only.rxtest.common.model.ServerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by only on 16/6/14.
 */
public interface ApiService {




    @POST("top250")
    Observable<MovieBean>getMovie( @Query("start") int start, @Query("count") int count);





    @GET("list.from")
    Call<ServerResponse<Result<List<JokeBean>>>> getJoke(@Query("sort") String sort,
                                                         @Query("page") int page, @Query("pagesize") int pageSize,
                                                         @Query("time") String time, @Query("key") String key);
}
