package com.example.easybuy.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easybuy.Firebase.AuthResult;
import com.example.easybuy.R;
import com.example.easybuy.ViewModels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignupFragment extends Fragment {
    MaterialButton signupbtn, loginbtn;
    TextInputEditText signupMail, signupPass;
    AuthViewModel authViewModel;

    public SignupFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        signupbtn = view.findViewById(R.id.signup);
        loginbtn = view.findViewById(R.id.loginfromSignup);
        signupMail = view.findViewById(R.id.signupMail);
        signupPass = view.findViewById(R.id.signupinPass);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult.getStatus() == AuthResult.Status.SUCCESS) {
                Toast.makeText(getContext(), "Signup successful! Please login.", Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchFragment(new LoginFragment(), false);
                }
            } else if (authResult.getStatus() == AuthResult.Status.ERROR) {
                signupbtn.setEnabled(true);
                Toast.makeText(getContext(), authResult.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (authResult.getStatus() == AuthResult.Status.LOADING) {
                signupbtn.setEnabled(false);
            }
        });

        signupbtn.setOnClickListener(v -> {
            String mail = signupMail.getText().toString();
            String pass = signupPass.getText().toString();
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                authViewModel.signUp(mail, pass);
            }
        });

        loginbtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new LoginFragment(), false);
            }
        });

        return view;
    }
}
