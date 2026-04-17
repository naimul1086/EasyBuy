package com.example.easybuy.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Models.OrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderRepo {

    private final DatabaseReference db;

    public OrderRepo() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    public LiveData<List<OrderModel>> getOrders(String userId) {
        MutableLiveData<List<OrderModel>> ordersLiveData = new MutableLiveData<>();
        db.child("orders")
                .orderByChild("userId")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<OrderModel> list = new ArrayList<>();
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                OrderModel order = child.getValue(OrderModel.class);
                                if (order != null) list.add(order);
                            }
                            list.sort((a, b) -> Long.compare(b.getOrderTimestamp(),
                                    a.getOrderTimestamp()));
                        }
                        ordersLiveData.setValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ordersLiveData.setValue(new ArrayList<>());
                    }
                });
        return ordersLiveData;
    }
}