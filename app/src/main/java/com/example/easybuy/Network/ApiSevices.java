package com.example.easybuy.Network;

import com.example.easybuy.Models.ResponseData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiSevices {
    @GET("products")
    Call<ResponseData> getProducts();
}
