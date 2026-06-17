package com.example.datalibrary.factory.specific;

import android.content.Context;

import com.baidu.idl.main.facesdk.FaceGaze;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class FaceGazeBuilder extends ModelBuilder<FaceGaze> {

    private FaceGaze faceGaze;
    private SdkInitListener listener;

    public FaceGazeBuilder(SdkInitListener listener) {
        this.listener = listener;
    }

    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null) {
            faceGaze = new FaceGaze();
        } else {
            faceGaze = new FaceGaze(bdFaceInstance);
        }
    }

    @Override
    public void init() {
        faceGaze = new FaceGaze();

    }

    @Override
    public void initModel(Context context) {
        LogUtils.d(FaceModel.TAG , "faceGaze.initModel");
        faceGaze.initModel(context, GlobalSet.GAZE_MODEL, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });
    }

    @Override
    public FaceGaze getExample() {
        return faceGaze;
    }
}

