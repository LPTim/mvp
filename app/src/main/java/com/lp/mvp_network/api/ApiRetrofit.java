package com.lp.mvp_network.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lp.mvp_network.App;
import com.lp.mvp_network.base.BaseContent;
import com.lp.mvp_network.base.convert.MyGsonConverterFactory;
import com.lp.mvp_network.base.cookie.CookieManger;
import com.lp.mvp_network.base.gson.DoubleDefault0Adapter;
import com.lp.mvp_network.base.gson.IntegerDefault0Adapter;
import com.lp.mvp_network.base.gson.LongDefault0Adapter;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * File descripition:   创建Retrofit
 *
 * @author lp
 * @date 2018/6/19
 */

public class ApiRetrofit {
    public final String BASE_SERVER_URL = BaseContent.baseUrl;
    private String TAG = "ApiRetrofit %s";
    private static ApiRetrofit apiRetrofit;
    private Retrofit retrofit;
    private ApiServer apiServer;
    private static Gson gson;
    private static final int DEFAULT_TIMEOUT = 15;


    public ApiRetrofit() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder
                .cookieJar(new CookieManger(App.getContext()))
                .addInterceptor(interceptor)
//                .addInterceptor(new HeadUrlInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);//错误重联

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_SERVER_URL)
//                .addConverterFactory(GsonConverterFactory.create(buildGson()))//添加json转换框架(正常转换框架)
                .addConverterFactory(MyGsonConverterFactory.create(buildGson()))//添加json自定义（根据需求）
                //支持RxJava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClientBuilder.build())
                .build();
        apiServer = retrofit.create(ApiServer.class);
    }

    /**
     * 增加后台返回""和"null"的处理
     * 1.int=>0
     * 2.double=>0.00
     * 3.long=>0L
     *
     * @return
     */
    public static Gson buildGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                    .registerTypeAdapter(long.class, new LongDefault0Adapter())
                    .create();
        }
        return gson;
    }

    public static ApiRetrofit getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofit();
                }
            }
        }
        return apiRetrofit;
    }

    public ApiServer getApiService() {
        return apiServer;
    }

    /**
     * 请求访问quest    打印日志
     * response拦截器
     */
    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();

            Logger.wtf(TAG, "----------Request Start----------------");
            Logger.e(TAG, "| " + request.toString() + "===========" + request.headers().toString());
            Logger.json(content);
            Logger.e(content);
            Logger.wtf(TAG, "----------Request End:" + duration + "毫秒----------");

            return response.newBuilder()
                    .body(ResponseBody.create(mediaType, content))
                    .build();
        }
    };

    /**
     * 添加  请求头
     */
    public class HeadUrlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
//                    .addHeader("Content-Type", "text/html; charset=UTF-8")
//                    .addHeader("Vary", "Accept-Encoding")
//                    .addHeader("Server", "Apache")
//                    .addHeader("Pragma", "no-cache")
//                    .addHeader("Cookie", "add cookies here")
//                    .addHeader("_identity",  cookie_value)
                    .build();
            return chain.proceed(request);
        }
    }
}
