package com.example.easybuy.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Models.CartModel;
import com.example.easybuy.Models.OrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartRepo {

    private final DatabaseReference db;

    public CartRepo() {
        db = FirebaseDatabase.getInstance().getReference();
    }
    
    public LiveData<List<CartModel>> getCartItems(String userId) {
        MutableLiveData<List<CartModel>> cartItems = new MutableLiveData<>();
        db.child("cart").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CartModel> list = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                CartModel item = child.getValue(CartModel.class);
                                if (item != null) list.add(item);
                            }
                        }
                        cartItems.setValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartItems.setValue(new ArrayList<>());
                    }
                });
        return cartItems;
    }

   
    public LiveData<Integer> getCartItemCount(String userId) {
        MutableLiveData<Integer> cartItemCount = new MutableLiveData<>();
        db.child("cart").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItemCount.setValue((int) snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartItemCount.setValue(0);
                    }
                });
        return cartItemCount;
    }

    public void increaseQuantity(String userId, CartModel item) {
        db.child("cart").child(userId).child(item.getCartId())
                .child("quantity")
                .setValue(item.getQuantity() + 1);
    }

 
    public void decreaseQuantity(String userId, CartModel item) {
        int newQty = item.getQuantity() - 1;
        if (newQty <= 0) {
            removeItem(userId, item);
        } else {
            db.child("cart").child(userId).child(item.getCartId())
                    .child("quantity")
                    .setValue(newQty);
        }
    }
    
    public void removeItem(String userId, CartModel item) {
        db.child("cart").child(userId).child(item.getCartId()).removeValue();
    }
    
    public void clearCart(String userId) {
        db.child("cart").child(userId).removeValue();
    }

    
    public LiveData<Boolean> placeOrder(OrderModel order) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        String orderId = db.child("orders").push().getKey();
        if (orderId == null) {
            result.setValue(false);
            return result;
        }
        order.setOrderId(orderId);
        db.child("orders").child(orderId).setValue(order)
                .addOnSuccessListener(unused -> result.setValue(true))
                .addOnFailureListener(e  -> result.setValue(false));
        return result;
    }
}
