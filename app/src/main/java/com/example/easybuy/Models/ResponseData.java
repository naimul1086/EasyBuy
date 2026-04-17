package com.example.easybuy.Models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ResponseData {
    @SerializedName("products")
    List<ProductModel> products = new ArrayList<>();

    public List<ProductModel> getProducts() {
        return products;
    }
}
