package com.example.easybuy.Network;

import static okhttp3.internal.Internal.instance;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    static RetrofitClient Instance;
    ApiSevices apiSevices;
    Retrofit retrofit;
    public RetrofitClient() {
        apiSevices = getRetrofit().create(ApiSevices.class);
    }
    public Retrofit getRetrofit() {
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://dummyjson.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }

    public ApiSevices getApiSevices() {
        return apiSevices;
    }
    public static RetrofitClient getInstance(){
        if(Instance==null){
            Instance=new RetrofitClient();
        }
        return Instance;
    }


}
