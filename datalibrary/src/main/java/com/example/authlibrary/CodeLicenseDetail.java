package com.example.authlibrary;

public class CodeLicenseDetail {
    /*
     * 未知错误
     * */
    public static final int LOGIC_INTERNAL_ERROR = 282000;
    /*
     * 非法参数
     * */
    public static final int INVALID_PARAM = 216100;
    /*
     * 不支持的状态类型
     * */
    public static final int SERVICE_NOT_SUPPORT = 216102;
    /*
     * 	请求参数不足
     * */
    public static final int NOT_ENOUGH_PARAM = 216101;
    /*
     * 	无操作权限
     * */
    public static final int NO_AUTH_TO_OPERATE = 290000;
    /*
     * 	序列号生成错误
     * */
    public static final int KEY_GENERATE_ERROR = 290001;
    /*
     * 	序列号错误
     * */
    public static final int KEY_INVALID = 290002;
    /*
     * 	设备标识错误
     * */
    public static final int DEVICEID_NOT_CORRECT = 290003;
    /*
     * 	license已在其它设备激活
     * */
    public static final int LICENSE_HAS_ACTIVED_ON_OTHER_DEVICE = 290004;
    /*
     * 	license生成失败
     * */
    public static final int LICENSE_GENERATE_ERROR = 290005;
    /*
     * 	license过期时间设置错误，必须比当前过期时间短
     * */
    public static final int LICENSE_UPDATE_TIME_ERROR = 290006;
    /*
     * 	license更新失败
     * */
    public static final int LICENSE_UPDATE_FAILED = 290007;
    /*
     * 	license下载次数已超过上限
     * */
    public static final int LICENSE_TIMES_HAS_REACHED_UPPER_LIMIT = 290008;

    /*
     * 	license已绑定其它设备
     * */
    public static final int LICENSE_BIND_ON_OTHER_DEVICE = 290009;
    /*
     * 	license设备绑定失败
     * */
    public static final int LICENSE_DEVICE_BIND_ERROR = 290010;
    /*
     * 	license查询失败
     * */
    public static final int LICENSE_QUERY_ERROR = 290011;

}
