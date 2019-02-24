package com.game.http;





import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zhangxinyu on 2019/1/5.
 *
 */

public class HttpServer {

    //自定义接口服务地址
    private static String BaseUrl = "http://120.79.4.192";


    //封装请求接口
    public interface projectAPI {
        //登录
        @GET("login")
        Call<ResponseBody> logIn(@Query("user") String user, @Query("password") String password);
        //排行榜查询
        @GET("list")
        Call<ResponseBody> worldLi();
        //更新分数
        @POST("updata")
        Call<ResponseBody> upData(@Query("user") String user, @Query("count") int count);
        //注册
        @FormUrlEncoded
        @POST("logup")
        Call<ResponseBody> logUp(@FieldMap Map<String,String> map);
    }

    public static void upData(String user, int count, final Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BaseUrl)
                .build();

        projectAPI projectAPI = retrofit.create(HttpServer.projectAPI.class);

        Call<ResponseBody> call = projectAPI.upData(user,count);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }

    //logIn
    public static void logIn( String user,String password, final Callback<ResponseBody> callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BaseUrl)
                .build();

        projectAPI projectAPI = retrofit.create(HttpServer.projectAPI.class);

        Call<ResponseBody> call = projectAPI.logIn(user,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });

    };

    //logup
    public static void logUp(Map<String,String> map, final Callback<ResponseBody> callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BaseUrl)
                .build();

        projectAPI projectAPI = retrofit.create(HttpServer.projectAPI.class);
        Call<ResponseBody> call = projectAPI.logUp(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }

    //worldLi
    public static void worldLi(final Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BaseUrl)
                .build();
        projectAPI projectAPI = retrofit.create(HttpServer.projectAPI.class);
        Call<ResponseBody> call = projectAPI.worldLi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });

    }
}
