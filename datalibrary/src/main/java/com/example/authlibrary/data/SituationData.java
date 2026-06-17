package com.example.authlibrary.data;

public class SituationData {

    private String massage; // 错误提示

    private String[] value; // 读取内容

    private String httpResponse; // 在线激活网络请求结果

    private String key; // key
    private int code; // 状态码
    public String getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
