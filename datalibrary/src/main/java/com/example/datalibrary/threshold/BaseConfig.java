package com.example.datalibrary.threshold;

/**
 * author : shangrong
 * date : 2019/5/22 9:10 PM
 * description :配置文件
 */
public class BaseConfig {
    // RGB预览Y轴转向falese为0，true为180
    private Boolean rgbRevert = false;
    // 默认为80px。可传入大于50px的数值，小于此大小的人脸不予检测
    private int minimumFace = 60;
    // 人脸置信度用于表征被检测到的物体是人脸的概率，该阈值设置越高检测越严格，建议在0.3-0.8区间内调整阈值
    private float faceThreshold = 0.5f;
    // 模糊度设置，默认0.5。取值范围[0~1]，0是最清晰，1是最模糊
    private float blur = 0.8f;
    // 光照设置，默认0.8.取值范围[0~1], 数值越大，光线越强
    private float illum = 0.8f;
    // 三维旋转之俯仰角度[-90(上), 90(下)]，默认30
    // 平面内旋转角[-180(逆时针), 180(顺时针)]，默认30
    // 三维旋转之左右旋转角[-90(左), 90(右)]，默认30
    private int gesture = 30;
    // 左眼被遮挡的阈值，默认0.8
    private float leftEye = 0.8f;
    // 右眼被遮挡的阈值，默认0.8
    private float rightEye = 0.8f;
    // 鼻子被遮挡的阈值，默认0.8
    private float nose = 0.8f;
    // 嘴巴被遮挡的阈值，默认0.8
    private float mouth = 0.8f;
    // 左脸颊被遮挡的阈值，默认0.8
    private float leftCheek = 0.8f;
    // 右脸颊被遮挡的阈值，默认0.8
    private float rightCheek = 0.8f;
    // 下巴被遮挡阈值，默认为0.8
    private float chinContour = 0.8f;
    // 识别阈值，0-100，默认为80分,需要选择具体模型的阈值。live：80、idcard：80
    private float liveScoreThreshold = 80f;
    // 识别阈值，0-100，默认为80分,需要选择具体模型的阈值。live：80、idcard：80
    private float idScoreThreshold = 80f;
    // 识别阈值，0-100，默认为80分,需要选择具体模型的阈值。live：80、idcard：80
    private float rgbAndNirScoreThreshold = 80f;
    // 模态切换光线阈值
    private int cameraLightThreshold = 50;
    // 使用的特征抽取模型默认为生活照：1；证件照：2；RGB+NIR混合模态模型：3；
    private int activeModel = 1;
    // 不使用活体: 0
    // RGB活体：1
    // RGB+NIR活体：2
    private int type = 1;
    // 是否开启质量检测开关
    private boolean qualityControl = true;
    // 是否开启活体检测开关
    private boolean livingControl = true;
    // RGB活体阀值
    private float rgbLiveScore = 0.80f;
    // NIR活体阀值
    private float nirLiveScore = 0.80f;
    // Depth活体阀值
    private float depthLiveScore = 0.80f;
    // 是否开启暗光恢复
    private boolean darkEnhance = false;
    // 是否开启best image
    private boolean bestImage = true;
    // 是否开启Log日志
    private boolean log = true;
    // 摄像头显示位置
    private int rbgCameraId = -1;

    // 是否开启openGL渲染 (建议开启后设置为640*480分辨率)
    private boolean isOpenGl = false;

    // 0:奥比中光海燕、大白（640*400）
    // 1:奥比中光海燕Pro、Atlas（400*640）
    // 2:奥比中光蝴蝶、Astra Pro\Pro S（640*480）
    // 3:舜宇Seeker06
    // 4:螳螂慧视天蝎P1
    // 5:瑞识M720N
    // 6:奥比中光Deeyea(结构光)
    // 7:华捷艾米A100S、A200(结构光)
    // 8:Pico DCAM710(ToF)
    private int cameraType = 0;


    // 是否开启属性检测
    private boolean attribute = false;

