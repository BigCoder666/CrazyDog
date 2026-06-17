package com.example.datalibrary.utils;

import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDLiveConfig;
import com.example.datalibrary.model.BDQualityConfig;
import com.example.datalibrary.threshold.SingleBaseConfig;

public class FaceUtils {
    private static class HolderClass {
        private static final FaceUtils INSTANCE = new FaceUtils();
    }

    public static FaceUtils getInstance() {
        return HolderClass.INSTANCE;
    }
    public BDFaceSDKConfig getBDFaceSDKConfig(){
        BDFaceSDKConfig config = new BDFaceSDKConfig();
        // TODO: 最小人脸个数检查，默认设置为1,用户根据自己需求调整
        config.maxDetectNum = 2;
        config.trackInterval = Integer.MAX_VALUE;


        // TODO: 默认为80px。可传入大于30px的数值，小于此大小的人脸不予检测，生效时间第一次加载模型
        config.minFaceSize = SingleBaseConfig.getBaseConfig().getMinimumFace();

        // TODO: 默认为0.5。可传入大于0.3的数值
        config.notRGBFaceThreshold = SingleBaseConfig.getBaseConfig().getFaceThreshold();
        config.notNIRFaceThreshold = SingleBaseConfig.getBaseConfig().getFaceThreshold();

        // 是否进行属性检测，默认关闭
        config.isAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
//
//            // TODO: 模糊，遮挡，光照三个质量检测和姿态角查默认关闭，如果要开启，设置页启动
        config.isCheckBlur = config.isOcclusion
                = config.isIllumination = config.isHeadPose
                = SingleBaseConfig.getBaseConfig().isQualityControl();
        return config;
    }
    public BDFaceCheckConfig getBDFaceCheckConfig(){
        BDFaceDetectListConf bdFaceDetectListConfig = getBDFaceDetectListConf();
        BDQualityConfig bdQualityConfig = getBDQualityConfig();
        BDLiveConfig bdLiveConfig = getBDLiveConfig();
        float threholdScore = 0;
        // 判断第一个阈值是否大于设定阈值，如果大于，检索成功
        if (SingleBaseConfig.getBaseConfig().getActiveModel() == 1) {
            threholdScore = SingleBaseConfig.getBaseConfig().getLiveThreshold();
        } else if (SingleBaseConfig.getBaseConfig().getActiveModel() == 2) {
            threholdScore = SingleBaseConfig.getBaseConfig().getIdThreshold();
        } else if (SingleBaseConfig.getBaseConfig().getActiveModel() == 3) {
            threholdScore = SingleBaseConfig.getBaseConfig().getRgbAndNirThreshold();
        }
        return new BDFaceCheckConfig(3 , SingleBaseConfig.getBaseConfig().isDarkEnhance() , threholdScore ,
                        SingleBaseConfig.getBaseConfig().isBestImage() ,
                SingleBaseConfig.getBaseConfig().getCameraType() ,
                        SingleBaseConfig.getBaseConfig().getActiveModel() , 
                bdFaceDetectListConfig , bdQualityConfig , bdLiveConfig
                );
    }

    public BDLiveConfig getBDLiveConfig(){
        return SingleBaseConfig.getBaseConfig().isLivingControl() ?
                new BDLiveConfig(SingleBaseConfig.getBaseConfig().getRgbLiveScore() ,
                SingleBaseConfig.getBaseConfig().getNirLiveScore() ,
                SingleBaseConfig.getBaseConfig().getDepthLiveScore()) : null;
    }


    private BDQualityConfig getBDQualityConfig(){
        return !SingleBaseConfig.getBaseConfig().isQualityControl() ?
                null : new BDQualityConfig(SingleBaseConfig.getBaseConfig().getBlur() ,
                SingleBaseConfig.getBaseConfig().getIllumination() ,
                SingleBaseConfig.getBaseConfig().getGesture() , SingleBaseConfig.getBaseConfig().getLeftEye() ,
                SingleBaseConfig.getBaseConfig().getRightEye() , SingleBaseConfig.getBaseConfig().getNose() ,
                SingleBaseConfig.getBaseConfig().getMouth() , SingleBaseConfig.getBaseConfig().getLeftCheek() ,
                SingleBaseConfig.getBaseConfig().getRightCheek() , SingleBaseConfig.getBaseConfig().getChinContour());
    }




    private BDFaceDetectListConf getBDFaceDetectListConf(){
       BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
        bdFaceDetectListConfig.usingQuality = bdFaceDetectListConfig.usingHeadPose
                = SingleBaseConfig.getBaseConfig().isQualityControl();
        bdFaceDetectListConfig.usingBestImage = SingleBaseConfig.getBaseConfig().isBestImage();
        return bdFaceDetectListConfig;
    }
}
