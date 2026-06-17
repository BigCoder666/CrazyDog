package com.example.datalibrary.factory.specific;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class DetectNirBuilder extends ModelBuilder<FaceDetect> {

    private FaceDetect faceNirDetect;
    private SdkInitListener listener;
    public DetectNirBuilder(SdkInitListener listener){
        this.listener = listener;
    }
    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null){
            faceNirDetect = new FaceDetect();
        }else {
            faceNirDetect = new FaceDetect(bdFaceInstance);
        }
    }

    @Override
    public void init() {

        faceNirDetect = new FaceDetect();
    }

    @Override
    public void initModel(Context context) {
        initAccurateModel(context);
    }

    @Override
    public FaceDetect getExample() {
        return faceNirDetect;
    }

    public void initAccurateModel(Context context) {
        LogUtils.d(FaceModel.TAG , " DetectNirBuilder initAccurateModel");
        faceNirDetect.initModel(context,
                GlobalSet.DETECT_NIR_MODE,
                GlobalSet.ALIGN_NIR_MODEL, BDFaceSDKCommon.DetectType.DETECT_NIR,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        LogUtils.d(FaceModel.TAG , "faceNirDetect.initModel code : " + code + " response :" + response);
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });

    }
    public FaceDetect getFaceNirDetect() {
        return faceNirDetect;
    }
}
