package com.example.datalibrary.factory.specific;

import static com.example.datalibrary.manager.FaceSDKManager.SDK_MODEL_LOAD_SUCCESS;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceSearch;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.example.datalibrary.factory.builder.ModelBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceModel;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.GlobalSet;
import com.example.datalibrary.utils.LogUtils;

public class FeatureBuilder extends ModelBuilder<FaceFeature> {

    public void setFaceFeature(FaceFeature faceFeature) {
        this.faceFeature = faceFeature;
    }
    private FaceFeature faceFeature;
    private FaceSearch faceSearch;
    private SdkInitListener listener;
    public FeatureBuilder(SdkInitListener listener){
        this.listener = listener;
    }
    @Override
    public void init(BDFaceInstance bdFaceInstance) {
        if (bdFaceInstance == null){
            faceFeature = new FaceFeature();
            faceSearch = new FaceSearch();
        }else {
            faceFeature = new FaceFeature(bdFaceInstance);
            faceSearch = new FaceSearch(bdFaceInstance);
        }
        faceSearch.setInputDBListener(new FaceSearch.InputDBListener() {
            @Override
            public void onInputDB(int i, int i1) {
                Log.e("face_feature_db_add" , i + " " + i1);
            }
        });
        faceSearch.setMaxUpdateSize(0);
        faceSearch.setInputDBIntervalTime(0);
        faceSearch.setRegisterCompareThreshold(90f);
        faceSearch.setUpdateCompareThreshold(0.9f);
        faceSearch.setInputDBThreshold(0.92f);

    }

    @Override
    public void init() {
        faceFeature = new FaceFeature();
        faceSearch = new FaceSearch();
        faceSearch.setMaxUpdateSize(0);
        faceSearch.setInputDBIntervalTime(0);
        faceSearch.setRegisterCompareThreshold(90f);
        faceSearch.setUpdateCompareThreshold(0.9f);
        faceSearch.setInputDBThreshold(0.92f);
    }

    @Override
    public void initModel(Context context) {
        faceFeature.initModel(context,
                GlobalSet.RECOGNIZE_IDPHOTO_MODEL,
                GlobalSet.RECOGNIZE_VIS_MODEL,
                GlobalSet.RECOGNIZE_NIR_MODEL,
                "",
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        LogUtils.d(FaceModel.TAG , "faceFeature.initModel code : " + code + " response :" + response);
//                        LogUtils.e(TIME_TAG, "init model time = " + (endInitModelTime - startInitModelTime));
                        if (code != 0) {
//                            ToastUtils.toast(context, "模型加载失败,尝试重启试试");
                            if (listener != null) {
                                listener.initModelFail(code, response);
                            }
                        } else {
                            FaceSDKManager.initStatus = SDK_MODEL_LOAD_SUCCESS;
                            // 模型初始化成功，加载人脸数据
//                            ToastUtils.toast(context, "模型加载完毕，欢迎使用");
                            if (listener != null) {
                                listener.initModelSuccess();
                            }
                        }
                    }
                });
    }

    @Override
    public FaceFeature getExample() {
        return faceFeature;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public FaceSearch getFaceSearch() {
        return faceSearch;
    }
}