    // rgb和nir摄像头宽
    private int rgbAndNirWidth = 640;
    // rgb和nir摄像头高
    private int rgbAndNirHeight = 480;
    // depth摄像头宽
    private int depthWidth = 640;
    // depth摄像头高
    private int depthHeight = 400;
    // 默认为0。可传入0、90、180、270四个选项。
    private int rgbVideoDirection = 0;

    // 默认为0。可传入0、90、180、270四个选项。
    private int nirVideoDirection = 0;

    // 0：RGB无镜像，1：有镜像
    private int mirrorVideoRGB = 0;

    // 0：Nir无镜像，1：有镜像
    private int mirrorVideoNIR = 0;

    // rbg人脸检测角度 默认为0。可传入0、90、180、270四个选项。
    private int rgbDetectDirection = 0;

    // nir人脸检测角度 默认为0。可传入0、90、180、270四个选项。
    private int nirDetectDirection = 0;

    // rbg人脸检测 0：RGB无镜像，1：有镜像
    private int mirrorDetectRGB = 0;

    // nir人脸检测 0：RGB无镜像，1：有镜像
    private int mirrorDetectNIR = 0;

    public boolean isOpenGl() {
        return isOpenGl;
    }

    public void setOpenGl(boolean openGl) {
        isOpenGl = openGl;
    }

    public int getRBGCameraId() {
        return rbgCameraId;
    }

