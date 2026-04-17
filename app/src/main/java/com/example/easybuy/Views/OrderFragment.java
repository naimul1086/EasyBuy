package com.example.easybuy.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Adapters.OrderAdapter;

import com.example.easybuy.Models.OrderModel;
import com.example.easybuy.R;
import com.example.easybuy.ViewModels.OrderViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFragment extends Fragment {

    private RecyclerView rvOrders;
    private LinearLayout layoutEmptyOrders;
    private TextView tvOrderCount;
    private ChipGroup chipGroupFilter;
    private MaterialButton btnStartShopping;

    private OrderViewModel ordersViewModel;
    private OrderAdapter ordersAdapter;
    private String userId;
    private List<OrderModel> allOrders = new ArrayList<>();
    private String activeFilter = "all";

    public OrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        bindViews(view);
        setupRecyclerView();
        setupChipFilter();
        resolveUser();

        btnStartShopping.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        return view;
    }

    private void bindViews(View view) {
        rvOrders          = view.findViewById(R.id.rvOrders);
        layoutEmptyOrders = view.findViewById(R.id.layoutEmptyOrders);
        tvOrderCount      = view.findViewById(R.id.tvOrderCount);
        chipGroupFilter   = view.findViewById(R.id.chipGroupFilter);
        btnStartShopping  = view.findViewById(R.id.btnStartShopping);
    }

    private void setupRecyclerView() {
        ordersAdapter = new OrderAdapter(getContext(), new ArrayList<>());
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(ordersAdapter);

        ordersAdapter.setOrderClickListener(order -> {
            Toast.makeText(getContext(),
                    "Order: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupChipFilter() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll)          activeFilter = "all";
            else if (id == R.id.chipPending)     activeFilter = "pending";
            else if (id == R.id.chipProcessing)  activeFilter = "processing";
            else if (id == R.id.chipDelivered)   activeFilter = "delivered";
            else if (id == R.id.chipCancelled)   activeFilter = "cancelled";
            applyFilter();
        });
    }

    private void resolveUser() {
        ordersViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            ordersViewModel.getOrders(userId).observe(getViewLifecycleOwner(), orders -> {
                allOrders = orders != null ? orders : new ArrayList<>();
                applyFilter();
            });
        }
    }

    private void applyFilter() {
        List<OrderModel> filtered;
        if (activeFilter.equals("all")) {
            filtered = allOrders;
        } else {
            final String f = activeFilter;
            filtered = allOrders.stream()
                    .filter(o -> f.equalsIgnoreCase(o.getStatus()))
                    .collect(Collectors.toList());
        }

        if (filtered.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            ordersAdapter.updateOrders(filtered);
        }

        int total = allOrders.size();
        tvOrderCount.setText(total + (total == 1 ? " order placed" : " orders placed"));
    }

    private void showEmptyState(boolean empty) {
        layoutEmptyOrders.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvOrders.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
