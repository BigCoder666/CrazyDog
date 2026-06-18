package me.tx.crazydog;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.authlibrary.BdFaceAuth;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.listener.DBLoadListener;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.User;
import com.example.datalibrary.threshold.SingleBaseConfig;
import com.example.datalibrary.utils.FaceUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import me.tx.crazydog.cache.CrazyDogCache;
import me.tx.crazydog.camera2.Camera2FrameCallback;
import me.tx.crazydog.camera2.CameraDeviceManager;
import me.tx.crazydog.task.TaskDog;
import me.tx.crazydog.ui.CrazyActivity;

public class MainActivity extends CrazyActivity {
    CameraDeviceManager cameraManager;

    RecyclerView recycler_view;

    List<String> logList = new ArrayList<>();

    public final static String GROUP_ID = "UPHICOO";

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

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(new RecyclerView.Adapter<LogHloder>() {
            @NonNull
            @Override
            public LogHloder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new LogHloder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull LogHloder holder, int position) {
                ((TextView)holder.itemView).setText(logList.get(position));
            }

            @Override
            public int getItemCount() {
                return logList.size();
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
                if(FaceSDKManager.initStatus == FaceSDKManager.SDK_MODEL_LOAD_SUCCESS){
                    FaceSDKManager.getInstance().feed(nv21Data,width,height);
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

    private void simpleNotify(){
        while (logList.size() > 10) {
            logList.remove(logList.size() - 1);
        }
        recycler_view.getAdapter().notifyDataSetChanged();
    }

    private class LogHloder extends RecyclerView.ViewHolder{
        public LogHloder(@NonNull View itemView) {
            super(itemView);
        }
    }


    private void dbSdkInit(){
        BdFaceAuth bdFaceAuth = new BdFaceAuth();
        bdFaceAuth.initLicenseOnLine(this, "3TWX-NFK7-FDEF-QQEB", new com.baidu.idl.main.facesdk.callback.Callback() {
            @Override
            public void onResponse(int code, String message) {
                if(code == 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logList.add(0,"激活成功");
                            simpleNotify();
                        }
                    });
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logList.add(0,"模型初始化成功");
                                    simpleNotify();
                                }
                            });
                            FaceSDKManager.initModelSuccess = true;
                            FaceApi.getInstance().init(new DBLoadListener() {
                                @Override
                                public void onStart(int successCount) {

                                }

                                @Override
                                public void onLoad(int finishCount, int successCount, float progress) {

                                }

                                @Override
                                public void onComplete(List<User> features, int successCount) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            logList.add(0,"数据库初始化成功,用户总数："+features.size());
                                            FaceApi.getInstance().setUsers(features);
                                            FaceSDKManager.getInstance().initPush();
                                            SingleBaseConfig.getBaseConfig().setRgbDetectDirection(90);
                                            FaceSDKManager.getInstance().startRecognize(GROUP_ID, new FaceSDKManager.IRecResult() {
                                                @Override
                                                public void notSure(User u, float score) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            logList.add(0,"也许:"+u.getUserId()+"\n低分数" + score+"\n-------------------------");
                                                            simpleNotify();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void sure(User u, float score) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            logList.add(0,"你是:"+u.getUserId()+"\n分数" + score+"\n-------------------------");
                                                            simpleNotify();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void unknow() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            logList.add(0,"不认识你\n-------------------------");
                                                            simpleNotify();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void tips(String msg) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            logList.add(0,msg+"\n-------------------------");
                                                            simpleNotify();
                                                        }
                                                    });
                                                }
                                            });
                                            simpleNotify();
                                        }
                                    });
                                }

                                @Override
                                public void onFail(int finishCount, int successCount, List<User> features) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            logList.add(0,"数据库初始化失败");
                                            simpleNotify();
                                        }
                                    });
                                }
                            },MainActivity.this);
                        }

                        @Override
                        public void initModelFail(int errorCode, String msg) {
                            logList.add(0,"模型初始化失败："+msg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    simpleNotify();
                                }
                            });
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logList.add(0,"激活失败");
                            simpleNotify();
                        }
                    });
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
