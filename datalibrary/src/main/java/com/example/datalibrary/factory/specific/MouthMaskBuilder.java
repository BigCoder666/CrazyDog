package com.example.datalibrary.factory.specific;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class MouthMaskBuilder  extends ModelBuilder<FaceMouthMask> {

    private FaceMouthMask faceMouthMask;
    private SdkInitListener listener;

    public MouthMaskBuilder(SdkInitListener listener) {
        this.listener = listener;
    }

    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null) {
            faceMouthMask = new FaceMouthMask();
        } else {
            faceMouthMask = new FaceMouthMask(bdFaceInstance);
        }
    }

    @Override
    public void init() {
        faceMouthMask = new FaceMouthMask();

    }

    @Override
    public void initModel(Context context) {
        LogUtils.d(FaceModel.TAG , "faceMouthMask.initModel");
        faceMouthMask.initModel(context, GlobalSet.MOUTH_MASK, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                LogUtils.d(FaceModel.TAG , "faceMouthMask.initModel code : " + code + " response :" + response);
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });
    }

    @Override
    public FaceMouthMask getExample() {
        return faceMouthMask;
    }
}

