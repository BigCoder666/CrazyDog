package com.example.datalibrary.model;

public class BDQualityConfig {
    public BDQualityConfig(float blur , float illum , float gesture ,
                           float leftEye , float rightEye ,
                           float nose , float mouth , float leftCheek ,
                           float rightCheek , float chinContour){
        this.blur = blur;
        this.illum = illum;
        this.gesture = gesture;
        this.leftEye = leftEye;
        this.rightEye = rightEye;
        this.nose = nose;
        this.mouth = mouth;
        this.leftCheek = leftCheek;
        this.rightCheek = rightCheek;
        this.chinContour = chinContour;
    }
    // 三维旋转之俯仰角度[-90(上), 90(下)]，默认30
    // 平面内旋转角[-180(逆时针), 180(顺时针)]，默认30
    // 三维旋转之左右旋转角[-90(左), 90(右)]，默认30
    public float gesture ;
    // 模糊度设置，默认0.8。取值范围[0~1]，0是最清晰，1是最模糊
    public float blur;
    // 光照设置，默认0.8.取值范围[0~1], 数值越大，光线越强
    public float illum;
    // 左眼被遮挡的阈值，默认0.8
    public float leftEye;
    // 右眼被遮挡的阈值，默认0.8
    public float rightEye;
    // 鼻子被遮挡的阈值，默认0.8
    public float nose;
    // 嘴巴被遮挡的阈值，默认0.8
    public float mouth;
    // 左脸颊被遮挡的阈值，默认0.8
    public float leftCheek;
    // 右脸颊被遮挡的阈值，默认0.8
    public float rightCheek;
    // 下巴被遮挡阈值，默认为0.8
    public float chinContour;
}
