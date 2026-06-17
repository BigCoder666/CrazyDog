package com.example.datalibrary.factory.specific;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceSafetyHat;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class FaceSafetyHatBuilder extends ModelBuilder<FaceSafetyHat> {

    private FaceSafetyHat faceSafetyHat;
    private SdkInitListener listener;

    public FaceSafetyHatBuilder(SdkInitListener listener) {
        this.listener = listener;
    }

    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null) {
            faceSafetyHat = new FaceSafetyHat();
        } else {
            faceSafetyHat = new FaceSafetyHat(bdFaceInstance);
        }
    }

    @Override
    public void init() {
        faceSafetyHat = new FaceSafetyHat();

    }

    @Override
    public void initModel(Context context) {
        LogUtils.d(FaceModel.TAG , "faceSafetyHat.initModel");
        faceSafetyHat.initModel(context, GlobalSet.SAFETY_HAT, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                LogUtils.d(FaceModel.TAG , "faceSafetyHat.initModel code : " + code + " response :" + response);
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });
    }

    @Override
    public FaceSafetyHat getExample() {
        return faceSafetyHat;
    }
}

