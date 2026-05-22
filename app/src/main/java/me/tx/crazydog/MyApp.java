package me.tx.crazydog;

import android.os.Build;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.tx.crazydog.net.NetDogSetting;
import me.tx.crazydog.ui.CrazyApp;

public class MyApp extends CrazyApp {
    @Override
    public List<NetDogSetting> buildNetDog() {
        List<NetDogSetting> listService = Arrays.asList(
                new NetDogSetting.Builder<TestService>()
                        .setBaseUrl("http://ucapi.hicootest.com/")
                        .setServiceClass(TestService.class)
                        .addHeader("Brand", Build.BRAND)
                        .addHeader("Model",Build.MODEL)
                        .addHeader("login-type","app")
                        .addHeader("AndroidVersion",String.valueOf(Build.VERSION.SDK_INT))
                        .addHeader("Client-Version","merchant-app/1.0.0")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36")
                        .setHttpTimeOut(5)
                        .build()
        );
        return listService;
    }
}
