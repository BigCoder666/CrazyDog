package com.example.datalibrary.model;

import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;

public class BDFaceCheckConfig {
    public int featureCheckMode; // 特征抽取模式【不提取特征：1】；【提取特征：2】；【提取特征+1：N 检索：3】;【提取特征+1：1 检索：4】
    public boolean darkEnhance; // 暗光回复
    public float scoreThreshold; // 识别阈值，0-1
    public boolean bestImage; // 是否开启best image
    public BDFaceDetectListConf bdFaceDetectListConfig; // detect参数
    public BDQualityConfig bdQualityConfig; // 质量阈值
    public BDLiveConfig bdLiveConfig; // 活体阈值

    public byte[] secondFeature; // 1:1特征值

    // 0:奥比中光海燕、大白（640*400）
    // 1:奥比中光海燕Pro、Atlas（400*640）
    // 2:奥比中光蝴蝶、Astra Pro\Pro S（640*480）
    // 3:舜宇Seeker06
    // 4:螳螂慧视天蝎P1
    // 5:瑞识M720N
    // 6:奥比中光Deeyea(结构光)
    // 7:华捷艾米A100S、A200(结构光)
    // 8:Pico DCAM710(ToF)
    public int cameraType = 0;
    // 使用的特征抽取模型默认为生活照：1；证件照：3；RGB+NIR混合模态模型：
    public int activeModel;
    public BDFaceCheckConfig(int featureCheckMode , boolean darkEnhance , float scoreThreshold ,
                             boolean bestImage , int cameraType , int activeModel ,
                             BDFaceDetectListConf bdFaceDetectListConfig , BDQualityConfig bdQualityConfig ,
                             BDLiveConfig bdLiveConfig
                             ){
        this.featureCheckMode = featureCheckMode;
        this.darkEnhance = darkEnhance;
        this.scoreThreshold = scoreThreshold;
        this.bestImage = bestImage;
        this.cameraType = cameraType;
        this.activeModel = activeModel;
        this.bdFaceDetectListConfig = bdFaceDetectListConfig;
        this.bdQualityConfig = bdQualityConfig;
        this.bdLiveConfig = bdLiveConfig;
    }

    public byte[] getSecondFeature() {
        return secondFeature;
    }

    public void setSecondFeature(byte[] secondFeature) {
        this.secondFeature = secondFeature;
    }
}
