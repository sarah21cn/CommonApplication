package com.sarah.commonapplication.net;

import android.util.Log;

import com.xiaomi.mico.infrareddevicemodule.mvp.model.Banner;
import com.xiaomi.mico.infrareddevicemodule.mvp.model.HttpResult;
import com.xiaomi.mico.infrareddevicemodule.mvp.model.MovieEntity;
import com.xiaomi.mico.infrareddevicemodule.mvp.model.exception.ApiException;

import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shan Yin on 11/20/18.
 */
public class HttpManager {

    private static String BASE_URL = "https://api.mina.mi.com/";
    private static long DEFAULT_TIMEOUT = 5l;

    private Retrofit mRetrofit;
    private ApiService mService;

    private HttpManager(){
        init();
    }

    private void init(){
        Interceptor logging = chain -> {
            Request request = chain.request();
            Log.d("okhttp", "okhttp--->" + request.url().toString());
            return chain.proceed(request);
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        mService = mRetrofit.create(ApiService.class);
    }

    private static class NetworkHolder{
        public final static HttpManager sInstance = new HttpManager();
    }

    public static HttpManager getInstance(){
        return NetworkHolder.sInstance;
    }

    private class HttpResultFunc<T> implements Function<HttpResult<T>, T>{

        @Override
        public T apply(HttpResult<T> tHttpResult) throws ApiException{
            if(tHttpResult != null && tHttpResult.getResultCode() != 0){
                throw new ApiException(tHttpResult.getResultCode(), tHttpResult.getResultMessage());
            }
            return tHttpResult.getData();
        }
    }

    public Observable<List<MovieEntity>> getTopMovie(int start, int count){
        return mService.getTopMovie(start, count)
                .map(new HttpResultFunc<List<MovieEntity>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Banner> getBanner(int type){
        return mService.getBanner(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
