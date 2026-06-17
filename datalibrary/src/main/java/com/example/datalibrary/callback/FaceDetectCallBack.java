/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.datalibrary.callback;


import com.example.datalibrary.model.LivenessModel;

import java.util.List;

/**
 * 人脸检测回调接口。
 *
 * @Time: 2019/1/25
 * @Author: v_chaixiaogang
 */
public interface FaceDetectCallBack {
    public void onFaceDetectCallback(List<LivenessModel> livenessModel);

    public void onTip(int code, String msg);

    void onFaceDetectDarwCallback(List<LivenessModel> livenessModel);
}
