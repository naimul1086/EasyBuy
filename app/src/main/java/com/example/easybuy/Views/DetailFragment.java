package com.example.easybuy.Views;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.easybuy.Adapters.DetailsViewPagerAdapter;
import com.example.easybuy.Models.CartModel;
import com.example.easybuy.Models.ProductModel;
import com.example.easybuy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailFragment extends Fragment {

    private ViewPager2 imageViewPager;
    private TabLayout imageIndicator;

    private TextView tvTitle, tvBrand, tvSku, tvCategory;
    private TextView tvRating, tvRatingCount;
    private TextView tvPrice, tvOriginalPrice, tvDiscount;
    private TextView tvStock;
    private TextView tvQuantity;
    private TextView tvTotalPrice;
    private TextView tvShipping, tvWarranty, tvReturnPolicy;
    private TextView tvDescription, tvSeeMore;
    private ChipGroup chipGroupTags;
    private ImageButton btnBack, btnWishlist;
    private ImageButton btnDecrease, btnIncrease;
    private MaterialButton btnAddToCart;

    private ProductModel product;
    private int quantity = 1;
    private boolean isDescriptionExpanded = false;
    FirebaseUser user;
    String userId;
    DatabaseReference cartDb;


    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        cartDb = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        if (bundle != null) {
            product = bundle.getParcelable("product");
            if (product != null) {
                bindData();
            }
        }

        setupListeners();
    }

    private void initViews(View view) {
        imageViewPager   = view.findViewById(R.id.imageViewPager);
        imageIndicator   = view.findViewById(R.id.imageIndicator);

        tvTitle          = view.findViewById(R.id.tvTitle);
        tvBrand          = view.findViewById(R.id.tvBrand);
        tvSku            = view.findViewById(R.id.tvSku);
        tvCategory       = view.findViewById(R.id.tvCategory);
        tvRating         = view.findViewById(R.id.tvRating);
        tvRatingCount    = view.findViewById(R.id.tvRatingCount);
        tvPrice          = view.findViewById(R.id.tvPrice);
        tvOriginalPrice  = view.findViewById(R.id.tvOriginalPrice);
        tvDiscount       = view.findViewById(R.id.tvDiscount);
        tvStock          = view.findViewById(R.id.tvStock);
        tvQuantity       = view.findViewById(R.id.tvQuantity);
        tvTotalPrice     = view.findViewById(R.id.tvTotalPrice);
        tvShipping       = view.findViewById(R.id.tvShipping);
        tvWarranty       = view.findViewById(R.id.tvWarranty);
        tvReturnPolicy   = view.findViewById(R.id.tvReturnPolicy);
        tvDescription    = view.findViewById(R.id.tvDescription);
        tvSeeMore        = view.findViewById(R.id.tvSeeMore);
        chipGroupTags    = view.findViewById(R.id.chipGroupTags);
        btnBack          = view.findViewById(R.id.btnBack);
        btnWishlist      = view.findViewById(R.id.btnWishlist);
        btnDecrease      = view.findViewById(R.id.btnDecrease);
        btnIncrease      = view.findViewById(R.id.btnIncrease);
        btnAddToCart     = view.findViewById(R.id.btnAddToCart);

        tvOriginalPrice.setPaintFlags(
                tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );
    }

    private void bindData() {
        if (product.images != null && !product.images.isEmpty()) {
            DetailsViewPagerAdapter imageAdapter = new DetailsViewPagerAdapter(
                    requireContext(), product.images
            );
            imageViewPager.setAdapter(imageAdapter);
            new TabLayoutMediator(imageIndicator, imageViewPager,
                    (tab, position) -> {}).attach();
        }

        tvTitle.setText(product.title);
        tvCategory.setText(product.category);
        tvBrand.setText("Brand: " + (product.brand != null ? product.brand : "N/A"));
        tvSku.setText("SKU: " + product.sku);

        tvRating.setText(String.valueOf(product.rating));
        tvRatingCount.setText("(Reviews)");

        double discountedPrice = product.price * (1 - product.discountPercentage / 100);
        tvPrice.setText(String.format("$%.2f", discountedPrice));
        tvOriginalPrice.setText(String.format("$%.2f", product.price));
        tvDiscount.setText(String.format("%.0f%% OFF", product.discountPercentage));

        updateTotalPrice(discountedPrice);

        if (product.stock > 0) {
            tvStock.setText("In Stock");
            tvStock.setTextColor(getResources().getColor(R.color.stock_green, null));
        } else {
            tvStock.setText("Out of Stock");
            tvStock.setTextColor(getResources().getColor(R.color.stock_red, null));
        }

        tvShipping.setText(product.shippingInformation != null
                ? product.shippingInformation : "Standard Shipping");
        tvWarranty.setText(product.warrantyInformation != null
                ? product.warrantyInformation : "No Warranty");
        tvReturnPolicy.setText(product.returnPolicy != null
                ? product.returnPolicy : "No Returns");

        tvDescription.setText(product.description);
        tvDescription.setMaxLines(3);

        if (product.tags != null && !product.tags.isEmpty()) {
            chipGroupTags.removeAllViews();
            for (String tag : product.tags) {
                Chip chip = new Chip(requireContext());
                chip.setText("#" + tag);
                chip.setClickable(false);
                chip.setCheckable(false);
                chipGroupTags.addView(chip);
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnWishlist.setOnClickListener(v -> {
            btnWishlist.setImageResource(R.drawable.ic_heart_filled);
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice(getCurrentDiscountedPrice());
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (product != null && quantity < product.stock) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice(getCurrentDiscountedPrice());
            }
        });

        tvSeeMore.setOnClickListener(v -> {
            if (isDescriptionExpanded) {
                tvDescription.setMaxLines(3);
                tvSeeMore.setText("See more");
            } else {
                tvDescription.setMaxLines(Integer.MAX_VALUE);
                tvSeeMore.setText("See less");
            }
            isDescriptionExpanded = !isDescriptionExpanded;
        });

        btnAddToCart.setOnClickListener(v -> {
            if (product == null || userId == null) return;

            cartDb.child("cart").child(userId).child(String.valueOf(product.id))
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            int existingQty = snapshot.child("quantity").getValue(Integer.class);
                            int newQty = existingQty + quantity;
                            cartDb.child("cart").child(userId)
                                    .child(String.valueOf(product.id))
                                    .child("quantity").setValue(newQty);
                            Toast.makeText(requireContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                            btnAddToCart.setText("Added to Cart");
                            btnAddToCart.setEnabled(false);
                            btnAddToCart.setBackgroundColor(getResources().getColor(R.color.stock_green, null));
                            btnAddToCart.setTextColor(getResources().getColor(R.color.white, null));
                            btnAddToCart.setOnClickListener(null);

                        } else {

                            double discountedPrice = product.price *
                                    (1 - product.discountPercentage / 100);

                            String thumbnail = (product.images != null
                                    && !product.images.isEmpty())
                                    ? product.images.get(0)
                                    : product.thumbnail;

                            CartModel cartItem = new CartModel(
                                    String.valueOf(product.id),
                                    String.valueOf(product.id),
                                    userId,
                                    thumbnail,
                                    product.title,
                                    discountedPrice,
                                    quantity
                            );

                            cartDb.child("cart").child(userId)
                                    .child(String.valueOf(product.id))
                                    .setValue(cartItem);
                            btnAddToCart.setText("Added to Cart");
                            btnAddToCart.setEnabled(false);
                            btnAddToCart.setBackgroundColor(getResources().getColor(R.color.stock_green, null));
                            btnAddToCart.setTextColor(getResources().getColor(R.color.white, null));
                            btnAddToCart.setOnClickListener(null);
                            Toast.makeText(requireContext(), "Added to Cart", Toast.LENGTH_SHORT).show();

                        }
                    });
        });
    }



    
    private double getCurrentDiscountedPrice() {
        if (product == null) return 0;
        return product.price * (1 - product.discountPercentage / 100);
    }

    private void updateTotalPrice(double unitPrice) {
        double total = unitPrice * quantity;
        tvTotalPrice.setText(String.format("$%.2f", total));
    }
}
