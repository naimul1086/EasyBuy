package com.example.easybuy.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Models.CartModel;
import com.example.easybuy.Models.OrderModel;
import com.example.easybuy.Repository.CartRepo;

import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepo cartRepository;

    public CartViewModel() {
        cartRepository = new CartRepo();
    }

    public LiveData<List<CartModel>> getCartItems(String userId) {
        return cartRepository.getCartItems(userId);
    }

    public LiveData<Integer> getCartItemCount(String userId) {
        return cartRepository.getCartItemCount(userId);
    }

    public void increaseQuantity(String userId, CartModel item) {
        cartRepository.increaseQuantity(userId, item);
    }

    public void decreaseQuantity(String userId, CartModel item) {
        cartRepository.decreaseQuantity(userId, item);
    }

    public void removeItem(String userId, CartModel item) {
        cartRepository.removeItem(userId, item);
    }

    public void clearCart(String userId) {
        cartRepository.clearCart(userId);
    }

    public LiveData<Boolean> placeOrder(OrderModel order) {
        return cartRepository.placeOrder(order);
    }
}