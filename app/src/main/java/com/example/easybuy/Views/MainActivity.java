package com.example.easybuy.Views;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.easybuy.R;
import com.example.easybuy.ViewModels.CartViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    Toolbar toolbar;
    public BottomNavigationView bottomNavigationView;
    ImageView cart, notification, logo;
    FrameLayout cartBadgeContainer;
    TextView cartBadge;
    FirebaseAuth auth;
    CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        appBarLayout         = findViewById(R.id.appbar);
        toolbar              = findViewById(R.id.toolbar);
        cart                 = findViewById(R.id.cartIcon);
        notification         = findViewById(R.id.notificationIcon);
        logo                 = findViewById(R.id.logo);
        cartBadgeContainer   = findViewById(R.id.cartBadgeContainer);
        cartBadge            = findViewById(R.id.cartBadge);
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);

        auth = FirebaseAuth.getInstance();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            switchFragment(new SplashFragment(), false);
        } else {
            Fragment currentFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.framelayout);

            if (currentFragment != null) {
                boolean shouldHide = (currentFragment instanceof LoginFragment)
                        || (currentFragment instanceof SignupFragment)
                        || (currentFragment instanceof SplashFragment)
                        || (currentFragment instanceof DetailFragment);

                appBarLayout.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
                bottomNavigationView.setVisibility(shouldHide ? View.GONE : View.VISIBLE);

                if (currentFragment instanceof HomeFragment) {
                    bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                } else if (currentFragment instanceof ProductFragment) {
                    bottomNavigationView.getMenu().findItem(R.id.product).setChecked(true);
                } else if (currentFragment instanceof OrderFragment) {
                    bottomNavigationView.getMenu().findItem(R.id.order).setChecked(true);
                } else if (currentFragment instanceof ProfileFragment) {
                    bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.home) {
                switchFragment(new HomeFragment(), false);
            } else if (id == R.id.product) {
                switchFragment(new ProductFragment(), false);
            } else if (id == R.id.order) {
                switchFragment(new OrderFragment(), false);
            } else if (id == R.id.profile) {
                switchFragment(new ProfileFragment(), false);
            } else if (id == R.id.wishlist) {
                switchFragment(new WishlistFragment(), false);
            }
            return true;
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            cartViewModel.getCartItemCount(userId).observe(this, count -> {
                setCartBadgeCount(count);
            });
        }


        cart.setOnClickListener(view ->
                switchFragment(new CartFragment(), false));

        notification.setOnClickListener(view ->
                switchFragment(new NotificationFragment(), false));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFragment = getSupportFragmentManager()
                        .findFragmentById(R.id.framelayout);

                if (currentFragment instanceof HomeFragment) {
                    finish();
                } else if (currentFragment instanceof SplashFragment
                        || currentFragment instanceof LoginFragment
                        || currentFragment instanceof SignupFragment) {
                    finish();
                } else if (currentFragment instanceof DetailFragment) {
                    switchFragment(new ProductFragment(), false);
                } else {
                    switchFragment(new HomeFragment(), false);
                }
            }
        });
    }

    public void setCartBadgeCount(int count) {
        if (count > 0) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(View.VISIBLE);
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        boolean shouldHide = (fragment instanceof LoginFragment)
                || (fragment instanceof SignupFragment)
                || (fragment instanceof SplashFragment)
                || (fragment instanceof DetailFragment);

        appBarLayout.setVisibility(shouldHide ? View.GONE : View.VISIBLE);
        bottomNavigationView.setVisibility(shouldHide ? View.GONE : View.VISIBLE);

        if (fragment instanceof HomeFragment) {
            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
        } else if (fragment instanceof ProductFragment) {
            bottomNavigationView.getMenu().findItem(R.id.product).setChecked(true);
        } else if (fragment instanceof OrderFragment) {
            bottomNavigationView.getMenu().findItem(R.id.order).setChecked(true);
        } else if (fragment instanceof ProfileFragment) {
            bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
        } else if (fragment instanceof WishlistFragment) {
            bottomNavigationView.getMenu().findItem(R.id.wishlist).setChecked(true);
        } else if (!shouldHide) {
            uncheckBottomNavItems();
        }

        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.framelayout, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void uncheckBottomNavItems() {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setCheckable(false);
            menu.getItem(i).setChecked(false);
            menu.getItem(i).setCheckable(true);
        }
    }

    public void onLoginSuccess() {
        bottomNavigationView.setSelectedItemId(R.id.home);
        switchFragment(new HomeFragment(), false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            CartViewModel cartViewModel = new ViewModelProvider(this)
                    .get(CartViewModel.class);
            cartViewModel.getCartItemCount(currentUser.getUid())
                    .observe(this, count -> setCartBadgeCount(count));
        }
    }

}