    public void setRBGCameraId(int rbgCameraId) {
        this.rbgCameraId = rbgCameraId;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isBestImage() {
        return bestImage;
    }

    public void setBestImage(boolean bestImage) {
        this.bestImage = bestImage;
    }
    public boolean isDarkEnhance() {
        return darkEnhance;
    }

    public void setDarkEnhance(boolean darkEnhance) {
        this.darkEnhance = darkEnhance;
    }

    public int getRgbDetectDirection() {
        return rgbDetectDirection;
    }

    public void setRgbDetectDirection(int rgbDetectDirection) {
        this.rgbDetectDirection = rgbDetectDirection;
    }

    public int getNirDetectDirection() {
        return nirDetectDirection;
    }

    public void setNirDetectDirection(int nirDetectDirection) {
        this.nirDetectDirection = nirDetectDirection;
    }

    public int getMirrorDetectRGB() {
        return mirrorDetectRGB;
    }

    public void setMirrorDetectRGB(int mirrorDetectRGB) {
        this.mirrorDetectRGB = mirrorDetectRGB;
    }

    public int getMirrorDetectNIR() {
        return mirrorDetectNIR;
    }

    public void setMirrorDetectNIR(int mirrorDetectNIR) {
        this.mirrorDetectNIR = mirrorDetectNIR;
    }

    public int getNirVideoDirection() {
        return nirVideoDirection;
    }

    public void setNirVideoDirection(int nirVideoDirection) {
        this.nirVideoDirection = nirVideoDirection;
    }

    public int getMirrorVideoNIR() {
        return mirrorVideoNIR;
    }

    public void setMirrorVideoNIR(int mirrorVideoNIR) {
        this.mirrorVideoNIR = mirrorVideoNIR;
    }

    public int getMirrorVideoRGB() {
        return mirrorVideoRGB;
    }

    public void setMirrorVideoRGB(int mirrorVideoRGB) {
        this.mirrorVideoRGB = mirrorVideoRGB;
    }

    public int getRgbVideoDirection() {
        return rgbVideoDirection;
    }

    public void setRgbVideoDirection(int rgbVideoDirection) {
        this.rgbVideoDirection = rgbVideoDirection;
    }

    public int getRgbAndNirWidth() {
        return rgbAndNirWidth;
    }

    public void setRgbAndNirWidth(int rgbAndNirWidth) {
        this.rgbAndNirWidth = rgbAndNirWidth;
    }

    public int getRgbAndNirHeight() {
        return rgbAndNirHeight;
    }

    public void setRgbAndNirHeight(int rgbAndNirHeight) {
        this.rgbAndNirHeight = rgbAndNirHeight;
    }

    public int getDepthWidth() {
        return depthWidth;
    }

    public void setDepthWidth(int depthWidth) {
        this.depthWidth = depthWidth;
    }

    public int getDepthHeight() {
        return depthHeight;
    }

    public void setDepthHeight(int depthHeight) {
        this.depthHeight = depthHeight;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }


    public float getRgbLiveScore() {
        return rgbLiveScore;
    }

    public void setRgbLiveScore(float rgbLiveScore) {
        this.rgbLiveScore = rgbLiveScore;
    }

    public float getNirLiveScore() {
        return nirLiveScore;
    }

    public void setNirLiveScore(float nirLiveScore) {
        this.nirLiveScore = nirLiveScore;
    }

    public float getDepthLiveScore() {
        return depthLiveScore;
    }

    public void setDepthLiveScore(float depthLiveScore) {
        this.depthLiveScore = depthLiveScore;
    }

    public float getFaceThreshold() {
        return faceThreshold;
    }

    public void setFaceThreshold(float faceThreshold) {
        this.faceThreshold = faceThreshold;
    }

    public int getMinimumFace() {
        return minimumFace;
    }

    public void setMinimumFace(int minimumFace) {
        this.minimumFace = minimumFace;
    }

    public float getBlur() {
        return blur;
    }

    public void setBlur(float blur) {
        this.blur = blur;
    }

    public float getIllumination() {
        return illum;
    }

    public void setIllumination(float illum) {
        this.illum = illum;
    }


    public void setGesture(int gesture) {
        this.gesture = gesture;
    }
    public int getGesture() {
        return gesture ;
    }
    public float getLeftEye() {
        return leftEye;
    }

    public void setLeftEye(float leftEye) {
        this.leftEye = leftEye;
    }

    public float getRightEye() {
        return rightEye;
    }

    public void setRightEye(float rightEye) {
        this.rightEye = rightEye;
    }

    public float getNose() {
        return nose;
    }

    public void setNose(float nose) {
        this.nose = nose;
    }

    public float getMouth() {
        return mouth;
    }

    public void setMouth(float mouth) {
        this.mouth = mouth;
    }

    public float getLeftCheek() {
        return leftCheek;
    }

    public void setLeftCheek(float leftCheek) {
        this.leftCheek = leftCheek;
    }

    public float getRightCheek() {
        return rightCheek;
    }

    public void setRightCheek(float rightCheek) {
        this.rightCheek = rightCheek;
    }

    public float getChinContour() {
        return chinContour;
    }

    public void setChinContour(float chinContour) {
        this.chinContour = chinContour;
    }
    public float getLiveThreshold() {
        return liveScoreThreshold;
    }

    public void setLiveThreshold(float liveScoreThreshold) {
        this.liveScoreThreshold = liveScoreThreshold;
    }

    public float getIdThreshold() {
        return idScoreThreshold;
    }

    public void setIdThreshold(float idScoreThreshold) {
        this.idScoreThreshold = idScoreThreshold;
    }

    public int getActiveModel() {
        return activeModel;
    }

    public void setActiveModel(int activeModel) {
        this.activeModel = activeModel;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isQualityControl() {
        return qualityControl;
    }

    public void setQualityControl(boolean qualityControl) {
        this.qualityControl = qualityControl;
    }


    public Boolean getRgbRevert() {
        return rgbRevert;
    }

    public void setRgbRevert(Boolean rgbRevert) {
        this.rgbRevert = rgbRevert;
    }


    public boolean isAttribute() {
        return attribute;
    }

    public void setAttribute(boolean attribute) {
        this.attribute = attribute;
    }

    public boolean isLivingControl() {
        return livingControl;
    }

    public void setLivingControl(boolean livingControl) {
        this.livingControl = livingControl;
    }

    public int getCameraLightThreshold() {
        return cameraLightThreshold;
    }

    public void setCameraLightThreshold(int cameraLightThreshold) {
        this.cameraLightThreshold = cameraLightThreshold;
    }

    public float getRgbAndNirThreshold() {
        return rgbAndNirScoreThreshold;
    }

    public void setRgbAndNirThreshold(float rgbAndNirScoreThreshold) {
        this.rgbAndNirScoreThreshold = rgbAndNirScoreThreshold;
    }
}
