package com.example.datalibrary.model;

public class BDLiveConfig {
    public BDLiveConfig(float rgbLiveScore , float nirLiveScore , float depthLiveScore){
        this.rgbLiveScore = rgbLiveScore;
        this.nirLiveScore = nirLiveScore;
        this.depthLiveScore = depthLiveScore;
    }
    public BDLiveConfig(float rgbLiveScore , float nirLiveScore , float depthLiveScore , int framesThreshold){
        this.rgbLiveScore = rgbLiveScore;
        this.nirLiveScore = nirLiveScore;
        this.depthLiveScore = depthLiveScore;
        this.framesThreshold = framesThreshold;
    }
    public float rgbLiveScore; // rgb活体阈值
    public float nirLiveScore; // nir活体阈值
    public float depthLiveScore; // depth活体阈值
    public int framesThreshold = 1; // 识别帧数
}
