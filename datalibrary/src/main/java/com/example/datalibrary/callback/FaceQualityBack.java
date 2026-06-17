package com.example.datalibrary.callback;

import android.content.Context;
import android.widget.Toast;

import com.example.datalibrary.model.LivenessModel;

import java.util.List;

public class FaceQualityBack implements FaceDetectCallBack{
    Context context;
    public FaceQualityBack(Context context){
        this.context = context;
    }
    @Override
    public void onFaceDetectCallback(List<LivenessModel> models) {

    }

    @Override
    public void onTip(int code, String msg) {
        Toast.makeText(context , msg , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFaceDetectDarwCallback(List<LivenessModel> models) {

    }
}
