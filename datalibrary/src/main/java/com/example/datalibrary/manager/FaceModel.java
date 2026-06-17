package com.example.datalibrary.manager;

import android.content.Context;

import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDarkEnhance;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceGaze;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.FaceSafetyHat;
import com.baidu.idl.main.facesdk.FaceSearch;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.factory.specific.CropBuilder;
import com.example.datalibrary.factory.specific.DarkBuilder;
import com.example.datalibrary.factory.specific.DetectBuilder;
import com.example.datalibrary.factory.specific.DetectNirBuilder;
import com.example.datalibrary.factory.specific.DetectQualityBuilder;
import com.example.datalibrary.factory.specific.FaceGazeBuilder;
import com.example.datalibrary.factory.specific.FaceSafetyHatBuilder;
import com.example.datalibrary.factory.specific.FeatureBuilder;
import com.example.datalibrary.factory.specific.LiveBuilder;
import com.example.datalibrary.factory.specific.MouthMaskBuilder;
import com.example.datalibrary.factory.specific.TrackBuilder;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.BDQualityConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.LogUtils;

import java.util.List;

public class FaceModel implements SdkInitListener {
    private final TrackBuilder trackBuilder;
    private final DetectBuilder detectBuilder;
    private final DetectQualityBuilder detectQualityBuilder;
    private final DetectNirBuilder detectNirBuilder;
    private final DarkBuilder darkBuilder;
    private final FeatureBuilder featureBuilder;
    private final FeatureBuilder featurePersonBuilder;
    private final LiveBuilder liveBuilder;
    private final CropBuilder cropBuilder;
    private final MouthMaskBuilder mouthMaskBuilder;
    private final FaceSafetyHatBuilder faceSafetyHatBuilder;
    private final FaceGazeBuilder faceGazeBuilder;
    //    private FaceComposite faceComposite;
    private boolean isModelInit;
    private int modeCode = 0;
    private String modeMsg = "";
    public static final String TAG = "FaceModel";


    public void setListener(SdkInitListener listener) {
        this.listener = listener;
    }

    private SdkInitListener listener;

    public FaceModel() {
        isModelInit = false;
        cropBuilder = new CropBuilder(this);
        trackBuilder = new TrackBuilder(this);
        detectBuilder = new DetectBuilder(this);
        detectQualityBuilder = new DetectQualityBuilder(this);
        detectNirBuilder = new DetectNirBuilder(this);
        darkBuilder = new DarkBuilder(this);
        liveBuilder = new LiveBuilder(this);
        featureBuilder = new FeatureBuilder(this);
        featurePersonBuilder = new FeatureBuilder(this);
        faceGazeBuilder = new FaceGazeBuilder(this);
        mouthMaskBuilder = new MouthMaskBuilder(this);
        faceSafetyHatBuilder = new FaceSafetyHatBuilder(this);
    }

