package me.tx.crazydog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.Feature;
import com.example.authlibrary.BdFaceAuth;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.listener.DBLoadListener;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.FaceUtils;
import com.example.datalibrary.utils.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import me.tx.crazydog.bean.CrazyDogBean;
import me.tx.crazydog.cache.CrazyDogCache;
import me.tx.crazydog.camera2.Camera2FrameCallback;
import me.tx.crazydog.camera2.CameraDeviceManager;
import me.tx.crazydog.net.LoadingObserver;
import me.tx.crazydog.net.NetDogObserver;
import me.tx.crazydog.net.NetDogSetting;
import me.tx.crazydog.task.TaskDog;
import me.tx.crazydog.ui.CrazyActivity;

public class MainActivity extends CrazyActivity {
    CameraDeviceManager cameraManager;

    ImageView img_frame;

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        addHeader(TestService.class,"key3","value3");

        findViewById(R.id.test).setOnClickListener(view -> {
            HashMap<String,String> requestParams = new HashMap<>();
            requestParams.put("username","15730538888");
            requestParams.put("password","123456");
            Disposable d = requestIO2Main(getService(TestService.class).loginAuthMain(requestParams), new DogeObserver<JSONObject>(this) {
                @Override
                public void onSuccess(JSONObject j) {

                }

                @Override
                public void onFailed(int code, String msg) {

                }
            });
//            d.dispose();
        });


        CrazyDogCache<CacheTestBean> testBeanCrazyDogCache = new CrazyDogCache<>(this, CacheTestBean.class);
        CacheTestBean cache = testBeanCrazyDogCache.getCache();
        Log.e("CrazyDogCache", JSON.toJSONString(cache));

        cache.age = cache.age+1;
        cache.name = cache.name+"N";
        cache.id = cache.id+"id";
        cache.content = cache.content+"content";

        testBeanCrazyDogCache.save(cache);

        CacheTestBean cacheNew = testBeanCrazyDogCache.getCache();
        Log.e("CrazyDogCache", JSON.toJSONString(cacheNew));

        new TestTask().start(new TaskDog.ITaskResult<Boolean>() {
            @Override
            public void done(Boolean aBoolean) {
                // 主线程：任务执行完
                // aBoolean 就是 test() 返回的值
                if (aBoolean) {
                    Log.e("TaskDog","处理完成");
                } else {
                    Log.e("TaskDog","处理失败");
                }
            }

            @Override
            public void failed(String reason) {
                // 主线程：出错了
            }
        });

        img_frame = findViewById(R.id.img_frame);
        // 1. 初始化 CameraDeviceManager
        cameraManager = new CameraDeviceManager();
        cameraManager.initCameraManager(this);

        // 2. 设置预览 TextureView
        TextureView previewView = findViewById(R.id.texture_view);
        cameraManager.setPreviewTextureView(previewView);

        // 3. 初始化 ImageReader（按需设置宽高和格式）
        cameraManager.getImageReaderManager().initImageReader(1080,1920);

