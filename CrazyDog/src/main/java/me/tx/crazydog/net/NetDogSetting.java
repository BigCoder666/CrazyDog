package me.tx.crazydog.net;

import java.util.HashMap;

import java.util.HashMap;

public class NetDogSetting<T> {

    private final String baseUrl;
    private final Class<T> serviceClass;
    private final int HTTP_TIME_OUT;
    private final HashMap<String, String> baseHeader;

    // 私有构造，只能通过 Builder 创建
    private NetDogSetting(Builder<T> builder) {
        this.baseUrl = builder.baseUrl;
        this.serviceClass = builder.serviceClass;
        this.HTTP_TIME_OUT = builder.HTTP_TIME_OUT;
        this.baseHeader = builder.baseHeader;
    }

    // ===================== Getter =====================
    public String getBaseUrl() {
        return baseUrl;
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }

    public int getHttpTimeOut() {
        return HTTP_TIME_OUT;
    }

    public HashMap<String, String> getBaseHeader() {
        return baseHeader;
    }

    // ===================== 核心：Builder 工厂 =====================
    public static class Builder<T> {
        private String baseUrl = "";
        private Class<T> serviceClass = null;
        private int HTTP_TIME_OUT = 10; // 默认10秒
        private HashMap<String, String> baseHeader = new HashMap<>();

        // 设置 BaseUrl
        public Builder<T> setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        // 设置 Service 接口 Class（关键！泛型绑定）
        public Builder<T> setServiceClass(Class<T> serviceClass) {
            this.serviceClass = serviceClass;
            return this;
        }

        // 设置超时时间
        public Builder<T> setHttpTimeOut(int timeOut) {
            this.HTTP_TIME_OUT = timeOut;
            return this;
        }

        // 添加单个 Header
        public Builder<T> addHeader(String key, String value) {
            this.baseHeader.put(key, value);
            return this;
        }

        // 直接设置 Header 集合
        public Builder<T> setBaseHeader(HashMap<String, String> baseHeader)
        {
            this.baseHeader = baseHeader;
            return this;
        }

        // 构建
        public NetDogSetting<T> build() {
            return new NetDogSetting<>(this);
        }
    }
}
