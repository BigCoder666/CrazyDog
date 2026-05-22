package me.tx.crazydog;

import java.util.HashMap;

import me.tx.crazydog.net.NetDog;
import me.tx.crazydog.net.NetDogSetting;

public class CrazyDog {

    private HashMap<String,NetDog> serviceMap = new HashMap<>();
    private static volatile CrazyDog instance;

    private CrazyDog() {
    }

    public <T> T getNetDogService(Class<T> serviceClass){
        String tag = serviceClass.getSimpleName();
        if(serviceMap.containsKey(tag)){
            return (T)serviceMap.get(tag).getService();
        }
        return null;
    }

    public <T> void buildNetDogService(NetDogSetting<T> netDogSetting){
        String tag = netDogSetting.getServiceClass().getSimpleName();
        NetDog<T> netDog = new NetDog<>(netDogSetting);
        serviceMap.put(tag,netDog);
    }

    public boolean addHeader(Class serviceClass,String k,String v){
        String tag = serviceClass.getSimpleName();
        if(serviceMap.containsKey(tag)){
            serviceMap.get(tag).onAddHeader(k,v);
            return true;
        }else {
            return false;
        }
    }

    public static CrazyDog getInstance() {
        if (instance == null) {
            synchronized (CrazyDog.class) {
                if (instance == null) {
                    instance = new CrazyDog();
                }
            }
        }
        return instance;
    }

}
