package com.example.easybuy.Repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Models.ProductModel;
import com.example.easybuy.Models.ResponseData;
import com.example.easybuy.Network.ApiSevices;
import com.example.easybuy.Network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepo {
    private ApiSevices apiSevices;
    private RetrofitClient retrofitClient;
    private MutableLiveData<List<ProductModel>> productLiveData = new MutableLiveData<>();

    public ProductRepo() {
        retrofitClient = RetrofitClient.getInstance();
        apiSevices = retrofitClient.getApiSevices();
    }

    public LiveData<List<ProductModel>> getProducts() {
        apiSevices.getProducts().enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        List<ProductModel> products = response.body().getProducts();
                        productLiveData.setValue(products);
                        Log.d("From Retrofit", "onResponse: " + products);
                    }
                } else {
                    Log.d("From Retrofit", "onResponse failed: " + response.code());
                    productLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable throwable) {
                productLiveData.setValue(null);
                Log.d("From Retrofit", "onFailure: " + throwable.getMessage());
            }
        });
        return productLiveData;
    }
}
