package com.sarah.commonapplication.net;

import com.sarah.commonapplication.mvp.model.HttpResult;
import com.sarah.commonapplication.mvp.model.MovieEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Shan Yin on 11/20/18.
 */
public interface ApiService {

    @GET("top250")
    Observable<HttpResult<List<MovieEntity>>> getTopMovie(@Query("start") int start, @Query("count") int count);
}