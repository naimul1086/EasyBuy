package com.example.easybuy.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Firebase.AuthResult;
import com.example.easybuy.Repository.FirebaseAuthRepo;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final FirebaseAuthRepo repo;
    private final MutableLiveData<AuthResult> authResultLiveData = new MutableLiveData<>();

    public AuthViewModel() {
        repo = new FirebaseAuthRepo();
    }

    public MutableLiveData<AuthResult> getAuthResult() {
        return authResultLiveData;
    }

    public void login(String email, String password) {
        repo.login(email, password, authResultLiveData);
    }

    public void signUp(String email, String password) {
        repo.signUp(email, password, authResultLiveData);
    }

    public void logout() {
        repo.logout();
    }

    public FirebaseUser getCurrentUser() {
        return repo.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return repo.isLoggedIn();
    }
}