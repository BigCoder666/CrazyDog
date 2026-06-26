package me.tx.crazydog;

import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;

import io.reactivex.rxjava3.disposables.Disposable;
import me.tx.crazydog.cache.CrazyDogCache;
import me.tx.crazydog.camera2.Camera2FrameCallback;
import me.tx.crazydog.camera2.CameraDeviceManager;
import me.tx.crazydog.task.TaskDog;
import me.tx.crazydog.ui.CrazyActivity;

public class MainActivity extends CrazyActivity {
    CameraDeviceManager cameraManager;

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


        // 1. 初始化 CameraDeviceManager
        cameraManager = new CameraDeviceManager();
        cameraManager.initCameraManager(this);

        // 2. 设置预览 TextureView
        TextureView previewView = findViewById(R.id.texture_view);
        cameraManager.setPreviewTextureView(previewView);

        // 3. 初始化 ImageReader（按需设置宽高和格式）
        cameraManager.getImageReaderManager().initImageReader(1920,1920);

        // 4. 设置帧回调（如果需要处理图像数据）
        cameraManager.setFrameCallback(new Camera2FrameCallback.FrameListener(5) {
            @Override
            public void onFrameResult(byte[] nv21Data, int width, int height) {

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
