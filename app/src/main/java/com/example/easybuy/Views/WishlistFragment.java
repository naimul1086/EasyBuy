package com.example.easybuy.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easybuy.Adapters.WishlistAdapter;
import com.example.easybuy.R;
import com.example.easybuy.ViewModels.WishlistViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class WishlistFragment extends Fragment {

    private RecyclerView rvWishlist;
    private LinearLayout layoutEmptyWishlist;
    private TextView tvItemCount;

    private WishlistViewModel wishlistViewModel;
    private WishlistAdapter wishlistAdapter;
    private String userId;

    public WishlistFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        rvWishlist          = view.findViewById(R.id.rvWishlistItems);
        layoutEmptyWishlist = view.findViewById(R.id.layoutEmptyWishlist);
        tvItemCount         = view.findViewById(R.id.tvItemCount);

        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);

        wishlistAdapter = new WishlistAdapter(getContext(), new ArrayList<>());
        rvWishlist.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWishlist.setAdapter(wishlistAdapter);

        // Remove listener — passes userId down
        wishlistAdapter.setRemoveListener((item, position) ->
                wishlistViewModel.removeFromWishlist(userId, item));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();

            wishlistViewModel.getWishlistItems(userId)
                    .observe(getViewLifecycleOwner(), items -> {
                        if (items == null || items.isEmpty()) {
                            layoutEmptyWishlist.setVisibility(View.VISIBLE);
                            rvWishlist.setVisibility(View.GONE);
                            tvItemCount.setText("0 Items");
                        } else {
                            layoutEmptyWishlist.setVisibility(View.GONE);
                            rvWishlist.setVisibility(View.VISIBLE);
                            wishlistAdapter.updateItems(items);
                            tvItemCount.setText(items.size() + (items.size() == 1 ? " Item" : " Items"));
                        }
                    });
        }

        return view;
    }
}