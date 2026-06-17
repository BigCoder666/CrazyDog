package com.example.datalibrary.manager;

import android.graphics.Bitmap;

import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.example.datalibrary.model.BDLiveConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.BitmapUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SaveImageManager {
    private static class HolderClass {
        private static final SaveImageManager SAVE_IMAGE_MANAGER = new SaveImageManager();
    }

    public static SaveImageManager getInstance() {
        return SaveImageManager.HolderClass.SAVE_IMAGE_MANAGER;
    }

    private ExecutorService es3 = Executors.newSingleThreadExecutor();
    private Future future3;
    public void saveImage(final List<LivenessModel> models , final BDLiveConfig bdLiveConfig){
        if (models == null || models.size() == 0){
            return;
        }
        // 检测结果输出
        if (future3 != null && !future3.isDone()) {
            return;
        }

        future3 = es3.submit(new Runnable() {
            @Override
            public void run() {
                LivenessModel livenessModel = models.get(0);
                String currentTime = System.currentTimeMillis() + "";
                BDFaceImageInstance rgbImage = livenessModel.getBdFaceImageInstance();
                BDFaceImageInstance nirImage = livenessModel.getBdNirFaceImageInstance();
                BDFaceImageInstance depthImage = livenessModel.getBdDepthFaceImageInstance();
                if (rgbImage != null){
                    Bitmap bitmap = BitmapUtils.getInstaceBmp(rgbImage);
                    if (livenessModel.getRgbLivenessScore() > bdLiveConfig.rgbLiveScore){
                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_RGB_Feature");
                    }else {

                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_RGB_Live");
                    }
                    if (!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                    rgbImage.destory();
                }
                if (nirImage != null){
                    Bitmap bitmap = BitmapUtils.getInstaceBmp(nirImage);
                    if (livenessModel.getIrLivenessScore() > bdLiveConfig.nirLiveScore){
                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_NIR_Feature");
                    }else {
                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_NIR_Live");
                    }
                    if (!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                    nirImage.destory();
                }
                if (depthImage != null){
                    Bitmap bitmap = BitmapUtils.getInstaceBmp(depthImage);
                    if (livenessModel.getDepthLivenessScore() > bdLiveConfig.depthLiveScore){
                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_Depth_Feature");
                    }else {
                        saveImage(bitmap, "Save-Image" + "/" + currentTime,
                                currentTime + "_Depth_Live");
                    }
                    if (!bitmap.isRecycled()){
                        bitmap.recycle();
                    }
                    depthImage.destory();
                }

            }
        });
    }
    private void saveImage(Bitmap bitmap, String url, String name) {
        BitmapUtils.saveRgbBitmap(bitmap, url, name);
    }
}
