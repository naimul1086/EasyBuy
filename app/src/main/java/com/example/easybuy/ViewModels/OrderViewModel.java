package com.example.easybuy.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Models.OrderModel;
import com.example.easybuy.Repository.OrderRepo;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private final OrderRepo ordersRepo;

    public OrderViewModel() {
        ordersRepo = new OrderRepo();
    }

    public LiveData<List<OrderModel>> getOrders(String userId) {
        return ordersRepo.getOrders(userId);
    }
}