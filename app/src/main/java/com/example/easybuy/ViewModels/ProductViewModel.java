package com.example.easybuy.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Models.ProductModel;
import com.example.easybuy.Repository.ProductRepo;

import java.util.List;

public class ProductViewModel extends ViewModel {
    ProductRepo productRepo;
    public ProductViewModel(){
        productRepo=new ProductRepo();
    }
    public LiveData<List<ProductModel>> getProducts(){
        return productRepo.getProducts();
    }


}
