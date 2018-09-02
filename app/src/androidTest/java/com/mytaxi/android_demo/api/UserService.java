package com.mytaxi.android_demo.api;

import retrofit2.Call;
import retrofit2.http.GET;

//https://randomuser.me/api/?seed=a1f30d446f820665
public interface UserService {
    @GET("api/?seed=a1f30d446f820665")
    Call<Response> api();
}
