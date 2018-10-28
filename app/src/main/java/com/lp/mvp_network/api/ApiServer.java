package com.lp.mvp_network.api;


import com.lp.mvp_network.activity.MainBean;
import com.lp.mvp_network.base.mvp.BaseModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * File descripition:
 * <p>
 * \@GET  Observable    @Query
 * \@FormUrlEncoded     @POST   Observable  @FieldMap   @FieldMap HashMap<String, String> params
 * \@Multipart  @POST   Observable  @PartMap    @PartMap Map<String, RequestBody> map
 *
 * @author lp
 * @date 2018/6/19
 */

public interface ApiServer {
    //示例    多种类型请求方式

//    @POST("api/Activity/get_activities?")
//    Observable<BaseModel<List<>>> getApi1(@Query("time") String requestType);

//    @GET("api/Activity/get_activities?")
//    Observable<BaseModel<List<>>> getApi1(@Query("time") String requestType);

//    @FormUrlEncoded
//    @POST("api/Activity/get_activities?")
//    Observable<BaseModel<List<>>> getApi1(@Field("time") String requestType);

//    @FormUrlEncoded
//    @POST("api/Activity/get_activities?")
//    Observable<BaseModel<List<>>> getApi1(@FieldMap HashMap<String, String> params);

//    @Multipart
//    @POST("api/Activity/get_activities?")
//    Observable<BaseModel<List<>>> getApi1(@PartMap Map<String, RequestBody> map);


    @POST("api/Activity/get_activities?")
    Observable<BaseModel<List<MainBean>>> getMain(@Query("time") String requestType);

}
