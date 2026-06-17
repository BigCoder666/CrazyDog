package com.example.datalibrary.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.example.datalibrary.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnLineFeatureManager {
    private static final OnLineFeatureManager INSTANCE = new OnLineFeatureManager();

    public static OnLineFeatureManager getInstance() {
        return INSTANCE;
    }

    public OnLineFeature get(Context context , Bitmap bitmap , String name){
        String auth = getAuth(context); // 获取授权key
        return getFeature(context , auth , bitmapToBase64(bitmap) , name );
    }
    private OnLineFeature getFeature(Context context , String acctssToken , String image , String bitmapName ){

        OnLineFeature onLineFeature = new OnLineFeature();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("access_token" , acctssToken);
            params.put("image" , image) ;
            params.put("image_type" , "BASE64");
            params.put("version" , "Android_8001");
            params.put("max_face_num" , "1");
            params.put("prob_threshold" , "0.5");
            params.put("min_face_size" , "50");
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String , Object> param : params.entrySet()){
                if (postData.length() != 0){
                    postData.append("&");
                }
                    postData.append(URLEncoder.encode(param.getKey() , "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()) , "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            HttpURLConnection conn = (HttpURLConnection) (new URL("https://aip.baidubce.com/rest/2.0/face/v1/feature")).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type" , "application/json");
            conn.setRequestProperty("Content-Length" , String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream() ,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;){
                sb.append((char) c);
            }
            in.close();
            conn.disconnect();

            String responseStr = sb.toString();

            Log.e("response" , responseStr);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(responseStr);
                int code = jsonObject.getInt("error_code");
                if (code == 0){
                    JSONObject result = (JSONObject)  jsonObject.getJSONObject("result");
                    JSONArray faceList = (JSONArray)  result.getJSONArray("face_list");
                    if (faceList.length() > 0){

                        JSONObject faceListString = (JSONObject) faceList.get(0);
                        onLineFeature.feature = faceListString.getString("feature");
                    }else {
                        onLineFeature.manager = bitmapName + context.getResources().getString(R.string.error_cannot_detect_face);
                    }
                }else {
                    onLineFeature.manager = bitmapName + context.getResources().getString(R.string.error_request_failed) + code;
                }

            }catch (Exception e){
                e.getLocalizedMessage();
                onLineFeature.manager = bitmapName + context.getResources().getString(R.string.error_parse_failed);
            }
        } catch (Exception e) {
            onLineFeature.manager = bitmapName + context.getResources().getString(R.string.error_network_request_failed) + e.getMessage();
        }
        return onLineFeature;
    }
    /*
     * bitmap转base64
     * */
    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*end*/
    public static String getAuth(Context context) {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "Okt8CXC4UY4pgWk7EnGnz4Et";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "bKUl8Aclgrds2gHZ38lVzD0vY5QVb6o8";
        return getAuth(context , clientId, clientSecret);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(Context context , String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String accessToken = jsonObject.getString("access_token");
            return accessToken;
        } catch (Exception e) {
            System.err.printf(context.getResources().getString(R.string.error_get_token_failed));
            e.printStackTrace(System.err);
        }
        return null;
    }

    class OnLineFeature{
        String feature;
        String manager;
    }
}