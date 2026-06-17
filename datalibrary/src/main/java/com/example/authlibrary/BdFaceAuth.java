package com.example.authlibrary;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceQueue;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.statistic.PostDeviceInfo;
import com.baidu.idl.main.facesdk.utils.PreferencesUtil;
import com.baidu.liantian.ac.LH;
import com.baidu.vis.facecollect.license.AndroidLicenser;
import com.baidu.vis.facecollect.license.BDLicenseLocalInfo;
import com.example.authlibrary.data.SituationData;
import com.example.authlibrary.util.BdFileUtils;
import com.example.authlibrary.util.BdHttpUtils;
import com.example.datalibrary.R;
import com.example.datalibrary.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class BdFaceAuth {
    private static final String TAG = "BdFaceAuth";
    private FaceAuth faceAuth;
    private static String deviceID = "";
    private static String mIdFlag = "1";
    private static final int ALGORITHM_ID = 3;
    private static final String OFFLINE_KEY = "activate_offline_key";
    private static final String ONLINE_KEY = "activate_online_key";
    private static final String BATCH_KEY = "activate_batch_key";

    public BdFaceAuth() {
        faceAuth = new FaceAuth();
    }
    public void setActiveLog(BDFaceSDKCommon.BDFaceLogInfo logInfo, int isLog) {
        faceAuth.setActiveLog(logInfo, isLog);
    }

    public void setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode runMode, int coreNum) {
        faceAuth.setCoreConfigure(runMode, coreNum);
    }
    public String getDeviceId(Context context) {
        if ("".equals(deviceID)) {
            try {
                LH.init(context, false);
                LogUtils.i(TAG, "Load liantian ac succeed");
                Pair<String, String> deviceId = LH.getId(context, mIdFlag);
                if (deviceId != null && deviceId.second != null) {
                    deviceID = ((String) deviceId.second).toUpperCase();
                }
            } catch (Exception var2) {
                var2.printStackTrace();
                LogUtils.i(TAG,  "Load liantian ac failed");
            }
        }

        return deviceID;
    }
    /*
    * 离线激活
    * */
    public void initLicenseOffLine(final Context context, final Callback callback) {
        LogUtils.i(TAG,  "initLicenseOffLine");
        if (context == null) {
            LogUtils.i(TAG,  "context is null");
            callback.onResponse(CodeDetail.CONTEXT_NOT_ERROR, "context is null");
            return;
        }
        if (TextUtils.isEmpty(getDeviceId(context))) {
            LogUtils.i(TAG,  "deviceId is null");
            callback.onResponse(CodeDetail.DEVICES_ID_NOT_ERROR, context.getResources().getString(R.string.devices_id_null));
            return;
        }

        // 检查SD卡是否装载
        boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String path;
        if (isSDPresent) {
            // 获取SD卡根目录
            File sdCardPath = Environment.getExternalStorageDirectory();
            assert sdCardPath != null;
            path = sdCardPath.getAbsolutePath();
            initLicenseOffLine(context, path, OFFLINE_KEY , callback);
            // 输出SD卡路径
            LogUtils.d(TAG, "SdCard: " + path);

        } else {
            LogUtils.d(TAG, "not find SdCard");
            initLicenseOffLine(context, context.getCacheDir().getAbsolutePath() , OFFLINE_KEY , callback);
        }

    }

    public void initLicenseOffLine(final Context context, String path, String preferencesKey , final Callback callback) {
        SituationData situationData = BdFileUtils.readAuthFile(context, path);
        if (situationData.getCode() == CodeDetail.SUCCESS) {
            initLicenseOffLine(context, situationData.getKey(), situationData.getValue(), preferencesKey , callback);
        } else {
            callback.onResponse(situationData.getCode(), situationData.getMassage());
        }
    }

    private void initLicenseOffLine(Context context, String key, String[] value, String preferencesKey, Callback callback) {
        LogUtils.v(TAG, "initLicenseOffLine key value preferencesKey");
        AndroidLicenser licenser = AndroidLicenser.getInstance();

        AndroidLicenser.ErrorCode errorCode1 = licenser.authFromFile(context,
                key, "idl-license.face-android", false, ALGORITHM_ID);
        if (errorCode1 == AndroidLicenser.ErrorCode.SUCCESS){
            int status = faceAuth.createInstance();
            LogUtils.v(TAG, "bdface_create_instance status " + status);
            PreferencesUtil.putString(preferencesKey , key);
            callback.onResponse(errorCode1.ordinal(), "");
            return;
        }

        AndroidLicenser.ErrorCode errorCode = licenser.authFromMemory(context,
                key, value, "idl-license.face-android", ALGORITHM_ID);
        if (errorCode != AndroidLicenser.ErrorCode.SUCCESS) {
            BDLicenseLocalInfo info = licenser.authGetLocalInfo(context, ALGORITHM_ID);
            if (info != null) {
                LogUtils.i(TAG, info.toString());
            }else {
                LogUtils.i(TAG, "info is null");
            }
        }else {
            int status = faceAuth.createInstance();
            LogUtils.v(TAG, "bdface_create_instance status " + status);
            PreferencesUtil.putString(preferencesKey , key);
        }
        String errMsgx = licenser.getErrorMsg(ALGORITHM_ID);
        callback.onResponse(errorCode.ordinal(), getErrorManager(context , errorCode) + " , " + errMsgx);
    }
    /*
    * 获取离线激活错误码
    * */
    private String getErrorManager(Context context , AndroidLicenser.ErrorCode errorCode){
        LogUtils.v(TAG, "getErrorManager : " + errorCode.toString());
        switch (errorCode){
            case SUCCESS:
                return "";
            case LICENSE_NOT_INIT_ERROR:
                return context.getResources().getString(R.string.license_not_initialized);
            case LICENSE_DECRYPT_ERROR:
                return context.getResources().getString(R.string.license_decryption_failed);
            case LICENSE_INFO_FORMAT_ERROR:
                return context.getResources().getString(R.string.license_decryption_error);
            case LICENSE_KEY_CHECK_ERROR:
                return context.getResources().getString(R.string.license_key_decryption_error);
            case LICENSE_ALGORITHM_CHECK_ERROR:
                return context.getResources().getString(R.string.algorithm_id_verification_error);
            case LICENSE_MD5_CHECK_ERROR:
                return context.getResources().getString(R.string.md5_checksum_error);
            case LICENSE_DEVICE_ID_CHECK_ERROR:
                return context.getResources().getString(R.string.device_ID_verification_error);
            case LICENSE_PACKAGE_NAME_CHECK_ERROR:
                return context.getResources().getString(R.string.packet_name_verification_error);
            case LICENSE_EXPIRED_TIME_CHECK_ERROR:
                return context.getResources().getString(R.string.time_verification_failed);
            case LICENSE_FUNCTION_CHECK_ERROR:
                return context.getResources().getString(R.string.function_not_authorized);
            case LICENSE_TIME_EXPIRED:
                return context.getResources().getString(R.string.authorization_has_expired);
            case LICENSE_LOCAL_FILE_ERROR:
                return context.getResources().getString(R.string.local_file_reading_failed);
            case LICENSE_REMOTE_DATA_ERROR:
                return context.getResources().getString(R.string.Remote_data_retrieval_failed);
            case LICENSE_LOCAL_TIME_ERROR:
                return context.getResources().getString(R.string.local_time_verification_error);
            case LICENSE_PARAM_ERROR:
                return context.getResources().getString(R.string.parameter_error);
            case LICENSE_KEY_FILE_ERROR:
                return context.getResources().getString(R.string.key_error);
        }
        return context.getResources().getString(R.string.other_errors);
    }

    /*
     * 在线激活
     * */
    public void initLicenseOnLine(final Context context, final String licenseID, final Callback callback) {
        LogUtils.v(TAG, "initLicenseOnLine licenseID" );
        Runnable runnable = new Runnable() {
            public void run() {
                if (context == null) {
                    LogUtils.i(TAG,  "context is null");
                    callback.onResponse(CodeDetail.CONTEXT_NOT_ERROR, "context is null");
                    return;
                }
                if (TextUtils.isEmpty(getDeviceId(context))) {
                    LogUtils.i(TAG,  "deviceId is null");
                    callback.onResponse(CodeDetail.DEVICES_ID_NOT_ERROR, context.getResources().getString(R.string.devices_id_null));
                    return;
                }

                File iniFile = new File(context.getFilesDir() + File.separator + "license.ini");
                File keyFile = new File(context.getFilesDir() + File.separator + "license.key");
                if (iniFile.canRead() && keyFile.canRead() && keyFile.exists()
                        && iniFile.exists() && keyFile.isFile() && iniFile.isFile()) {
                    SituationData situationData = BdFileUtils.readAuthFile(context, keyFile, iniFile);
                    LogUtils.d(TAG, "code = " +  situationData.getCode() + "  key = " + situationData.getKey() + "  licenseID = " + licenseID);
                    if (situationData.getCode() == CodeDetail.SUCCESS) {
                        if (situationData.getKey().equals(licenseID)) {
                            initLicenseOffLine(context, situationData.getKey(), situationData.getValue() , ONLINE_KEY , callback);
                            return;
                        }
                    }
                }
                iniFile.delete();
                keyFile.delete();
                SituationData situationData = BdHttpUtils.requestPost(context , licenseID, deviceID);
                if (situationData.getCode() == CodeDetail.SUCCESS) {
                    String response = situationData.getHttpResponse();
                    try {
                        JSONObject json = null;
                        json = new JSONObject(response);
                        int jsonErrorCode = json.optInt("error_code");
                        if (jsonErrorCode != 0) {
                            String errorMsg = json.optString("error_msg");
                            LogUtils.i("FaceSDK", "error_msg->" + errorMsg);
                            callback.onResponse(jsonErrorCode, getHttpErrorManager(context , jsonErrorCode) + " , " + errorMsg);
                            return;
                        } else {
                            situationData.setCode(CodeDetail.HTTP_RESULT_ERROR);
                            JSONObject result = json.optJSONObject("result");
                            if (result == null) {
                                LogUtils.d(TAG, "result = null" );
                                situationData.setMassage(context.getResources().getString(R.string.error_http_response_empty));
                                callback.onResponse(situationData.getCode(), situationData.getMassage());
                                return;
                            }
                            String license = result.optString("license");
                            if (TextUtils.isEmpty(license)) {
                                LogUtils.d(TAG, "license = null" );
                                situationData.setMassage(context.getResources().getString(R.string.error_http_license_empty));
                                callback.onResponse(situationData.getCode(), situationData.getMassage());
                                return;
                            }
                            String[] licenses = license.split(",");
                            if (licenses.length != 2) {
                                LogUtils.d(TAG, "licenses.length != 2" );
                                situationData.setMassage(context.getResources().getString(R.string.error_license_length_error));
                                callback.onResponse(situationData.getCode(), situationData.getMassage());
                                return;
                            }
                            BdFileUtils.saveAuthFile(context , licenseID, licenses, context.getFilesDir().getAbsolutePath());
                            initLicenseOffLine(context, licenseID, licenses, ONLINE_KEY, callback);
                            return;
                        }
                    } catch (JSONException e) {
                        LogUtils.d(TAG, "error : " + e.getMessage());
                        situationData.setCode(CodeDetail.JSON_CREATE_ERROR);
                        situationData.setMassage(context.getResources().getString(R.string.error_json_parse_exception));
                    }
                }
                callback.onResponse(situationData.getCode(), situationData.getMassage());
            }
        };
        FaceQueue.getInstance().execute(runnable);
    }


    /*
     * 获取在线激活错误码
     * */
    private String getHttpErrorManager(Context context , int errorCode){
        LogUtils.d(TAG, "getHttpErrorManager errorCode = " + errorCode);
        switch (errorCode){
            case CodeLicenseDetail.LOGIC_INTERNAL_ERROR:
                return context.getResources().getString(R.string.error_logic_internal_error);
            case CodeLicenseDetail.INVALID_PARAM:
                return context.getResources().getString(R.string.error_invalid_param);
            case CodeLicenseDetail.SERVICE_NOT_SUPPORT:
                return context.getResources().getString(R.string.error_service_not_support);
            case CodeLicenseDetail.NOT_ENOUGH_PARAM:
                return context.getResources().getString(R.string.error_not_enough_param);
            case CodeLicenseDetail.NO_AUTH_TO_OPERATE:
                return context.getResources().getString(R.string.error_no_auth_to_operate);
            case CodeLicenseDetail.KEY_GENERATE_ERROR:
                return context.getResources().getString(R.string.error_key_generate_error);
            case CodeLicenseDetail.KEY_INVALID:
                return context.getResources().getString(R.string.error_key_invalid);
            case CodeLicenseDetail.DEVICEID_NOT_CORRECT:
                return context.getResources().getString(R.string.error_deviceid_not_correct);
            case CodeLicenseDetail.LICENSE_HAS_ACTIVED_ON_OTHER_DEVICE:
                return context.getResources().getString(R.string.error_license_has_actived_on_other_device);
            case CodeLicenseDetail.LICENSE_GENERATE_ERROR:
                return context.getResources().getString(R.string.error_license_generate_error);
            case CodeLicenseDetail.LICENSE_UPDATE_TIME_ERROR:
                return context.getResources().getString(R.string.error_license_update_time_error);
            case CodeLicenseDetail.LICENSE_UPDATE_FAILED:
                return context.getResources().getString(R.string.error_license_update_failed);
            case CodeLicenseDetail.LICENSE_TIMES_HAS_REACHED_UPPER_LIMIT:
                return context.getResources().getString(R.string.error_license_times_has_reached_upper_limit);
            case CodeLicenseDetail.LICENSE_BIND_ON_OTHER_DEVICE:
                return context.getResources().getString(R.string.error_license_bind_on_other_device);
            case CodeLicenseDetail.LICENSE_DEVICE_BIND_ERROR:
                return context.getResources().getString(R.string.error_license_device_bind_error);
            case CodeLicenseDetail.LICENSE_QUERY_ERROR:
                return context.getResources().getString(R.string.error_license_query_error);
        }
        return context.getResources().getString(R.string.other_errors);
    }
    /*
    * 批量激活
    * */
    public void initLicenseBatchLine(final Context context, final String licenseKey, final Callback callback){
        if (context == null) {
            LogUtils.i(TAG,  "context is null");
            callback.onResponse(CodeDetail.CONTEXT_NOT_ERROR, "context is null");
            return;
        }
        if (TextUtils.isEmpty(getDeviceId(context))) {
            LogUtils.i(TAG,  "deviceId is null");
            callback.onResponse(CodeDetail.DEVICES_ID_NOT_ERROR, context.getResources().getString(R.string.devices_id_null));
            return;
        }
//        faceAuth.initLicenseBatchLine(context , licenseKey , callback);
        Runnable runnable = new Runnable() {
            public void run() {
                PreferencesUtil.initPrefs(context);
                String statics = PreferencesUtil.getString("statics", "");
                if (TextUtils.isEmpty(statics)) {
                    PostDeviceInfo.uploadDeviceInfo(context, new Callback() {
                        public void onResponse(int code, String response) {
                            if (code == 0) {
                                PreferencesUtil.putString("statics", "ok");
                            }

                        }
                    });
                }

                if (!TextUtils.isEmpty(licenseKey) && !TextUtils.isEmpty("idl-license.face-android")) {
                    AndroidLicenser licenser = AndroidLicenser.getInstance();
                    AndroidLicenser.ErrorCode errorCode = licenser.authFromFile(context, licenseKey, "idl-license.face-android", true, ALGORITHM_ID);
                    if (errorCode != AndroidLicenser.ErrorCode.SUCCESS) {
                        BDLicenseLocalInfo info = licenser.authGetLocalInfo(context, ALGORITHM_ID);
                        if (info != null) {
                            LogUtils.i("FaceSDK", info.toString());
                        }
                    }else {
                        faceAuth.createInstance();
                        PreferencesUtil.putString(BATCH_KEY , licenseKey);
                    }
                    String errMsg = licenser.getErrorMsg(ALGORITHM_ID);
                    callback.onResponse(errorCode.ordinal(), getErrorManager(context , errorCode) + " , " +errMsg);
                } else {
                    callback.onResponse(2, context.getResources().getString(R.string.license_keyword_empty));
                }

            }
        };
        FaceQueue.getInstance().execute(runnable);
    }
}
