package com.example.easybuy.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Models.WishlistModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WishlistRepo {

    private final DatabaseReference db;

    public WishlistRepo() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<WishlistModel>> getWishlistItems(String userId) {
        MutableLiveData<List<WishlistModel>> wishlistItems = new MutableLiveData<>();
        db.child("wishlist").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<WishlistModel> list = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                WishlistModel item = child.getValue(WishlistModel.class);
                                if (item != null) list.add(item);
                            }
                        }
                        wishlistItems.setValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        wishlistItems.setValue(new ArrayList<>());
                    }
                });
        return wishlistItems;
    }

    public LiveData<Integer> getWishlistItemCount(String userId) {
        MutableLiveData<Integer> count = new MutableLiveData<>();
        db.child("wishlist").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        count.setValue((int) snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        count.setValue(0);
                    }
                });
        return count;
    }

    public void addToWishlist(String userId, WishlistModel item) {
        db.child("wishlist").child(userId)
                .orderByChild("productId")
                .equalTo(item.getProductId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            String wishlistId = db.child("wishlist").child(userId).push().getKey();
                            if (wishlistId == null) return;
                            item.setWishlistId(wishlistId);
                            db.child("wishlist").child(userId).child(wishlistId).setValue(item);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public void removeFromWishlist(String userId, WishlistModel item) {
        db.child("wishlist").child(userId).child(item.getWishlistId()).removeValue();
    }


    public LiveData<Boolean> isWishlisted(String userId, String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        db.child("wishlist").child(userId)
                .orderByChild("productId")
                .equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        result.setValue(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        result.setValue(false);
                    }
                });
        return result;
    }
}