package com.example.datalibrary.manager;

import static com.example.datalibrary.model.GlobalSet.FEATURE_SIZE;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.FaceSearch;
import com.baidu.idl.main.facesdk.ImageIllum;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.baidu.idl.main.facesdk.model.Feature;
import com.example.datalibrary.R;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.callback.FaceQualityBack;
import com.example.datalibrary.db.DBManager;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.BDLiveConfig;
import com.example.datalibrary.model.BDQualityConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.LogUtils;
import com.example.datalibrary.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class FaceSDKManager {
    public static final int SDK_MODEL_LOAD_SUCCESS = 0;
    public static final int SDK_UNACTIVATION = 1;
    private static final String TAG = "FaceSDKManager";
    public static volatile int initStatus = SDK_UNACTIVATION;
    public static volatile boolean initModelSuccess = false;
    private final FaceAuth faceAuth;
    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;
    private final ExecutorService es2 = Executors.newSingleThreadExecutor();
    private Future future2;
    private final ExecutorService es3 = Executors.newSingleThreadExecutor();
    private Future future3;
    private ImageIllum imageIllum;
    private FaceModel faceModel;
    private boolean checkMouthMask = false;
    private boolean isMultiIdentify = true;

    private FaceSDKManager() {
        faceAuth = new FaceAuth();
        faceAuth.setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode.BDFACE_LITE_POWER_NO_BIND, 2);
    }

    public void setActiveLog(boolean isLog) {
        LogUtils.isDebug = isLog;
        LogUtils.d(TAG, "setActiveLog isLog = " + isLog);
        if (faceAuth != null) {
            if (isLog) {
                faceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 1);
            } else {
                faceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 0);
            }
        }
    }

    public FaceModel getFaceModel() {
        return faceModel;
    }

    public void setCheckMouthMask(boolean checkMouthMask) {
        this.checkMouthMask = checkMouthMask;
    }

    public void setMultiIdentify(boolean isMultiFaceIdentify) {
        this.isMultiIdentify = isMultiFaceIdentify;
    }

    private static class HolderClass {
        private static final FaceSDKManager INSTANCE = new FaceSDKManager();
    }

    public static FaceSDKManager getInstance() {
        return HolderClass.INSTANCE;
    }

    public ImageIllum getImageIllum() {
        return imageIllum;
    }

    public void initModel(
            final Context context, BDFaceSDKConfig config, boolean isLog, final SdkInitListener listener) {
        LogUtils.d(TAG, "initModel");
        setActiveLog(isLog);
        initModel(context, config, listener);
    }

    /**
     * 初始化模型，目前包含检查，活体，识别模型；因为初始化是顺序执行，可以在最好初始化回掉中返回状态结果
     *
     * @param context
     */
    public void initModel(final Context context, BDFaceSDKConfig config, final SdkInitListener listener) {
        LogUtils.d(TAG, "initModel");
        // 曝光
        if (imageIllum == null) {
            imageIllum = new ImageIllum();
        }
        // 其他模型初始化
        if (faceModel == null) {
            faceModel = new FaceModel();
        }
        faceModel.setListener(listener);
        faceModel.init(config, context);
    }

    public FaceCrop getFaceCrop() {
        return faceModel.getFaceCrop();
    }

    public FaceDetect getFaceDetectPerson() {
        return faceModel.getFaceDetectPerson();
    }

    public FaceSearch getFaceSearch() {
        return faceModel.getFaceSearch();
    }

    public FaceFeature getFacePersonFeature() {
        return faceModel.getFacePersonFeature();
    }

    public FaceMouthMask getFaceMouthMask() {
        return faceModel.getFaceMoutMask();
    }

    public void initDataBases(Context context) {
        LogUtils.d(TAG, "initDataBases");
        if (FaceApi.getInstance().getmUserNum() != 0) {
            ToastUtils.toast(context, context.getResources().getString(R.string.toast_loading_face_database));
        }
        // 初始化数据库
        DBManager.getInstance().init(context);
        // 数据变化，更新内存
        initPush();
    }

    /**
     * 数据库发现变化时候，重新把数据库中的人脸信息添加到内存中，id+feature
     */
    public void initPush() {
        if (future3 != null && !future3.isDone()) {
            LogUtils.d(TAG, "future3 is not Done");
            return;
        }
        future3 = es3.submit(new Runnable() {
            @Override
            public void run() {
                faceModel.getFaceSearch().featureClear();
                LogUtils.d(TAG, "featureClear");
                synchronized (faceModel.getFaceSearch()) {
                    List<User> users = FaceApi.getInstance().getAllUserList();
                    if (users == null) {
                        LogUtils.d(TAG, "users is null");
                        return;
                    }
                    LogUtils.d(TAG, "users size = " + users.size());
                    for (int i = 0; i < users.size(); i++) {
                        User user = users.get(i);
                        LogUtils.d(TAG, "user id = " + user.getId() + " name = " + user.getUserName());
                        faceModel.getFaceSearch().pushPersonById(user.getId(), user.getFeature());
                    }
                }
            }
        });
    }

    public BDFaceImageInstance getCopeFace(Bitmap bitmap, float[] landmarks, int initialValue) {
        LogUtils.d(TAG, "getCopeFace");
        if (faceModel == null || faceModel.getFaceCrop() == null) {
            LogUtils.d(TAG, "getCopeFace faceModel is null");
            return null;
        }
        BDFaceImageInstance imageInstance = new BDFaceImageInstance(bitmap);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        AtomicInteger isOutoBoundary = new AtomicInteger();
        BDFaceImageInstance cropInstance =
                faceModel.getFaceCrop().cropFaceByLandmark(imageInstance, landmarks, 2.0f, false, isOutoBoundary);
        LogUtils.d(TAG, "getCopeFace faceModel is complete");
        return cropInstance;
    }

    /**
     * 人脸跟踪接口，并对人脸信息进行 检测-活体-特征-人脸检索流程
     *
     * @param bdFaceImageConfig      可见光YUV 数据流
     * @param bdNirFaceImageConfig   红外YUV 数据流
     * @param bdDepthFaceImageConfig 深度depth 数据流
     * @param bdFaceCheckConfig      识别参数
     * @param faceDetectCallBack
     */
    public void onDetectCheck(
            final BDFaceImageConfig bdFaceImageConfig,
            final BDFaceImageConfig bdNirFaceImageConfig,
            final BDFaceImageConfig bdDepthFaceImageConfig,
            final BDFaceCheckConfig bdFaceCheckConfig,
            final FaceDetectCallBack faceDetectCallBack) {
        LogUtils.d(TAG, "onDetectCheck");
        if (!FaceSDKManager.initModelSuccess) {
            LogUtils.d(TAG, "initModelSuccess is false");
            return;
        }
        if (future != null && !future.isDone()) {
            LogUtils.d(TAG, "future is not Done");
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            return;
        }
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                LogUtils.d(TAG, "es.submit : " + startTime);
                // 创建检测结果存储数据
                List<LivenessModel> models = new ArrayList<>();
                // 创建检测对象，如果原始数据YUV，转为算法检测的图片BGR
                // TODO: 用户调整旋转角度和是否镜像，手机和开发版需要动态适配
                LogUtils.d(TAG, "faceModel.getBdImage");
                BDFaceImageInstance rgbInstance = faceModel.getBdImage(bdFaceImageConfig, bdFaceCheckConfig.darkEnhance);
                LogUtils.d(TAG, "faceModel.onTrack");
                FaceInfo[] faceInfos = faceModel.onTrack(rgbInstance, models, isMultiIdentify);
                if (faceInfos == null || faceInfos.length == 0) {
                    rgbInstance.destory();
                    LogUtils.d(TAG, "faceInfos is null ");
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(null);
                        faceDetectCallBack.onFaceDetectDarwCallback(models);
                        faceDetectCallBack.onTip(0, "未检测到人脸");
                    }
                    return;
                }
                // 调用绘制人脸框接口
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectDarwCallback(models);
                }
                // 送检识别
                LogUtils.d(TAG, "onDetectCheck close , enter onLiveCheck trackFaceInfo");
                onLiveCheck(rgbInstance, bdNirFaceImageConfig, bdDepthFaceImageConfig, bdFaceCheckConfig, models,
                        startTime, faceInfos, faceDetectCallBack);

            }
        });
    }

    public LivenessModel getLivenessModel(FaceInfo faceInfos, BDFaceImageInstance rgbInstance) {
        LivenessModel model = new LivenessModel();
        model.setFaceInfo(faceInfos);
        model.setBdFaceImageInstance(rgbInstance);
        return model;
    }
    /**
     * 不进行人脸跟踪 , 质量-活体-特征-人脸检索全流程
     *
     * @param bdFaceImageConfig      可见光YUV 数据流
     * @param bdNirFaceImageConfig   红外YUV 数据流
     * @param bdDepthFaceImageConfig 深度depth 数据流
     * @param bdFaceCheckConfig      阈值参数
     * @param faceDetectCallBack
     */
    public void onLiveCheck(
            final BDFaceImageConfig bdFaceImageConfig,
            final BDFaceImageConfig bdNirFaceImageConfig,
            final BDFaceImageConfig bdDepthFaceImageConfig,
            final BDFaceCheckConfig bdFaceCheckConfig,
            final FaceDetectCallBack faceDetectCallBack) {
        LogUtils.d(TAG, "onLiveCheck single");
        if (future2 != null && !future2.isDone()) {
            LogUtils.d(TAG, "future2 is not Done");
            return;
        }
        if (bdFaceCheckConfig == null) {
            LogUtils.d(TAG, "bdFaceCheckConfig is null");
            return;
        }
        if (!FaceSDKManager.initModelSuccess) {
            LogUtils.d(TAG, "initModelSuccess is false");
            return;
        }
        future2 = es2.submit(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                LogUtils.d(TAG, "es2.submit : " + startTime);
                BDFaceImageInstance rgbInstance = faceModel.getBdImage(bdFaceImageConfig, bdFaceCheckConfig.darkEnhance);
                List<LivenessModel> models = new ArrayList<>();
                bdFaceCheckConfig.bdFaceDetectListConfig.usingDetect = true;
                LogUtils.d(TAG, "faceModel.onDetect");
                FaceInfo[] faceInfos = faceModel.onDetect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, bdFaceCheckConfig.bdFaceDetectListConfig, rgbInstance, null);
                if (faceInfos == null) {
                    LogUtils.d(TAG, "faceInfos is null");
                    rgbInstance.destory();
                    return;
                }
                LogUtils.d(TAG, "faceInfos size = " + faceInfos.length);
                if (!isMultiIdentify) {
                    models.add(getLivenessModel(faceInfos[0], rgbInstance.getImage()));
                } else {
                    for (FaceInfo f : faceInfos) {
                        models.add(getLivenessModel(f, rgbInstance.getImage()));
                    }
                }
                for (int i = 0, k = models.size(); i < k; i++) {
                    LivenessModel model = models.get(i);
                    // 最优人脸控制
                    if (!faceModel.onBestImageCheck(model)) {
                        LogUtils.d(TAG, "BestImage Not passed");
                        model.setQualityCheck(true);
                        continue;
                    }
                    // 质量检测未通过,销毁BDFaceImageInstance，结束函数
                    if (!faceModel.onQualityCheck(model.getFaceInfo(), bdFaceCheckConfig.bdQualityConfig, checkMouthMask, faceDetectCallBack)) {
                        LogUtils.d(TAG, "Quality Not passed");
                        model.setQualityCheck(true);
                        continue;
                    }
                    LogUtils.d(TAG, "onLiveCheck single close , enter onLiveCheck");
                    onLiveCheck(rgbInstance, bdNirFaceImageConfig, bdDepthFaceImageConfig, bdFaceCheckConfig, faceInfos, model, startTime);
                }
                rgbInstance.destory();
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(models);
                }
            }
        });
    }


    /**
     * 对人脸跟踪接口获取的人脸信息进行 质量-活体-特征-人脸检索全流程
     *
     * @param rgbInstance            可见光底层送检对象
     * @param nirBDFaceImageConfig   红外YUV 数据流
     * @param depthBDFaceImageConfig 深度depth 数据流
     * @param bdFaceCheckConfig      阈值参数
     * @param models                 检测结果数据集合
     * @param startTime              开始检测时间
     * @param fastFaceInfos          人脸跟踪接口获取的人脸信息
     * @param faceDetectCallBack
     */
    private void onLiveCheck(
            final BDFaceImageInstance rgbInstance,
            final BDFaceImageConfig nirBDFaceImageConfig,
            final BDFaceImageConfig depthBDFaceImageConfig,
            final BDFaceCheckConfig bdFaceCheckConfig,
            final List<LivenessModel> models,
            final long startTime,
            final FaceInfo[] fastFaceInfos,
            final FaceDetectCallBack faceDetectCallBack) {
        LogUtils.d(TAG, "onLiveCheck trackFaceInfo");
        if (future2 != null && !future2.isDone()) {
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            LogUtils.d(TAG, "future2 is not Done");
            rgbInstance.destory();
            return;
        }
        // 获取BDFaceCheckConfig配置信息
        if (bdFaceCheckConfig == null) {
            LogUtils.d(TAG, "bdFaceCheckConfig is null");
            rgbInstance.destory();
            return;
        }
        future2 = es2.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0, k = models.size(); i < k; i++) {
                    LivenessModel model = models.get(i);
                    LogUtils.d(TAG, "faceModel.onDetect");
                    FaceInfo[] faceInfos = faceModel.onDetect(bdFaceCheckConfig, rgbInstance, fastFaceInfos, model, i);
                    if (faceInfos == null) {
                        continue;
                    }
                    // 最优人脸控制
                    if (!faceModel.onBestImageCheck(model)) {
                        LogUtils.d(TAG, "BestImage Not passed");
                        model.setQualityCheck(true);
                        continue;
                    }
                    // 质量检测未通过,销毁BDFaceImageInstance，结束函数
                    if (!faceModel.onQualityCheck(faceInfos[i], bdFaceCheckConfig.bdQualityConfig, checkMouthMask, faceDetectCallBack)) {
                        LogUtils.d(TAG, "Quality Not passed");
                        model.setQualityCheck(true);
                        continue;
                    }
                    LogUtils.d(TAG, "onLiveCheck trackFaceInfo close , enter onLiveCheck");
                    onLiveCheck(rgbInstance, nirBDFaceImageConfig, depthBDFaceImageConfig, bdFaceCheckConfig, faceInfos, model, startTime);
                }
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(models);
                }
            }
        });
    }


    private void onLiveCheck(
            final BDFaceImageInstance rgbInstance,
            final BDFaceImageConfig nirBDFaceImageConfig,
            final BDFaceImageConfig depthBDFaceImageConfig,
            final BDFaceCheckConfig bdFaceCheckConfig,
            FaceInfo[] faceInfos,
            LivenessModel model,
            final long startTime) {
        LogUtils.d(TAG, "onLiveCheck");
        // 口罩检测数据
        if (checkMouthMask) {
            FaceMouthMask mouthMask = getFaceMouthMask();
            LogUtils.d(TAG, "start mouthMask mouthMask ");
            if (mouthMask != null) {
                float[] maskScores = mouthMask.checkMask(rgbInstance, faceInfos);
                if (maskScores == null || maskScores.length == 0) {
                    LogUtils.d(TAG, "maskScores is null");
                } else {
                    float maskResult = maskScores[0];
                    LogUtils.d(TAG, "mask_score:" + maskResult);
                    model.setMouthMaskArray(maskScores);
                }
            }
        }
        model.setQualityCheck(false);
        // 获取LivenessConfig liveCheckMode 配置选项：【不使用活体：0】；【RGB活体：1】；【RGB+NIR活体：2】；【RGB+Depth活体：3】；【RGB+NIR+Depth活体：4】
        // TODO 活体检测
        float rgbScore = 0;
        BDLiveConfig bdLiveConfig = bdFaceCheckConfig.bdLiveConfig;
        boolean isLiveCheck = bdFaceCheckConfig.bdLiveConfig != null;
        LogUtils.d(TAG, "rgb silentLives isLiveCheck : " + isLiveCheck);
        if (isLiveCheck) {
            long startRgbTime = System.currentTimeMillis();
            rgbScore = faceModel.silentLives(rgbInstance,
                    BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                    model.getFaceInfo(), bdLiveConfig.rgbLiveScore);
            LogUtils.d(TAG, "rgbScore : " + rgbScore);
            model.setRgbLivenessScore(rgbScore);
            model.setRgbLivenessDuration(System.currentTimeMillis() - startRgbTime);
        }

        // TODO nir活体检测
        float nirScore = -1;
        FaceInfo[] faceInfosIr = null;
        boolean isHaveNirImage = nirBDFaceImageConfig != null && isLiveCheck;
        LogUtils.d(TAG, "nir silentLives isHaveNirImage : " + isHaveNirImage);
        if (isHaveNirImage) {
            // 创建检测对象，如果原始数据YUV-IR，转为算法检测的图片BGR
            // TODO: 用户调整旋转角度和是否镜像，手机和开发版需要动态适配
            long nirInstanceTime = System.currentTimeMillis();
            BDFaceImageInstance  nirInstance = faceModel.getBdImage(nirBDFaceImageConfig, false);
            LogUtils.d(TAG, "nir cropFaceByLandmark");
            BDFaceImageInstance nirCropInstance = faceModel.getFaceCrop().
                    cropFaceByLandmark(nirInstance, model.getLandmarks(), 2.0f, false, new AtomicInteger());
            nirInstance.destory();
            model.setBdNirFaceImageInstance(nirCropInstance);
            LogUtils.d(TAG, "nir new BDFaceImageInstance");
            BDFaceImageInstance nirImageInstance = new BDFaceImageInstance(nirCropInstance.data, nirCropInstance.height, nirCropInstance.width,
                    nirCropInstance.imageType, 0, 0);
            nirCropInstance.destory();
            model.setNirInstanceTime(System.currentTimeMillis() - nirInstanceTime);
            // 避免RGB检测关键点在IR对齐活体稳定，增加红外检测
            long startIrDetectTime = System.currentTimeMillis();
            BDFaceDetectListConf bdFaceDetectListConf = new BDFaceDetectListConf();
            bdFaceDetectListConf.usingDetect = true;
            LogUtils.d(TAG, "nir faceModel.onDetect");
            faceInfosIr = faceModel.onDetect(
                    BDFaceSDKCommon.DetectType.DETECT_NIR,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE, bdFaceDetectListConf, nirImageInstance, null);
            if (faceInfosIr != null && faceInfosIr.length > 0) {
                LogUtils.d(TAG, "nir silentLives");
                FaceInfo faceInfoIr = faceInfosIr[0];
                nirScore = faceModel.silentLives(
                        nirImageInstance, BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_NIR, faceInfoIr, bdLiveConfig.nirLiveScore);
                LogUtils.d(TAG, "nir silentLives nirScore : " + nirScore);
                model.setIrLivenessScore(nirScore);
            } else {
                LogUtils.d(TAG, "faceInfosIr is null");
            }
            model.setIrLivenessDuration(System.currentTimeMillis() - startIrDetectTime);
            nirImageInstance.destory();
        }

        // TODO depth活体检测
        float depthScore = -1;
        boolean isHaveDepthImage = depthBDFaceImageConfig != null && isLiveCheck;
        LogUtils.d(TAG, "depth silentLives isHaveDepthImage : " + isHaveDepthImage);
        if (isHaveDepthImage) {
            // TODO: 用户调整旋转角度和是否镜像，适配Atlas 镜头，目前宽和高400*640，其他摄像头需要动态调整,人脸72 个关键点x 坐标向左移动80个像素点
            float[] depthLandmark = new float[faceInfos[0].landmarks.length];
            BDFaceImageInstance depthInstance;
            LogUtils.d(TAG, "bdFaceCheckConfig.cameraType == " + bdFaceCheckConfig.cameraType);
            if (bdFaceCheckConfig.cameraType == 1) {
                System.arraycopy(faceInfos[0].landmarks, 0, depthLandmark, 0, faceInfos[0].landmarks.length);
                for (int j = 0; j < 144; j = j + 2) {
                    depthLandmark[j] -= 80;
                }
            } else {
                depthLandmark = faceInfos[0].landmarks;
            }
            depthInstance = faceModel.getBdImage(depthBDFaceImageConfig, false);
            model.setBdDepthFaceImageInstance(depthInstance.getImage());
            // 创建检测对象，如果原始数据Depth
            long startDepthTime = System.currentTimeMillis();
            LogUtils.d(TAG, "depth silentLive");
            depthScore = faceModel.getFaceLive().silentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_DEPTH,
                    depthInstance, depthLandmark);
            LogUtils.d(TAG, "depth silentLive depthScore = " + depthScore);
            model.setDepthLivenessScore(depthScore);
            model.setDepthtLivenessDuration(System.currentTimeMillis() - startDepthTime);
            depthInstance.destory();
        }

        boolean isRgbScoreCheck = false;
        boolean isNirScoreCheck = false;
        boolean isDepthScoreCheck = false;
        LogUtils.d(TAG, "isLiveCheck = " + isLiveCheck + " isRgbScoreCheck = " + isRgbScoreCheck + " isNirScoreCheck = " +
                isNirScoreCheck + " isDepthScoreCheck = " + isDepthScoreCheck + " bdLiveConfig.rgbLiveScore = " + bdLiveConfig.rgbLiveScore +
                " bdLiveConfig.nirLiveScore = " + bdLiveConfig.nirLiveScore + " bdLiveConfig.depthLiveScore = " + bdLiveConfig.depthLiveScore);
        if (isLiveCheck) {
            if (rgbScore > bdLiveConfig.rgbLiveScore) {
                isRgbScoreCheck = true;
            }
            isNirScoreCheck = (!isHaveNirImage || nirScore > bdLiveConfig.nirLiveScore);
            isDepthScoreCheck = (!isHaveDepthImage || depthScore > bdLiveConfig.depthLiveScore);
        }
        // TODO 特征提取+人脸检索
        if (!isLiveCheck || (isRgbScoreCheck && isNirScoreCheck && isDepthScoreCheck)) {
            synchronized (faceModel.getFaceSearch()) {
                // 特征提取
                if (!checkMouthMask) {
                    LogUtils.d(TAG, "faceModel.setNeedJoinDB");
                    // 动态底库限制
                    faceModel.setNeedJoinDB(model.getFaceInfo().bluriness, model.getFaceInfo().occlusion);
                }
                onFeatureChecks(rgbInstance, bdFaceCheckConfig, model.getFaceInfo(),
                        model, bdFaceCheckConfig.secondFeature, bdFaceCheckConfig.featureCheckMode);
            }
        }
        // 流程结束,记录最终时间
        model.setAllDetectDuration(System.currentTimeMillis() - startTime);
        LogUtils.d(TAG, "search close");
        //                LogUtils.e(TIME_TAG, "all process time = " + livenessModel.getAllDetectDuration());

    }

    /**
     * 特征提取-人脸识别比对
     *
     * @param rgbInstance      可见光底层送检对象
     * @param rgbFaceInfo      rgb人脸数据
     * @param livenessModel    检测结果数据集合
     * @param featureCheckMode 特征抽取模式【不提取特征：1】；【提取特征：2】；【提取特征+1：N检索：3】；
     */
    private void onFeatureChecks(
            BDFaceImageInstance rgbInstance,
            BDFaceCheckConfig bdFaceCheckConfig,
            FaceInfo rgbFaceInfo,
            LivenessModel livenessModel,
            byte[] secondFeature,
            final int featureCheckMode) {
        LogUtils.d(TAG, "enter onFeatureChecks");
        // 如果不抽取特征，直接返回
        if (featureCheckMode == 1) {
            LogUtils.d(TAG, "featureCheckMode == 1");
            return;
        }
        byte[] feature = new byte[512];
        // 生活照检索
        long startFeatureTime = System.currentTimeMillis();
        if (rgbFaceInfo.landmarks == null) {
            LogUtils.d(TAG, "rgbFaceInfo.landmarks == null");
            return;
        }
        LogUtils.d(TAG, "faceModel.getFaceFeature().feature");
        float featureSize = faceModel.getFaceFeature().feature(BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO,
                rgbInstance, rgbFaceInfo.landmarks, feature);
        livenessModel.setFeatureDuration(System.currentTimeMillis() - startFeatureTime);
        livenessModel.setFeature(feature);
        LogUtils.d(TAG, "featureSize = " + featureSize);
        // 人脸检索
        featureSearchs(featureCheckMode, livenessModel, bdFaceCheckConfig, feature,
                secondFeature, featureSize, BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);
    }

    /**
     * 人脸库检索
     *
     * @param featureCheckMode 特征抽取模式【不提取特征：1】；【提取特征：2】；【提取特征+1：N检索：3】；
     * @param livenessModel    检测结果数据集合
     * @param feature          特征点
     * @param secondFeature    1:1 特征点
     * @param featureSize      特征点的size
     * @param type             特征提取类型
     */
    private void featureSearchs(
            final int featureCheckMode, LivenessModel livenessModel, BDFaceCheckConfig bdFaceCheckConfig,
            byte[] feature, byte[] secondFeature, float featureSize,
            BDFaceSDKCommon.FeatureType type) {
        LogUtils.d(TAG, "featureSearchs featureCheckMode : " + featureCheckMode);
        // 如果只提去特征，不做检索，此处返回
        if (featureCheckMode == 2) {
            LogUtils.d(TAG, "featureCheckMode == 2");
            livenessModel.setFeatureCode(featureSize);
            return;
        }
        // 如果提取特征+检索，调用search 方法
        if (featureSize != FEATURE_SIZE / 4) {
            LogUtils.d(TAG, "featureSize != " + FEATURE_SIZE / 4);
            return;
        }
        long startFeature = System.currentTimeMillis();
        // 特征提取成功
        // TODO 阈值可以根据不同模型调整
        if (featureCheckMode == 3) {
            synchronized (faceModel.getFaceSearch()) {
                LogUtils.d(TAG, "faceModel.getFaceSearch().search");
                List<? extends Feature> featureResult =
                        faceModel.getFaceSearch().search(type, bdFaceCheckConfig.scoreThreshold, 1, feature);
                // TODO 返回top num = 1 个数据集合，此处可以任意设置，会返回比对从大到小排序的num 个数据集合
                if (featureResult == null || featureResult.isEmpty()) {
                    LogUtils.d(TAG, "featureResult is null");
                    return;
                }
                // 获取第一个数据
                Feature topFeature = featureResult.get(0);
                // 判断第一个阈值是否大于设定阈值，如果大于，检索成功
                float threholdScore = bdFaceCheckConfig.scoreThreshold;
                LogUtils.d(TAG, "threholdScore = " + threholdScore);
                if (topFeature == null) {
                    LogUtils.d(TAG, "topFeature is null ");
                } else if (topFeature.getScore() <= threholdScore) {
                    LogUtils.d(TAG, "topFeature.getScore <= threholdScore , score = " + topFeature.getScore());
                } else {
                    // 当前featureEntity 只有id+feature 索引，在数据库中查到完整信息
                    LogUtils.d(TAG, "topFeature.getScore = " + topFeature.getScore());
                    User user = FaceApi.getInstance().getUserListById(topFeature.getId());
                    LogUtils.d(TAG, "user = " + user);
                    if (user != null) {
                        livenessModel.setUser(user);
                        livenessModel.setFeatureScore(topFeature.getScore());
                    }
                }
            }
        } else if (featureCheckMode == 4) {
            // 目前仅支持
            LogUtils.d(TAG, "faceModel.getFaceSearch().compare");
            float score = faceModel.getFaceSearch().compare(
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO, livenessModel.getFeature(), secondFeature, true);
            LogUtils.d(TAG, "compare score = " + score);
            livenessModel.setScore(score);
        }
        livenessModel.setCheckDuration(System.currentTimeMillis() - startFeature);
    }

    // 人证核验特征提取
    public float personDetect(
            final Bitmap bitmap, final byte[] feature, final BDFaceCheckConfig bdFaceCheckConfig, Context context) {
        LogUtils.d(TAG, "personDetect bdFaceCheckConfig is " + (bdFaceCheckConfig == null));
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        float ret = -1;
        FaceInfo[] faceInfos;
        LogUtils.d(TAG, "personDetect detect ");
        BDQualityConfig bdQualityConfig = bdFaceCheckConfig == null ? null : bdFaceCheckConfig.bdQualityConfig;
        if (bdFaceCheckConfig != null) {
            bdFaceCheckConfig.bdFaceDetectListConfig.usingDetect = true;
            faceInfos = faceModel.getFaceDetectPerson().detect(
                    BDFaceSDKCommon.DetectType.DETECT_VIS,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                    rgbInstance,
                    null,
                    bdFaceCheckConfig.bdFaceDetectListConfig);
        } else {
            faceInfos = faceModel.getFaceDetectPerson().detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        }
        if (faceInfos == null || faceInfos.length == 0) {
            LogUtils.d(TAG, "faceInfos is null ");
            rgbInstance.destory();
            return -10;
        }
        // 判断质量检测，针对模糊度、遮挡、角度
        if (!faceModel.onQualityCheck(faceInfos[0], bdQualityConfig, checkMouthMask, new FaceQualityBack(context))) {
            LogUtils.d(TAG, "Quality Not passed");
            return -11;
        }
        LogUtils.d(TAG, "personDetect feature");
        ret = faceModel.getFacePersonFeature().feature(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
        LogUtils.d(TAG, "ret = " + ret);
        rgbInstance.destory();
        return ret;
    }
}
