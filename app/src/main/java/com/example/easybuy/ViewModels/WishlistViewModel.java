package com.example.easybuy.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Models.WishlistModel;
import com.example.easybuy.Repository.WishlistRepo;

import java.util.List;

public class WishlistViewModel extends ViewModel {

    private final WishlistRepo wishlistRepo;

    public WishlistViewModel() {
        wishlistRepo = new WishlistRepo();
    }

    public LiveData<List<WishlistModel>> getWishlistItems(String userId) {
        return wishlistRepo.getWishlistItems(userId);
    }

    public LiveData<Integer> getWishlistItemCount(String userId) {
        return wishlistRepo.getWishlistItemCount(userId);
    }

    public void addToWishlist(String userId, WishlistModel item) {
        wishlistRepo.addToWishlist(userId, item);
    }

    public void removeFromWishlist(String userId, WishlistModel item) {
        wishlistRepo.removeFromWishlist(userId, item);
    }

    public LiveData<Boolean> isWishlisted(String userId, String productId) {
        return wishlistRepo.isWishlisted(userId, productId);
    }
}