package com.example.easybuy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybuy.Models.CartModel;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private List<CartModel> cartItemList = new ArrayList<>();

    public interface CartActionListener {
        void onIncrease(CartModel item, int position);
        void onDecrease(CartModel item, int position);
        void onDelete(CartModel item, int position);
        void onWishlist(CartModel item, int position);
    }

    private CartActionListener listener;

    public void setCartActionListener(CartActionListener listener) {
        this.listener = listener;
    }

    public CartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartItemList = cartModelList != null ? cartModelList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel item = cartItemList.get(position);

        holder.tvProductTitle.setText(item.getTitle());
        holder.tvProductPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.ic_cart)
                .into(holder.ivProductImage);

        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onIncrease(item, holder.getAdapterPosition());
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null) listener.onDecrease(item, holder.getAdapterPosition());
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item, holder.getAdapterPosition());
        });

        holder.btnWishlist.setOnClickListener(v -> {
            holder.btnWishlist.setImageResource(R.drawable.ic_heart_filled);
            if (listener != null) listener.onWishlist(item, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        ImageButton btnDelete, btnDecrease, btnIncrease, btnWishlist;
        TextView tvProductTitle, tvProductPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage  = itemView.findViewById(R.id.ivProductImage);
            btnDelete       = itemView.findViewById(R.id.btnDelete);
            btnDecrease     = itemView.findViewById(R.id.btnDecrease);
            btnIncrease     = itemView.findViewById(R.id.btnIncrease);
            btnWishlist     = itemView.findViewById(R.id.btnWishlist);
            tvProductTitle  = itemView.findViewById(R.id.tvProductTitle);
            tvProductPrice  = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity      = itemView.findViewById(R.id.tvQuantity);
        }
    }

    public void updateCartItems(List<CartModel> newList) {
        cartItemList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public double calculateSubtotal() {
        double total = 0;
        for (CartModel item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public List<CartModel> getCartItemList() {
        return cartItemList;
    }
}
