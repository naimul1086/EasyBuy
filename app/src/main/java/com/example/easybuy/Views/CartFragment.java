package com.example.easybuy.Views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Adapters.CartAdapter;
import com.example.easybuy.Models.CartModel;
import com.example.easybuy.Models.OrderModel;
import com.example.easybuy.Models.WishlistModel;
import com.example.easybuy.R;
import com.example.easybuy.ViewModels.CartViewModel;
import com.example.easybuy.ViewModels.WishlistViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private MaterialButton btnCheckout, btnShopNow, btnApplyPromo;
    private TextView tvBottomTotal, tvSubtotal, tvShipping,
            tvDiscountAmount, tvTotal, tvItemCount;
    private EditText etPromoCode;
    private LinearLayout layoutEmptyCart, bottomBar;

    private CartViewModel cartViewModel;
    private WishlistViewModel wishlistViewModel;
    private CartAdapter cartAdapter;
    private String userId;
    private double discountAmount = 0.0;

    private static final String PROMO_CODE    = "SAVE10";
    private static final double PROMO_PERCENT = 0.10;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        bindViews(view);
        setupRecyclerView();
        setupViewModels();
        setupListeners();
        resolveUser();
        return view;
    }

    private void bindViews(View view) {
        cartRecyclerView  = view.findViewById(R.id.rvCartItems);
        btnCheckout       = view.findViewById(R.id.btnCheckout);
        btnShopNow        = view.findViewById(R.id.btnShopNow);
        btnApplyPromo     = view.findViewById(R.id.btnApplyPromo);
        tvBottomTotal     = view.findViewById(R.id.tvBottomTotal);
        tvSubtotal        = view.findViewById(R.id.tvSubtotal);
        tvShipping        = view.findViewById(R.id.tvShipping);
        tvDiscountAmount  = view.findViewById(R.id.tvDiscountAmount);
        tvTotal           = view.findViewById(R.id.tvTotal);
        tvItemCount       = view.findViewById(R.id.tvItemCount);
        etPromoCode       = view.findViewById(R.id.etPromoCode);
        layoutEmptyCart   = view.findViewById(R.id.layoutEmptyCart);
        bottomBar         = view.findViewById(R.id.bottomBar);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(getContext(), new ArrayList<>());
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartAdapter);

        cartAdapter.setCartActionListener(new CartAdapter.CartActionListener() {
            @Override
            public void onIncrease(CartModel item, int position) {
                cartViewModel.increaseQuantity(userId, item);
            }

            @Override
            public void onDecrease(CartModel item, int position) {
                cartViewModel.decreaseQuantity(userId, item);
            }

            @Override
            public void onDelete(CartModel item, int position) {
                cartViewModel.removeItem(userId, item);
                Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWishlist(CartModel item, int position) {
                wishlistViewModel.isWishlisted(userId, item.getProductId())
                        .observe(getViewLifecycleOwner(), alreadyAdded -> {
                            if (Boolean.TRUE.equals(alreadyAdded)) {
                                Toast.makeText(getContext(),
                                        "Already in Wishlist", Toast.LENGTH_SHORT).show();
                            } else {
                                WishlistModel w = new WishlistModel(
                                        "",
                                        item.getProductId(),
                                        userId,
                                        item.getImage(),
                                        item.getTitle(),
                                        item.getPrice(),
                                        item.getPrice(),
                                        ""
                                );
                                wishlistViewModel.addToWishlist(userId, w);
                                Toast.makeText(getContext(),
                                        "Added to Wishlist ", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void setupViewModels() {
        cartViewModel     = new ViewModelProvider(this).get(CartViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
    }

    private void resolveUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            observeCart();
        }
    }

    private void observeCart() {
        cartViewModel.getCartItems(userId).observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                showEmptyState(true);
            } else {
                showEmptyState(false);
                cartAdapter.updateCartItems(items);
                updateOrderSummary(items);
            }
        });
    }

    private void setupListeners() {
        btnApplyPromo.setOnClickListener(v -> applyPromoCode());
        btnCheckout.setOnClickListener(v -> placeOrder());
        btnShopNow.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });
    }

    private void applyPromoCode() {
        String code = etPromoCode.getText().toString().trim().toUpperCase();
        List<CartModel> items = cartAdapter.getCartItemList();
        double subtotal = calculateSubtotal(items);
        if (code.equals(PROMO_CODE)) {
            discountAmount = subtotal * PROMO_PERCENT;
            Toast.makeText(getContext(), "Promo applied! 10% off ", Toast.LENGTH_SHORT).show();
        } else {
            discountAmount = 0.0;
            Toast.makeText(getContext(), "Invalid promo code", Toast.LENGTH_SHORT).show();
        }
        updateOrderSummary(items);
    }

    private void updateOrderSummary(List<CartModel> items) {
        double subtotal = calculateSubtotal(items);
        double total    = subtotal - discountAmount;
        int count       = items != null ? items.size() : 0;

        tvItemCount.setText(count + (count == 1 ? " Item" : " Items"));
        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvShipping.setText("Free");
        tvDiscountAmount.setText(String.format("- $%.2f", discountAmount));
        tvTotal.setText(String.format("$%.2f", total));
        tvBottomTotal.setText(String.format("$%.2f", total));
    }

    private double calculateSubtotal(List<CartModel> items) {
        if (items == null) return 0;
        double total = 0;
        for (CartModel item : items) total += item.getPrice() * item.getQuantity();
        return total;
    }

    private void placeOrder() {
        List<CartModel> items = cartAdapter.getCartItemList();
        if (items == null || items.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        double subtotal = calculateSubtotal(items);
        double total    = subtotal - discountAmount;
        String promo    = etPromoCode.getText().toString().trim();

        OrderModel order = new OrderModel(
                "",
                userId,
                new ArrayList<>(items),
                subtotal,
                0.0,
                discountAmount,
                total,
                "pending",
                System.currentTimeMillis(),
                promo
        );

        cartViewModel.placeOrder(order).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                cartViewModel.clearCart(userId);
                showOrderSuccessDialog(order.getOrderId(), total);
            } else {
                Toast.makeText(getContext(), "Order failed. Try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOrderSuccessDialog(String orderId, double total) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_order_success);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        View dialogView = dialog.getWindow().getDecorView();

        TextView tvOrderId    = dialogView.findViewById(R.id.tvDialogOrderId);
        TextView tvOrderTotal = dialogView.findViewById(R.id.tvDialogTotal);
        MaterialButton btnDone = dialogView.findViewById(R.id.btnDialogDone);

        if (tvOrderId != null && orderId != null && orderId.length() >= 8)
            tvOrderId.setText("Order #" + orderId.substring(0, 8).toUpperCase());
        if (tvOrderTotal != null)
            tvOrderTotal.setText(String.format("$%.2f", total));

        View card = dialogView.findViewById(R.id.dialogCard);
        if (card != null) {
            card.setAlpha(0f);
            card.setTranslationY(100f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(450)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        try {
            com.airbnb.lottie.LottieAnimationView lottie =
                    dialogView.findViewById(R.id.lottieSuccess);
            if (lottie != null) lottie.playAnimation();
        } catch (NoClassDefFoundError ignored) {}

        if (btnDone != null) {
            btnDone.setOnClickListener(v -> {
                dialog.dismiss();
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }

        dialog.show();
    }

    private void showEmptyState(boolean empty) {
        layoutEmptyCart.setVisibility(empty ? View.VISIBLE : View.GONE);
        cartRecyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        bottomBar.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
