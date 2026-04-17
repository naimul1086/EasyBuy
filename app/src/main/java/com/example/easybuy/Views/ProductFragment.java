package com.example.easybuy.Views;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easybuy.Adapters.ProductAdapter;
import com.example.easybuy.Models.ProductModel;
import com.example.easybuy.R;
import com.example.easybuy.ViewModels.ProductViewModel;


import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {
    ProductViewModel productViewModel;
    ProductAdapter productAdapter;
    RecyclerView productRecyclerView;
    List<ProductModel> products = new ArrayList<>();
    ProgressBar progressBar;
   SearchView searchView;

    public ProductFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);
        productRecyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.ProductsearchView);
        
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(getContext(), products);
        productRecyclerView.setAdapter(productAdapter);
        
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        progressBar.setVisibility(View.VISIBLE);

        productViewModel.getProducts().observe(getViewLifecycleOwner(), productModelList -> {
            progressBar.setVisibility(View.GONE);
            if (productModelList != null) {
                if (!productModelList.isEmpty()) {
                    productAdapter.UpdateProductList(productModelList);
                } else {
                    Log.d("ProductFragment", "No products found.");
                }
            } else {
                Log.d("ProductFragment", "ProductModelList is null.");
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return true;
            }
        });


        return view;
    }
}
