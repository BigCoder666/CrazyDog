package me.tx.crazydog.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.tx.crazydog.CrazyDog;
import me.tx.crazydog.task.TaskDog;

public abstract class CrazyActivity extends AppCompatActivity {
    Bundle savedInstanceState;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public abstract void onActivityCreate(Bundle savedInstanceState);
    public <T> T getService(Class<T> service){
        T t = CrazyDog.getInstance().getNetDogService(service);
        if(t==null){
            if(getApplication() instanceof  CrazyApp){
                return ((CrazyApp)getApplication()).getService(service);
            }else {
                return null;
            }
        }else{
            return t;
        }
    }

    public <T> void addHeader(Class<T> service,String k,String v){
        if(getApplication() instanceof  CrazyApp){
            ((CrazyApp)getApplication()).addHeader(service,k,v);
        }else {
            CrazyDog.getInstance().addHeader(service,k,v);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        // 开启全屏（Android 15+ 新标准，替代老写法）
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // 状态栏文字黑色（最常用）
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        onActivityCreate(this.savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    public <T> Disposable requestIO2Main(Observable<T> observable, DisposableObserver<T> observer) {
        Disposable d = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
        disposables.add(d);
        return d;
    }


}
