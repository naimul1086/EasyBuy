package com.example.easybuy.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.Models.ProductModel;
import com.example.easybuy.R;
import com.example.easybuy.Views.DetailFragment;
import com.example.easybuy.Views.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final Context context;
    private List<ProductModel> OriginalproductList = new ArrayList<>();
    private List<ProductModel> FilteredProductList = new ArrayList<>();


    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.OriginalproductList = productList ;
        this.FilteredProductList= new ArrayList<>(OriginalproductList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = FilteredProductList.get(position);

        holder.tvTitle.setText(product.title != null ? product.title : "");
        holder.tvPrice.setText(String.format("$%.2f", product.price));
        holder.ratingBar.setRating((float)product.rating);
        holder.tvRating.setText(String.valueOf(product.rating));
        holder.tvBrand.setText(product.brand != null ? product.brand : "N/A");

        Glide.with(context)
                .load(product.thumbnail)
                .placeholder(R.drawable.ic_cart)
                .error(R.drawable.ic_cart)
                .into(holder.ivProductImage);

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                DetailFragment detailFragment = new DetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("product", product);
                detailFragment.setArguments(bundle);
                ((MainActivity) context).switchFragment(detailFragment, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return FilteredProductList != null ? FilteredProductList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvTitle, tvPrice, tvRating, tvBrand;
        RatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.productImage);
            tvTitle        = itemView.findViewById(R.id.productName);
            tvPrice        = itemView.findViewById(R.id.productPrice);
            tvRating       = itemView.findViewById(R.id.ratingText);
            tvBrand        = itemView.findViewById(R.id.productStock);
            ratingBar      = itemView.findViewById(R.id.ratingBar);
        }
    }
    public void filter(String query) {
        FilteredProductList.clear();
        if (query.isEmpty()) {
            FilteredProductList.addAll(OriginalproductList);
        } else {
            String q = query.toLowerCase().trim();
            for (ProductModel product : OriginalproductList) {
                if (product.getTitle().toLowerCase().contains(q)) {
                    FilteredProductList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void UpdateProductList(List<ProductModel> newList) {
        this.OriginalproductList = newList != null ? newList : new ArrayList<>();
        this.FilteredProductList = new ArrayList<>(OriginalproductList);
        notifyDataSetChanged();
    }

}