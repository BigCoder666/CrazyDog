package me.tx.crazydog;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import me.tx.crazydog.bean.CrazyDogBean;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
public interface TestService {
    @POST("/public/user/login-auth")
    Observable<CrazyDogBean<JSONObject>> loginAuthMain(@Body Map<String, String> map);
}
