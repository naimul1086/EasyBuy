package com.example.easybuy.Repository;

import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Firebase.AuthResult;
import com.example.easybuy.Models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAuthRepo {

    private final FirebaseAuth auth;
    private DatabaseReference db;


    public FirebaseAuthRepo() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }
    public void login(String email, String password,
                      MutableLiveData<AuthResult> resultLiveData) {

        resultLiveData.setValue(AuthResult.loading());

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultLiveData.setValue(AuthResult.success());
                    } else {
                        String msg = getErrorMessage(task.getException());
                        resultLiveData.setValue(AuthResult.error(msg));
                    }
                });
    }

    public void signUp(String email, String password,
                       MutableLiveData<AuthResult> resultLiveData) {

        resultLiveData.setValue(AuthResult.loading());

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        UserModel user = new UserModel(email,password,userId);
                        db.child("users").child(userId).setValue(user)
                                .addOnCompleteListener(dbtask->{
                                    if(dbtask.isSuccessful()){
                                        resultLiveData.setValue(AuthResult.success());
                                    }else{
                                        resultLiveData.setValue(AuthResult.error("User Saved Failed"));
                                    }
                                });

                    } else {
                        String msg = getErrorMessage(task.getException());
                        resultLiveData.setValue(AuthResult.error(msg));
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }


    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            return "Email or password Incorrect";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            return "Already Have an Account!";
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            return "Password Atleast 6 characters";
        } else if (exception != null) {
            return exception.getMessage();
        }
        return "Unknown error occurred.";
    }
}