/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.datalibrary.model;


import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.example.datalibrary.manager.IdentifyResult;
import com.example.datalibrary.model.User;

import java.util.ArrayList;
import java.util.List;

public class LivenessModel {

    private FaceInfo faceInfo = null;
    private float irLivenessScore;
    private long depthtLivenessDuration;
    private float rgbLivenessScore;
    private float depthLivenessScore;
    private float[] landmarks;
    private byte[] feature;
    float score;

    // 口罩数据
    private float[] mouthMaskArray;
    // 人脸得分
    private float featureScore;
    private float featureCode;
    private BDFaceImageInstance bdFaceImageInstance;

    private BDFaceImageInstance bdNirFaceImageInstance;
    private BDFaceImageInstance bdDepthFaceImageInstance;

    private User user;

//    private FaceInfo[] trackFaceInfo;
    private long allDetectDuration;

    private float maskScore;
    private long testBDFaceImageInstanceDuration; // 创建BDFaceImage耗时
    private long rgbDetectDuration; // track
    private long accurateTime; // detect 和质量判断
    private long rgbLivenessDuration; // rgb活体
    private long nirInstanceTime; // 创建nir BDFaceImage耗时
    private long irLivenessDuration; // nir detect
    private long featureDuration; // feature
    private long checkDuration; // featureSearch
    private float safetyHatScore;

    private BDFaceGazeInfo bdFaceGazeInfo; // 左右眼注意力
    private float compositeScore; // ai图片得分
    private boolean isQualityCheck; // 是否通过质量检测

    public float getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(float compositeScore) {
        this.compositeScore = compositeScore;
    }

    public BDFaceGazeInfo getBdFaceGazeInfo() {
        return bdFaceGazeInfo;
    }

    public void setBdFaceGazeInfo(BDFaceGazeInfo bdFaceGazeInfo) {
        this.bdFaceGazeInfo = bdFaceGazeInfo;
    }
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getNirInstanceTime() {
        return nirInstanceTime;
    }

    public void setNirInstanceTime(long nirInstanceTime) {
        this.nirInstanceTime = nirInstanceTime;
    }

    public long getAccurateTime() {
        return accurateTime;
    }

    public void setAccurateTime(long accurateTime) {
        this.accurateTime = accurateTime;
    }

    public long getTestBDFaceImageInstanceDuration() {
        return testBDFaceImageInstanceDuration;
    }

    public void setTestBDFaceImageInstanceDuration(long testBDFaceImageInstanceDuration) {
        this.testBDFaceImageInstanceDuration = testBDFaceImageInstanceDuration;
    }

    public float getSafetyHatScore() {
        return safetyHatScore;
    }

    public void setSafetyHatScore(float safetyHatScore) {
        this.safetyHatScore = safetyHatScore;
    }

    public boolean isQualityCheck() {
        return isQualityCheck;
    }

    public void setQualityCheck(boolean qualityCheck) {
        isQualityCheck = qualityCheck;
    }

    public float getMaskScore() {
        return maskScore;
    }

    public void setMaskScore(float maskScore) {
        this.maskScore = maskScore;
    }

    public BDFaceImageInstance getBdFaceImageInstance() {
        return bdFaceImageInstance;
    }

    public void setBdFaceImageInstance(BDFaceImageInstance bdFaceImageInstance) {
        this.bdFaceImageInstance = bdFaceImageInstance;
    }

    public long getAllDetectDuration() {
        return allDetectDuration;
    }

    public void setAllDetectDuration(long allDetectDuration) {
        this.allDetectDuration = allDetectDuration;
    }


    public BDFaceImageInstance getBdNirFaceImageInstance() {
        return bdNirFaceImageInstance;
    }

    public void setBdNirFaceImageInstance(BDFaceImageInstance bdNirFaceImageInstance) {
        this.bdNirFaceImageInstance = bdNirFaceImageInstance;
    }

    public BDFaceImageInstance getBdDepthFaceImageInstance() {
        return bdDepthFaceImageInstance;
    }

    public void setBdDepthFaceImageInstance(BDFaceImageInstance bdDepthFaceImageInstance) {
        this.bdDepthFaceImageInstance = bdDepthFaceImageInstance;
    }

    public byte[] getFeature() {
        return feature;
    }

    public void setFeature(byte[] feature) {
        this.feature = feature;
    }

    public float[] getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(float[] landmarks) {
        this.landmarks = landmarks;
    }
    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

//    public FaceInfo[] getFaceInfos() {
//        return faceInfos;
//    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

//    public void setFaceInfos(FaceInfo[] faceInfos) {
//        this.faceInfos = faceInfos;
//    }

    public long getRgbDetectDuration() {
        return rgbDetectDuration;
    }

    public void setRgbDetectDuration(long rgbDetectDuration) {
        this.rgbDetectDuration = rgbDetectDuration;
    }
    public long getRgbLivenessDuration() {
        return rgbLivenessDuration;
    }

    public void setRgbLivenessDuration(long rgbLivenessDuration) {
        this.rgbLivenessDuration = rgbLivenessDuration;
    }

    public float getIrLivenessScore() {
        return irLivenessScore;
    }

    public void setIrLivenessScore(float irLivenessScore) {
        this.irLivenessScore = irLivenessScore;
    }

    public long getIrLivenessDuration() {
        return irLivenessDuration;
    }

    public void setIrLivenessDuration(long irLivenessDuration) {
        this.irLivenessDuration = irLivenessDuration;
    }

    public long getDepthtLivenessDuration() {
        return depthtLivenessDuration;
    }

    public void setDepthtLivenessDuration(long depthtLivenessDuration) {
        this.depthtLivenessDuration = depthtLivenessDuration;
    }

    public float getRgbLivenessScore() {
        return rgbLivenessScore;
    }

    public void setRgbLivenessScore(float rgbLivenessScore) {
        this.rgbLivenessScore = rgbLivenessScore;
    }

    public float getDepthLivenessScore() {
        return depthLivenessScore;
    }

    public void setDepthLivenessScore(float depthLivenessScore) {
        this.depthLivenessScore = depthLivenessScore;
    }
    public float getFeatureScore() {
        return featureScore;
    }

    public void setFeatureScore(float featureScore) {
        this.featureScore = featureScore;
    }

    public long getFeatureDuration() {
        return featureDuration;
    }

    public void setFeatureDuration(long featureDuration) {
        this.featureDuration = featureDuration;
    }

    public long getCheckDuration() {
        return checkDuration;
    }

    public void setCheckDuration(long checkDuration) {
        this.checkDuration = checkDuration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public float getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(float featureCode) {
        this.featureCode = featureCode;
    }

    public void setMouthMaskArray(float[] mouthMaskArray) {
        this.mouthMaskArray = mouthMaskArray;
    }

    public float[] getMouthMaskArray() {
        return mouthMaskArray;
    }
}