        // 4. 设置帧回调（如果需要处理图像数据）
        cameraManager.setFrameCallback(new Camera2FrameCallback.FrameListener(100) {
            @Override
            public void onFrameResult(byte[] nv21Data, int width, int height) {
                // 1. NV21 → Bitmap
                YuvImage yuvImage = new YuvImage(nv21Data, ImageFormat.NV21, width, height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, baos);
                byte[] jpegData = baos.toByteArray();
                Bitmap rawBitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
                // 2. 旋转 90 度（后置摄像头竖屏标准修正）
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(
                        rawBitmap,
                        0, 0,
                        rawBitmap.getWidth(),
                        rawBitmap.getHeight(),
                        matrix,
                        true
                );
                rawBitmap.recycle(); // 及时回收避免OOM

                // 3. 显示到 ImageView
                img_frame.post(() -> {
                    img_frame.setImageBitmap(rotatedBitmap);
                });

                if(FaceSDKManager.initStatus == FaceSDKManager.SDK_MODEL_LOAD_SUCCESS){
                    byte[] feature512 = new byte[512];
                    float ret = FaceSDKManager.getInstance().personDetect(rotatedBitmap,feature512,FaceUtils.getInstance().getBDFaceCheckConfig(),MainActivity.this);

                    if(ret == 128){
                        List<? extends Feature> featureList = FaceSDKManager.getInstance().getFaceSearch().search(BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,0,1,feature512);
                        if(featureList==null||featureList.size()==0){
                            ToastUtils.toast(MainActivity.this,"抓到特征!\n无数据");
                            FaceSDKManager.getInstance().getFaceSearch().pushPersonById((int)(System.currentTimeMillis()%10000000),feature512);
                        }else{
                            if(featureList.get(0).getScore()<80&&featureList.get(0).getScore()>60){
                                ToastUtils.toast(MainActivity.this,"抓到特征!\n比对分数不确定：分数"+featureList.get(0).getScore());
                            }else if(featureList.get(0).getScore()<60){
                                ToastUtils.toast(MainActivity.this,"抓到特征!\n"+"新用户，进行注册");
                                FaceSDKManager.getInstance().getFaceSearch().pushPersonById((int)(System.currentTimeMillis()%10000000),feature512);
                            }else {
                                ToastUtils.toast(MainActivity.this, "抓到特征!\n你是ID：" + featureList.get(0).getId());
                            }
                        }
                    }
                }
            }
        });

        CrazyPermission.cameraAndStorage(this, new CrazyPermission.IPermissionResult() {
            @Override
            public void result(boolean allGranted) {
                if(allGranted){
                    // 5. 打开相机（需先申请权限）
                    String[] cameraIds = cameraManager.getCameraIdList();
                    if (cameraIds.length > 0) {
                        cameraManager.openCamera(cameraIds[0]); // 打开后置相机（一般 id=0 是后置，1 是前置）
                    }

                    // 6. 启动预览
                    cameraManager.startPreview();

                    dbSdkInit();

                }
            }
        });

    }


    private void dbSdkInit(){
        BdFaceAuth bdFaceAuth = new BdFaceAuth();
        bdFaceAuth.initLicenseOnLine(this, "3TWX-NFK7-FDEF-QQEB", new com.baidu.idl.main.facesdk.callback.Callback() {
            @Override
            public void onResponse(int code, String msg) {
                if(code == 0){
                    ToastUtils.toast(MainActivity.this,"激活成功");
                    FaceSDKManager.getInstance().initModel(MainActivity.this, FaceUtils.getInstance().getBDFaceSDKConfig(), new SdkInitListener() {
                        @Override
                        public void initStart() {

                        }

                        @Override
                        public void initLicenseSuccess() {

                        }

                        @Override
                        public void initLicenseFail(int errorCode, String msg) {

                        }

                        @Override
                        public void initModelSuccess() {
                            ToastUtils.toast(MainActivity.this,"模型初始化成功");
                            FaceSDKManager.initModelSuccess = true;
                        }

                        @Override
                        public void initModelFail(int errorCode, String msg) {
                            ToastUtils.toast(MainActivity.this,"模型初始化失败："+msg);
                        }
                    });

                    FaceApi.getInstance().init(new DBLoadListener() {
                        @Override
                        public void onStart(int successCount) {

                        }

                        @Override
                        public void onLoad(int finishCount, int successCount, float progress) {

                        }

                        @Override
                        public void onComplete(List<User> features, int successCount) {
                            ToastUtils.toast(MainActivity.this,"数据库初始化成功");
                        }

                        @Override
                        public void onFail(int finishCount, int successCount, List<User> features) {
                            ToastUtils.toast(MainActivity.this,"数据库初始化失败");
                        }
                    },MainActivity.this);
                }else {
                    ToastUtils.toast(MainActivity.this,"激活失败");
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // 7. 释放资源（如 onDestroy 中）
        cameraManager.releaseCamera();
    }
}
