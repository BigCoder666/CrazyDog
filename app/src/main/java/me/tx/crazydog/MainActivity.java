package me.tx.crazydog;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import me.tx.crazydog.bean.CrazyDogBean;
import me.tx.crazydog.cache.CrazyDogCache;
import me.tx.crazydog.net.LoadingObserver;
import me.tx.crazydog.net.NetDogObserver;
import me.tx.crazydog.net.NetDogSetting;
import me.tx.crazydog.task.TaskDog;
import me.tx.crazydog.ui.CrazyActivity;

public class MainActivity extends CrazyActivity {

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

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
    }
}
