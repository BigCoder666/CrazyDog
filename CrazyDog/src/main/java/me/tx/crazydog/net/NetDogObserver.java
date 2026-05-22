package me.tx.crazydog.net;

import com.alibaba.fastjson.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import me.tx.crazydog.bean.CrazyDogBean;
import retrofit2.HttpException;

public abstract class NetDogObserver<T> extends DisposableObserver<CrazyDogBean<T>> {

    public abstract void onSuccess(T t);
    public abstract void onFailed(int code,String msg);

    @Override
    public void onNext(CrazyDogBean<T> tCrazyDogBean) {
        if(tCrazyDogBean.code==CrazyDogBean.SUCCESS_CODE){
            onSuccess(tCrazyDogBean.data);
        }else {
            onFailed(tCrazyDogBean.code,tCrazyDogBean.message);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof HttpException) {
            // HTTP 错误 404 500 401 403
            onFailed(((HttpException) e).code(),((HttpException) e).message());
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            // 网络错误、超时
            onFailed(999,e.getMessage());
        } else if (e instanceof JSONException) {
            // 解析错误
            onFailed(987,e.getMessage());
        } else if (e instanceof UnknownHostException) {
            // 域名解析失败（没网）
            onFailed(986,e.getMessage());
        } else {
            // 其他未知错误
            onFailed(900,e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }
}
