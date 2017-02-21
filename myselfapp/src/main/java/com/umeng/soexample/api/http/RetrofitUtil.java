package com.umeng.soexample.api.http;
import com.example.http.response.BaseResponse;
import com.example.http.utils.OkHttp3Utils;
import com.umeng.soexample.AppConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitUtil {

    public static final int DEFAULT_TIMEOUT = 5;

    private Retrofit mRetrofit;
    private ApiService mApiService;

    private static RetrofitUtil mInstance;
    private static OkHttpClient mOkHttpClient;

    /**
     * 私有构造方法
     */
    private RetrofitUtil(){
        if (null == mOkHttpClient) {
            mOkHttpClient = OkHttp3Utils.getOkHttpClient();
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mRetrofit = new Retrofit.Builder()
//                .client(builder.build())
                //设置使用okhttp网络请求
                .client(mOkHttpClient)
                .baseUrl(AppConfig.APP_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public static RetrofitUtil getInstance(){
        if (mInstance == null){
            synchronized (RetrofitUtil.class){
                mInstance = new RetrofitUtil();
            }
        }
        return mInstance;
    }

//    /**
//     * 用于获取用户信息
//     * @param subscriber
//     */
//    public void getUsers(Subscriber<BaseResponse<List<UserModel>>> subscriber){
//        mApiService.getUsersByRx()
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//    }
//
//    public void getUsersByMore(Subscriber<BaseResponse<? extends Object>> subscriber){
//        mApiService.getUsersByRx()
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//    }

    private <T> void toSubscribe(Observable<T> observable,Subscriber<T> subscriber){
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    //验证手机号码   这里是我自己的接口
    public void verfcationNum(Map<String,String> map,Subscriber<BaseResponse> subscriber){
//        mApiService.getVerfcationCodePostMap(map)
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
        toSubscribe(mApiService.getVerfcationCodePostMap(map),subscriber);
    }

    //上传头像  这里是我自己的接口
    public void uploadImg(MultipartBody.Part part,Map<String,RequestBody>map,Subscriber<BaseResponse>subscriber){

        mApiService.uploadImage(map,part)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