    public void init(BDFaceSDKConfig config, Context context) {
        LogUtils.d(TAG, "isModelInit = " + isModelInit);
        if (isModelInit) {
            return;
        }
        BDFaceInstance trackInstance = new BDFaceInstance();
        trackInstance.creatInstance();
        BDFaceInstance detectInstance = new BDFaceInstance();
        detectInstance.creatInstance();
        // 人证核验init入参
        BDFaceInstance detectQualityInstance = new BDFaceInstance();
        detectQualityInstance.creatInstance();
        BDFaceInstance cropInstance = new BDFaceInstance();
        cropInstance.creatInstance();

//        faceComposite = new FaceComposite(detectInstance);
        trackBuilder.init(trackInstance, config);
        cropBuilder.init(cropInstance);
        detectBuilder.init(detectInstance, config);
        detectNirBuilder.init(detectInstance);
        // 认证核验检测模型预配置
        detectQualityBuilder.init(detectQualityInstance, config);
        darkBuilder.init(null);
        liveBuilder.init(null);
        // 认证核验提取特征模型预配置
        featurePersonBuilder.init(detectQualityInstance);
        // 通用特征提取模型预配置
        featureBuilder.init(null);

        mouthMaskBuilder.init(null);
        faceSafetyHatBuilder.init(null);
        faceGazeBuilder.init(null);
        faceGazeBuilder.initModel(context);
        // 口罩模型初始化
        mouthMaskBuilder.initModel(context);
        // 安全帽模型初始化
        faceSafetyHatBuilder.initModel(context);
        // 抠图模型初始化
        cropBuilder.initModel(context);
        // 跟踪模型初始化
        trackBuilder.initModel(context);
        // 检测模型初始化
        detectBuilder.initModel(context);
        // 认证核验检测模型初始化 （边导入边识别-需初始化此处）
        detectQualityBuilder.initModel(context);
        // 红外检测模型
        detectNirBuilder.initModel(context);
        // 暗光恢复模型
        darkBuilder.initModel(context);
        // 活体分数模型
        liveBuilder.initModel(context);
        // 认证特征提取模型 （边导入边识别-需初始化此处）
        featurePersonBuilder.initModel(context);
        // 通用特征提取模型
        featureBuilder.initModel(context);
//        faceComposite.initModel(context, GlobalSet.COMPOSITE_GRAPH, new Callback() {
//            @Override
//            public void onResponse(int i, String s) {
//                if (i != 0) {
//                    initModelFail(i, s);
//                } else {
//                    initLicenseSuccess();
//                }
//            }
//        });
    }

    @Override
    public void initStart() {
        listener.initStart();
    }

    @Override
    public void initLicenseSuccess() {

        listener.initLicenseSuccess();
    }

    @Override
    public void initLicenseFail(int errorCode, String msg) {
        listener.initLicenseFail(errorCode, msg);
    }

    @Override
    public void initModelSuccess() {
        if (modeCode == 0) {
            isModelInit = true;
            listener.initModelSuccess();
        }
    }

    @Override
    public void initModelFail(int errorCode, String msg) {
        modeCode += errorCode;
        modeMsg += msg + "\n";
        isModelInit = false;
        listener.initModelFail(modeCode, modeMsg);
    }

    public FaceFeature getFacePersonFeature() {
        return featurePersonBuilder.getExample();
    }

    public FaceSearch getFacePersonSearch() {
        return featurePersonBuilder.getFaceSearch();
    }

    public FaceFeature getFaceFeature() {
        return featureBuilder.getExample();
    }

    public FaceSearch getFaceSearch() {
        return featureBuilder.getFaceSearch();
    }

    public FaceDetect getFaceTrack() {
        return trackBuilder.getExample();
    }

    public FaceCrop getFaceCrop() {
        return cropBuilder.getExample();
    }

    public FaceDetect getFaceDetectPerson() {
        return detectQualityBuilder.getExample();
    }

    public FaceDetect getFaceDetect() {
        return detectBuilder.getExample();
    }

    public FaceDetect getFaceNirDetect() {
        return detectNirBuilder.getExample();
    }

    public FaceDarkEnhance getDark() {
        return darkBuilder.getExample();
    }

    public FaceLive getFaceLive() {
        return liveBuilder.getExample();
    }

    public FaceMouthMask getFaceMoutMask() {
        return mouthMaskBuilder.getExample();
    }

    public FaceSafetyHat getFaceSafetyHat() {
        return faceSafetyHatBuilder.getExample();
    }

    public FaceGaze getFaceGaze() {
        return faceGazeBuilder.getExample();
    }

//    public FaceComposite getFaceComposite() {
//        return faceComposite;
//    }


