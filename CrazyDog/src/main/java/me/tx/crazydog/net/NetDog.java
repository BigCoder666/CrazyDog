package me.tx.crazydog.net;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

public class NetDog<T> {
    NetDogSetting netDogSetting = null;
    OkHttpClient baseClient = null;

    T service = null;

    public String TAG = "";

    public NetDog(){

    }

    public NetDog(NetDogSetting<T> netDogSetting){
        this.netDogSetting = netDogSetting;
        OkHttpClient.Builder builder  = new OkHttpClient.Builder()
                .connectTimeout(netDogSetting.getHttpTimeOut(), TimeUnit.SECONDS) // 超时
                .readTimeout(netDogSetting.getHttpTimeOut(), TimeUnit.SECONDS);

        builder.addInterceptor(chain ->{
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();

            HashMap<String,String> header = netDogSetting.getBaseHeader();
            for(String k:header.keySet()){
                requestBuilder.addHeader(k,header.get(k));
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        builder.addInterceptor(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY));

        baseClient = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(new Retrofit2ConverterFactory())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(baseClient)
                .baseUrl( this.netDogSetting .getBaseUrl())
                .build();
        service = retrofit.create(netDogSetting.getServiceClass());

        TAG = netDogSetting.getServiceClass().getSimpleName();
    }

    public T getService(){
        return service;
    }

    public void onAddHeader(String key, String value) {
        if(netDogSetting!=null){
            netDogSetting.getBaseHeader().put(key,value);
        }
    }

}
