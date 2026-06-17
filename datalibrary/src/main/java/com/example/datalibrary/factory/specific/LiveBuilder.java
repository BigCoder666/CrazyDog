package com.example.datalibrary.factory.specific;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class LiveBuilder extends ModelBuilder<FaceLive> {

    private FaceLive faceLiveness;
    private SdkInitListener listener;
    public LiveBuilder(SdkInitListener listener){
        this.listener = listener;
    }
    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null){
            faceLiveness = new FaceLive();
        }else {
            faceLiveness = new FaceLive(bdFaceInstance);
        }
    }

    @Override
    public void init() {
        faceLiveness = new FaceLive();
    }

    @Override
    public void initModel(Context context) {
        LogUtils.d(FaceModel.TAG , " LiveBuilder initModel");
        faceLiveness.initModel(context,
                GlobalSet.LIVE_VIS_MODEL,
//                GlobalSet.LIVE_VIS_2DMASK_MODEL,
//                GlobalSet.LIVE_VIS_HAND_MODEL,
//                GlobalSet.LIVE_VIS_REFLECTION_MODEL,
                "", "", "",
                GlobalSet.LIVE_NIR_MODEL,
                GlobalSet.LIVE_DEPTH_MODEL,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        LogUtils.d(FaceModel.TAG , "faceLiveness.initModel code : " + code + " response :" + response);
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });
    }

    @Override
    public FaceLive getExample() {
        return faceLiveness;
    }

    public FaceLive getFaceLiveness() {
        return faceLiveness;
    }
}