    /**
     * 快速检测人脸
     */
    public FaceInfo[] onTrack(BDFaceImageInstance rgbInstance, List<LivenessModel> models, boolean isMultiIdentify) {
        LogUtils.d(TAG, "onTrack isMultiIdentify = " + isMultiIdentify);
        long startDetectTime = System.currentTimeMillis();
        // track
        LogUtils.d(TAG, "getTrackCheck");
        FaceInfo[] faceInfos = getTrackCheck(rgbInstance);
        // 检测结果判断
        if (faceInfos == null || faceInfos.length == 0) {
            LogUtils.d(TAG, "faceInfos is null");
            return null;
        }
        long trackTime = System.currentTimeMillis() - startDetectTime;
        if (!isMultiIdentify) {
            LivenessModel model = new LivenessModel();
            setTrackFaceInfo(faceInfos[0], model, rgbInstance.getImage(), trackTime);
            models.add(model);
        } else {
            for (FaceInfo f : faceInfos) {
                LivenessModel model = new LivenessModel();
                setTrackFaceInfo(f, model, rgbInstance.getImage(), trackTime);
                models.add(model);
            }
        }
        LogUtils.d(TAG, "onTrack close ");
        return faceInfos;
    }

    private void setTrackFaceInfo(FaceInfo faceInfo, LivenessModel model, BDFaceImageInstance rgbInstance, long time) {
        LogUtils.d(TAG, "setTrackFaceInfo time = " + time);
        model.setRgbDetectDuration(time);
        // 保存人脸特征点
        model.setLandmarks(faceInfo.landmarks);
        model.setFaceInfo(faceInfo);
        // 保存人脸图片
        model.setBdFaceImageInstance(rgbInstance);
        LogUtils.d(TAG, "setTrackFaceInfo close ");
    }

    public FaceInfo[] getTrackCheck(BDFaceImageInstance rgbInstance) {
        LogUtils.d(TAG, "getTrackCheck");
        // 快速检测获取人脸信息，仅用于绘制人脸框，详细人脸数据后续获取
        FaceInfo[] faceInfos = getFaceTrack().track(
                BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST,
                rgbInstance);
        LogUtils.d(TAG, "getTrackCheck close ");
        return faceInfos;
    }

