package me.tx.crazydog.ui;

import android.app.Presentation;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.alibaba.fastjson.JSONObject;

public abstract class CrazyPresentationActivity extends CrazyActivity {
    private Display secondaryDisplay;
    public abstract void onPresentationActivityCreate(Bundle savedInstanceState);
    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        buildDisplay();
        onPresentationActivityCreate(savedInstanceState);
    }

    private Display buildDisplay(){
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        Display[] displays = dm.getDisplays();
        if (displays.length > 1) {
            secondaryDisplay = displays[1];
            return secondaryDisplay;
        }else {
            return null;
        }
    }

    // Activity 发送消息给最上层 Presentation
    public void sendMessage(String messageType, JSONObject data) {
        PresentationManager.getInstance().dispatchMessage(messageType, data);
    }

    public Display getSecondaryDisplay(){
        if(secondaryDisplay!=null){
            return secondaryDisplay;
        }else {
            return buildDisplay();
        }
    }

    public void openPresentation(CrazyPresentation p) {
        PresentationManager.getInstance().startPresentation(p);
    }

    // 发送测试消息
    public void sendTestMessage(View v) {
        try {
            JSONObject json = new JSONObject();
            json.put("content", "来自主屏的消息");
            sendMessage("TEST_MSG", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭全部
    public void finishAll(View v) {
        PresentationManager.getInstance().finishAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PresentationManager.getInstance().finishAll();
    }
}
