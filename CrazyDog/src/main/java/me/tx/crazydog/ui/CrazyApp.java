package me.tx.crazydog.ui;

import android.app.Application;

import java.util.List;

import me.tx.crazydog.CrazyDog;
import me.tx.crazydog.net.NetDogSetting;

public abstract class CrazyApp extends Application {

    public abstract List<NetDogSetting> buildNetDog();

    CrazyDog crazyDog = null;

    public <T> T getService(Class<T> serviceClass){
        return crazyDog.getNetDogService(serviceClass);
    }

    public <T> void addHeader(Class<T> serviceClass,String k,String v){
        crazyDog.addHeader(serviceClass,k,v);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        crazyDog = CrazyDog.getInstance();

        for(NetDogSetting netDogSetting:buildNetDog()){
            crazyDog.buildNetDogService(netDogSetting);
        }
    }
}