    public BDFaceImageInstance getBdImage(BDFaceImageConfig bdFaceImageConfig, boolean darkEnhance) {
        LogUtils.d(TAG, "getBdImage darkEnhance = " + darkEnhance);
        if (bdFaceImageConfig.bitmap != null && !bdFaceImageConfig.bitmap.isRecycled()) {
            LogUtils.d(TAG, "get bitmap instance");
            BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bdFaceImageConfig.bitmap);
            return rgbInstance;
        }
        LogUtils.d(TAG, "get data instance srcWidth = " + bdFaceImageConfig.srcWidth + " srcHeight = " +
                bdFaceImageConfig.srcHeight + " data size = " + bdFaceImageConfig.data.length);
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(
                bdFaceImageConfig.data,
                bdFaceImageConfig.srcHeight,
                bdFaceImageConfig.srcWidth,
                bdFaceImageConfig.bdFaceImageType,
                bdFaceImageConfig.direction,
                bdFaceImageConfig.mirror);
        BDFaceImageInstance rgbInstanceOne;
        // 判断暗光恢复
        if (darkEnhance) {
            rgbInstanceOne = getDark().faceDarkEnhance(rgbInstance);
            rgbInstance.destory();
        } else {
            rgbInstanceOne = rgbInstance;
        }
        LogUtils.d(TAG, "getBdImage close");
        return rgbInstanceOne;
    }

    /**
     * detect 采集人脸信息
     */
    public FaceInfo[] onDetect(
            BDFaceCheckConfig bdFaceCheckConfig,
            BDFaceImageInstance rgbInstance,
            FaceInfo[] fastFaceInfos,
            LivenessModel livenessModel,
            int index) {
        LogUtils.d(TAG, "onDetect");
        long accurateTime = System.currentTimeMillis();
        LogUtils.d(TAG, "onDetect detection");
        FaceInfo[] faceInfos = onDetect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, bdFaceCheckConfig.bdFaceDetectListConfig,
                rgbInstance, fastFaceInfos);
        livenessModel.setAccurateTime(System.currentTimeMillis() - accurateTime);
        if (faceInfos != null && faceInfos.length > index) {
            livenessModel.setFaceInfo(faceInfos[index]);
            // 保存人脸关键点
            livenessModel.setLandmarks(faceInfos[index].landmarks);
        }
        LogUtils.d(TAG, "onDetect close");
        return faceInfos;
    }

    public FaceInfo[] onDetect(
            BDFaceSDKCommon.DetectType detectType,
            BDFaceSDKCommon.AlignType alignType,
            BDFaceDetectListConf bdFaceCheckConfig,
            BDFaceImageInstance rgbInstance,
            FaceInfo[] fastFaceInfos) {
        LogUtils.d(TAG, "onDetect detection bdFaceCheckConfig is " + (bdFaceCheckConfig == null));
        FaceInfo[] faceInfos;
        if (bdFaceCheckConfig != null) {
            faceInfos = this.getFaceDetect().detect(
                    detectType, alignType, rgbInstance, fastFaceInfos, bdFaceCheckConfig);
        } else {
            faceInfos = this.getFaceDetect().detect(detectType, rgbInstance);
        }
        LogUtils.d(TAG, "onDetect detection close ");
        return faceInfos;
    }

    /**
     * 活体检测
     *
     * @param rgbInstance
     * @param type
     * @param faceInfo
     * @param liveScore
     * @return
     */
    public float silentLives(
            BDFaceImageInstance rgbInstance,
            BDFaceSDKCommon.LiveType type,
            FaceInfo faceInfo,
            float liveScore) {
        LogUtils.d(TAG, "silentLives type :  " + type + " liveScore : " + liveScore);
        float scores = 0;
        if (faceInfo != null) {
            synchronized (this.getFaceLive()) {
                scores = this.getFaceLive().silentLive(type, rgbInstance, faceInfo.landmarks, liveScore);
            }
        }
        LogUtils.d(TAG, "silentLives close");
//        while (list.size() > 6) {
//            list.remove(0);
//        }
//        if (list.size() > 2) {
//            int rgbSum = 0;
//            for (Boolean b : list) {
//                if (b) {
//                    rgbSum++;
//                }
//            }
//            if (1.0 * rgbSum / list.size() > 0.6) {
//                if (score < liveScore) {
//                    score = liveScore + (1 - liveScore) * new Random().nextFloat();
//                }
//            } else {
//                if (score > liveScore) {
//                    score = new Random().nextFloat() * liveScore;
//                }
//            }
//        }
        return scores;
    }

    public void setNeedJoinDB(float blur, BDFaceOcclusion occlusion) {
        LogUtils.d(TAG, "setNeedJoinDB");
        float leftEye = occlusion.leftEye;
        // "左眼遮挡"
        float rightEye = occlusion.rightEye;
        // "右眼遮挡"
        float nose = occlusion.nose;
        // "鼻子遮挡置信度"
        float mouth = occlusion.mouth;
        // "嘴巴遮挡置信度"
        float leftCheek = occlusion.leftCheek;
        // "左脸遮挡"
        float rightCheek = occlusion.rightCheek;
        // "右脸遮挡"
        float chin = occlusion.chin;
        LogUtils.d(TAG, "setNeedJoinDB leftEye = " + leftEye + " rightEye = " + rightEye + " nose = " + nose + " mouth = " + mouth +
        " leftCheek = " + leftCheek + " rightCheek = " + rightCheek);
        // 动态底库限制
        this.getFaceSearch().setNeedJoinDB(selectQuality(
                blur, leftEye, rightEye, nose, mouth,
                leftCheek, rightCheek, chin));
        LogUtils.d(TAG, "setNeedJoinDB close");
    }

    /**
     * 检测-活体-特征- 全流程
     * bluriness 模糊得分
     * leftEye  左眼遮擋得分
     * rightEye 右眼遮擋得分
     * nose     鼻子遮擋得分
     * mouth    嘴巴遮擋得分
     * leftCheek 左臉眼遮擋得分
     * rightCheek 右臉遮擋得分
     */
    private boolean selectQuality(float bluriness, float leftEye, float rightEye,
                                  float nose, float mouth, float leftCheek, float rightCheek, float chin) {

        return bluriness < 0.5 && leftEye < 0.75 && rightEye < 0.75 && nose < 0.75
                && mouth < 0.75 && leftCheek < 0.75 && rightCheek < 0.75 && chin < 0.7;
    }

    /**
     * 质量检测结果过滤，如果需要质量检测，
     * 需要调用 SingleBaseConfig.getBaseConfig().setQualityControl(true);设置为true，
     * 再调用  FaceSDKManager.getInstance().initConfig() 加载到底层配置项中
     *
     * @param faceInfo        人脸信息
     * @param bdQualityConfig 质量参数
     * @param checkMouthMask  是为口罩识别
     * @return
     */
    public boolean onQualityCheck(
            final FaceInfo faceInfo,
            final BDQualityConfig bdQualityConfig,
            final boolean checkMouthMask,
            final FaceDetectCallBack faceDetectCallBack
    ) {
        LogUtils.d(TAG, "onQualityCheck checkMouthMask = " + checkMouthMask);
        if (bdQualityConfig == null) {
            LogUtils.d(TAG, "bdQualityConfig is null");
            return true;
        }
        boolean qualityCheck = false;
        if (faceInfo == null) {
            LogUtils.d(TAG, "faceInfo is null");
            return true;
        }
        boolean checkItem = true;
        // 角度过滤
        if (Math.abs(faceInfo.yaw) > bdQualityConfig.gesture) {
            LogUtils.d(TAG, "人脸左右偏转角超出限制 faceInfo.yaw = " + faceInfo.yaw + " bdQualityConfig.gesture = " + bdQualityConfig.gesture);
            faceDetectCallBack.onTip(-1, "人脸左右偏转角超出限制");
            checkItem = false;
        } else if (Math.abs(faceInfo.roll) > bdQualityConfig.gesture) {
            LogUtils.d(TAG, "人脸平行平面内的头部旋转角超出限制 faceInfo.roll = " + faceInfo.roll + " bdQualityConfig.gesture = " + bdQualityConfig.gesture);
            faceDetectCallBack.onTip(-1, "人脸平行平面内的头部旋转角超出限制");
            checkItem = false;
        } else if (Math.abs(faceInfo.pitch) > bdQualityConfig.gesture) {
            LogUtils.d(TAG, "人脸上下偏转角超出限制 faceInfo.pitch = " + faceInfo.pitch + " bdQualityConfig.gesture = " + bdQualityConfig.gesture);
            faceDetectCallBack.onTip(-1, "人脸上下偏转角超出限制");
            checkItem = false;
        }

        // 模糊结果过滤
        float blur = faceInfo.bluriness;
        if (blur > bdQualityConfig.blur) {
            LogUtils.d(TAG, "图片模糊 faceInfo.bluriness = " + faceInfo.bluriness + " bdQualityConfig.blur = " + bdQualityConfig.blur);
            faceDetectCallBack.onTip(-1, "图片模糊");
            checkItem = false;
        }

        // 光照结果过滤
        float illum = faceInfo.illum;
        if (illum < bdQualityConfig.illum) {
            LogUtils.d(TAG, "图片光照不通过 faceInfo.illum = " + faceInfo.illum + " bdQualityConfig.illum = " + bdQualityConfig.illum);
            faceDetectCallBack.onTip(-1, "图片光照不通过");
            checkItem = false;
        }

        // 口罩识别不进行质量判断
        if (checkMouthMask) {
            LogUtils.d(TAG, "checkMouthMask is true");
            return true;
        }
        // 遮挡结果过滤
        if (faceInfo.occlusion == null) {
            LogUtils.d(TAG, "faceInfo.occlusion == null");
            return true;
        }
        BDFaceOcclusion occlusion = faceInfo.occlusion;
        if (occlusion.leftEye > bdQualityConfig.leftEye) {
            LogUtils.d(TAG, "左眼遮挡 occlusion.leftEye = " + occlusion.leftEye + " bdQualityConfig.leftEye = " + bdQualityConfig.leftEye);
            // 左眼遮挡置信度
            faceDetectCallBack.onTip(-1, "左眼遮挡");
            checkItem = false;
        } else if (occlusion.rightEye > bdQualityConfig.rightEye) {
            LogUtils.d(TAG, "右眼遮挡 occlusion.rightEye = " + occlusion.rightEye + " bdQualityConfig.rightEye = " + bdQualityConfig.rightEye);
            // 右眼遮挡置信度
            faceDetectCallBack.onTip(-1, "右眼遮挡");
            checkItem = false;
        } else if (occlusion.nose > bdQualityConfig.nose) {
            LogUtils.d(TAG, "鼻子遮挡 occlusion.nose = " + occlusion.nose + " bdQualityConfig.nose = " + bdQualityConfig.nose);
            // 鼻子遮挡置信度
            faceDetectCallBack.onTip(-1, "鼻子遮挡");
            checkItem = false;
        } else if (occlusion.mouth > bdQualityConfig.mouth) {
            LogUtils.d(TAG, "嘴巴遮挡 occlusion.mouth = " + occlusion.mouth + " bdQualityConfig.mouth = " + bdQualityConfig.mouth);
            // 嘴巴遮挡置信度
            faceDetectCallBack.onTip(-1, "嘴巴遮挡");
            checkItem = false;
        } else if (occlusion.leftCheek > bdQualityConfig.leftCheek) {
            LogUtils.d(TAG, "左脸遮挡 occlusion.leftCheek = " + occlusion.leftCheek + " bdQualityConfig.leftCheek = " + bdQualityConfig.leftCheek);
            // 左脸遮挡置信度
            faceDetectCallBack.onTip(-1, "左脸遮挡");
            checkItem = false;
        } else if (occlusion.rightCheek > bdQualityConfig.rightCheek) {
            LogUtils.d(TAG, "右脸遮挡 occlusion.rightCheek = " + occlusion.rightCheek + " bdQualityConfig.rightCheek = " + bdQualityConfig.rightCheek);
            // 右脸遮挡置信度
            faceDetectCallBack.onTip(-1, "右脸遮挡");
            checkItem = false;
        } else if (occlusion.chin > bdQualityConfig.chinContour) {
            LogUtils.d(TAG, "下巴遮挡 occlusion.chin = " + occlusion.chin + " bdQualityConfig.chinContour = " + bdQualityConfig.chinContour);
            // 下巴遮挡置信度
            faceDetectCallBack.onTip(-1, "下巴遮挡");
        }
        if (checkItem) {
            qualityCheck = true;
        }
        LogUtils.d(TAG, "onQualityCheck close");
        return qualityCheck;
    }

    /**
     * 最优人脸控制
     *
     * @param livenessModel
     * @return
     */
    public boolean onBestImageCheck(
            LivenessModel livenessModel) {
        LogUtils.d(TAG, "onBestImageCheck");
        boolean isBestImageCheck = false;
        FaceInfo faceInfo = livenessModel.getFaceInfo();
        if (faceInfo == null) {
            LogUtils.d(TAG, "faceInfo is null ");
            return isBestImageCheck;
        }
        float bestImageScore = faceInfo.bestImageScore;
        LogUtils.d(TAG, "bestImageScore : " + bestImageScore);
        if (bestImageScore > 0.5) {
            isBestImageCheck = true;
        }
        LogUtils.d(TAG, "onBestImageCheck close");
        return isBestImageCheck;
    }
}
