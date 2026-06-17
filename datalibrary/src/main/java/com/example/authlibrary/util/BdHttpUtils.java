package com.example.authlibrary.util;

import android.content.Context;
import android.util.Log;

import com.example.authlibrary.CodeDetail;
import com.example.authlibrary.data.SituationData;
import com.example.datalibrary.R;
import com.example.datalibrary.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BdHttpUtils {
    private static final String TAG = "BdHttpUtils";
    public static SituationData requestPost(Context context, String licenseID , String deviceID){
        LogUtils.d(TAG,  "requestPost licenseID = " + licenseID + " deviceID = " + deviceID);
        OutputStream outputStream = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        String resultData = null;
        SituationData situationData = new SituationData();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceId", deviceID);
            jsonObject.put("key", licenseID);
            jsonObject.put("platformType", 2);
            jsonObject.put("version", 5);
            String paramStr = jsonObject.toString();
            byte[] postDataBytes = paramStr.getBytes(StandardCharsets.UTF_8);
            HttpURLConnection conn = (HttpURLConnection) (new URL("https://ai.baidu.com/activation/key/activate")).openConnection();
            System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
            System.setProperty("sun.net.client.defaultReadTimeout", "8000");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();
            outputStream = conn.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();
            int responseCode = conn.getResponseCode();
            LogUtils.e(TAG, "responseCode: " + responseCode);
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024];
                baos = new ByteArrayOutputStream();
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                byte[] b = baos.toByteArray();
                resultData = new String(b, "utf-8");
                baos.flush();
                LogUtils.e(TAG, "resultData: " + resultData);
                situationData.setCode(CodeDetail.SUCCESS);
                situationData.setHttpResponse(resultData);
                return situationData;
            }

            situationData.setCode(CodeDetail.HTTP_ERROR_ERROR);
            situationData.setMassage(context.getResources().getString(R.string.error_http_request_failed) + getHttpErrorManager(context , responseCode));
        } catch (IOException e) {
            LogUtils.e(TAG, "error IOException: " + e.getMessage());
            situationData.setCode(CodeDetail.WIFI_ERROR);
            situationData.setMassage(context.getResources().getString(R.string.error_network_connection) + e.getMessage());
        } catch (JSONException e) {
            LogUtils.e(TAG, "error JSONException: " + e.getMessage());
            situationData.setCode(CodeDetail.JSON_CREATE_ERROR);
            situationData.setMassage(context.getResources().getString(R.string.error_json_exception) + e.getMessage());
        }
        return situationData;
    }

    public static String getHttpErrorManager(Context context , int code){
        LogUtils.e(TAG, "getHttpErrorManager code = " + code);
        if (code == HttpURLConnection.HTTP_ACCEPTED){
            return context.getResources().getString(R.string.http_accepted);
        }else if (code == HttpURLConnection.HTTP_MOVED_PERM){
            return context.getResources().getString(R.string.http_moved_perm);
        }else if (code == HttpURLConnection.HTTP_MOVED_TEMP){
            return context.getResources().getString(R.string.http_moved_temp);
        }else if (code == HttpURLConnection.HTTP_BAD_REQUEST){
            return context.getResources().getString(R.string.http_bad_request);
        }else if (code == HttpURLConnection.HTTP_UNAUTHORIZED){
            return context.getResources().getString(R.string.http_unauthorized);
        }else if (code == HttpURLConnection.HTTP_FORBIDDEN){
            return context.getResources().getString(R.string.http_forbidden);
        }else if (code == HttpURLConnection.HTTP_NOT_FOUND){
            return context.getResources().getString(R.string.http_not_found);
        }else if (code == HttpURLConnection.HTTP_BAD_METHOD){
            return context.getResources().getString(R.string.http_bad_method);
        }else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT){
            return context.getResources().getString(R.string.http_client_timeout);
        }else if (code == HttpURLConnection.HTTP_CONFLICT){
            return context.getResources().getString(R.string.http_conflict);
        }else if (code == HttpURLConnection.HTTP_GONE){
            return context.getResources().getString(R.string.http_gone);
        }else if (code == HttpURLConnection.HTTP_UNAVAILABLE){
            return context.getResources().getString(R.string.http_unavailable);
        }
        return "";
    }
}
