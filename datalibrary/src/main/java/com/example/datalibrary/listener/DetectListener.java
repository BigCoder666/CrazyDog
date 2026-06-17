package com.example.datalibrary.listener;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;

public interface DetectListener {
    public void onDetectSuccess(FaceInfo[] faceInfos , BDFaceImageInstance rgbInstance);
    public void onDetectFail();
}
