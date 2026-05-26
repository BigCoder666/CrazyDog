package me.tx.crazydog.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.alibaba.fastjson.JSONObject;

public abstract class CrazyPresentation extends android.app.Presentation {
    public abstract void onPresentationCreate(Bundle savedInstanceState);
    public CrazyPresentation(Context context, Display display) {
        super(context, display);
    }

    // 接收 Activity 消息（只有栈顶会收到）
    public void onActivityMessage(String messageType, JSONObject data) {}

    // 关闭当前页面，返回上一页
    public void finishPresentation() {
        PresentationManager.getInstance().finishPresentation(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PresentationManager.getInstance().addPresentation(this);
        onPresentationCreate(savedInstanceState);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        PresentationManager.getInstance().removePresentation(this);
    }
}