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
import com.example.easybuy.Models.WishlistModel;
import com.example.easybuy.R;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final Context context;
    private List<WishlistModel> itemList = new ArrayList<>();

    public interface RemoveListener {
        void onRemove(WishlistModel item, int position);
    }

    private RemoveListener removeListener;

    public void setRemoveListener(RemoveListener listener) {
        this.removeListener = listener;
    }

    public WishlistAdapter(Context context, List<WishlistModel> list) {
        this.context  = context;
        this.itemList = list != null ? list : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishlistModel item = itemList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.tvCategory.setText(item.getCategory());

        Glide.with(context)
                .load(item.getImage())
                .placeholder(R.drawable.ic_cart)
                .into(holder.ivImage);

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null)
                removeListener.onRemove(item, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageButton btnRemove;
        TextView tvTitle, tvPrice, tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage     = itemView.findViewById(R.id.ivWishlistImage);
            btnRemove   = itemView.findViewById(R.id.btnRemoveWishlist);
            tvTitle     = itemView.findViewById(R.id.tvWishlistTitle);
            tvPrice     = itemView.findViewById(R.id.tvWishlistPrice);
            tvCategory  = itemView.findViewById(R.id.tvWishlistCategory);
        }
    }

    public void updateItems(List<WishlistModel> newList) {
        itemList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }
}
