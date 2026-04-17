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


public class LoginFragment extends Fragment {
    MaterialButton loginbtn, signupbtn, forgotbtn;
    TextInputEditText loginMail, loginPass;
    AuthViewModel authViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginbtn = view.findViewById(R.id.login);
        signupbtn = view.findViewById(R.id.signupfromlogin);
        forgotbtn = view.findViewById(R.id.forgotpass);
        loginMail = view.findViewById(R.id.loginMail);
        loginPass = view.findViewById(R.id.loginPass);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);


        authViewModel.getAuthResult().observe(getViewLifecycleOwner(), authResult -> {
            if (authResult.getStatus() == AuthResult.Status.SUCCESS) {
                if (getActivity() instanceof MainActivity) {
                    Toast.makeText(getContext(),"Login successful!", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).onLoginSuccess();
                }
            } else if (authResult.getStatus() == AuthResult.Status.ERROR) {
                loginbtn.setEnabled(true);
                Toast.makeText(getContext(), authResult.getMessage(), Toast.LENGTH_SHORT).show();
            } else if (authResult.getStatus() == AuthResult.Status.LOADING) {
                loginbtn.setEnabled(false);
            }
        });

        loginbtn.setOnClickListener(v -> {
            String mail = loginMail.getText().toString();
            String pass = loginPass.getText().toString();
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                authViewModel.login(mail, pass);
            }
        });

        signupbtn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchFragment(new SignupFragment(), false);
            }
        });

        return view;
    }


}
