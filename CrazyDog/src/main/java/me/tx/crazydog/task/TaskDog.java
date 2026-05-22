package me.tx.crazydog.task;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class TaskDog<T> {

    public abstract T taskDetail();

    public interface ITaskResult<T> {
        void done(T t);
        void failed(String reason);
    }

    // 每次 start 都创建新的 Observable，不再用成员变量缓存
    private Observable<T> createObservable() {
        return Observable.fromCallable(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        return taskDetail();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 启动任务，并返回 Disposable 用于取消
    public Disposable start(ITaskResult<T> iTaskResult) {
        return createObservable().subscribe(
                new Consumer<T>() {
                    @Override
                    public void accept(T result) throws Exception {
                        if (iTaskResult != null) {
                            iTaskResult.done(result);
                        }
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (iTaskResult == null) return;

                        String msg = throwable.getMessage();
                        if (msg == null || msg.isEmpty()) {
                            msg = "未知错误";
                        }
                        iTaskResult.failed(msg);
                    }
                }
        );
    }
}