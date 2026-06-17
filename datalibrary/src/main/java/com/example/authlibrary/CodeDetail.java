package com.example.authlibrary;

public class CodeDetail {
    /*
    * 读取成功
    * */
    public static final int SUCCESS = 1000;
    /*
     * 文件读取失败
     * */
    public static final int FILE_READ_ERROR = 1001;
    /*
     * 文件缓存清理失败
     * */
    public static final int FILE_CLEAN_ERROR = 1002;
    /*
     * 创建zip解析对象失败
     * */
    public static final int FILE_ZIP_CREATE_ERROR = 1003;
    /*
     * zip文件读取失败
     * */
    public static final int FILE_ZIP_READ_ERROR = 1004;
    /*
     * 没有读取到文件
     * */
    public static final int FILE_NOT_ERROR = 1005;
    /*
     * Context为空
     * */
    public static final int CONTEXT_NOT_ERROR = 1006;
    /*
     * devicesId为空
     * */
    public static final int DEVICES_ID_NOT_ERROR = 1007;
    /*
     * json异常
     * */
    public static final int JSON_CREATE_ERROR = 1008;
    /*
     * HTTP请求异常
     * */
    public static final int HTTP_ERROR_ERROR = 1009;
    /*
     * HTTP网络数据异常
     * */
    public static final int HTTP_RESULT_ERROR = 1010;
    /*
     * 网络链接异常
     * */
    public static final int WIFI_ERROR = 1011;

}
