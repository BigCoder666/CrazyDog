package me.tx.crazydog.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONException;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DisposableObserver;
import me.tx.crazydog.R;
import me.tx.crazydog.bean.CrazyDogBean;
import retrofit2.HttpException;

public abstract class LoadingObserver<T> extends NetDogObserver<T> {
    private final AlertDialog loadingDialog;

    public abstract int getLoadingImageResource();

    public LoadingObserver(Context context) {
        View loadingView = LayoutInflater.from(context)
                .inflate(R.layout.crazydog_dialog_loading, null);

        ImageView loading_img = loadingView.findViewById(R.id.crazydog_loading_img);
        loading_img.setImageResource(getLoadingImageResource());
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rotate_forever);
        loading_img.startAnimation(animation);

        loadingDialog = new MaterialAlertDialogBuilder(context,R.style.CrazyTheme)
                .setView(loadingView)
                .setCancelable(false)
                .create();
        // 背景全透明
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onStart() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void onComplete() {
        dismissLoading();
    }

    @Override
    public void onError(Throwable e) {
        // 请求失败也要关闭loading
        super.onError(e);
        dismissLoading();
    }

    // 安全关闭loading
    private void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
