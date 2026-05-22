package me.tx.crazydog;

import android.content.Context;

import me.tx.crazydog.net.LoadingObserver;

public abstract class DogeObserver<T> extends LoadingObserver<T> {
    public DogeObserver(Context context) {
        super(context);
    }

    @Override
    public int getLoadingImageResource() {
        return R.drawable.doge;
    }
}
