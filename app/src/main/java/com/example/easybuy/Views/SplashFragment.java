package com.example.easybuy.Views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easybuy.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashFragment extends Fragment {

    ImageView logo;
    TextView creator;
    FirebaseAuth auth;

    public SplashFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logo = view.findViewById(R.id.splashlogo);
        creator = view.findViewById(R.id.admin);
        auth = FirebaseAuth.getInstance();

        Animation logoAnim = AnimationUtils.loadAnimation(getContext(), R.anim.logo_animation);
        Animation bounce = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_effect);
        Animation textAnim = AnimationUtils.loadAnimation(getContext(), R.anim.text_animation);

        logo.startAnimation(logoAnim);

        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.startAnimation(bounce);
                creator.startAnimation(textAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                if (auth.getCurrentUser() != null) {
                    activity.onLoginSuccess();
                } else {
                    activity.switchFragment(new LoginFragment(), false);
                }
            }
        }, 2000);
    }
}
