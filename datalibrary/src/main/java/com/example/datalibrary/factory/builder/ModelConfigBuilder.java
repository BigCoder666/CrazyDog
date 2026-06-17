package com.example.datalibrary.factory.builder;

import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;

public abstract class ModelConfigBuilder<T> extends ModelBuilder<T>{
    public abstract void init(BDFaceInstance bdFaceInstance , BDFaceSDKConfig config);
//    public abstract void initFastModel(Context context);
//    public abstract void initAccurateModel(Context context);
//    public abstract void initQualityModel(Context context);
//    public abstract void initAttrbuteModel(Context context);
//    public abstract void initBestImageModel(Context context);
}
