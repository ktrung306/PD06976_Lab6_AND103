package com.example.lab6_pd06976_nguyenkhactrung.services;

import com.example.lab6_pd06976_nguyenkhactrung.model.Distributor;
import com.example.lab6_pd06976_nguyenkhactrung.model.Fruit;
import com.example.lab6_pd06976_nguyenkhactrung.model.Response;
import com.example.lab6_pd06976_nguyenkhactrung.model.User;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiServices {
    public static String BASE_URL = "http://192.168.1.6:3000/api/";

    @GET("get-list-distributor")
    Call<Response<ArrayList<Distributor>>> getListDistributor();
    @Multipart
    @POST("register-send-email")
    Call<Response<User>> register(@Part("username")RequestBody username,
                                  @Part("password") RequestBody password,
                                  @Part("email") RequestBody email,
                                  @Part("name") RequestBody name,
                                  @Part MultipartBody.Part avatar);
    @POST("login")
    Call<Response<User>> login(@Body User user);

    @GET("get-list-fruit")
    Call<Response<ArrayList<Fruit>>> getListFruit(@Header("Authorization") String token);
    //@Header("Authorization") là token ta aut ta cần truyền lên để có thể lấy dữ liệu

    @Multipart
    @POST("add-fruit-with-file-image")
    Call<Response<Fruit>> addFruitWithFileImage(@PartMap Map<String, RequestBody> requestBodyMap,
                                               @Part ArrayList<MultipartBody.Part> ds_hinh);
}
