package com.example.easybuy.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.easybuy.Models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepo {
    private DatabaseReference db;

    public UserRepo() {
        db = FirebaseDatabase.getInstance().getReference();
    }
  public LiveData<UserModel> getUser(String userId){
        MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
        db.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    userLiveData.setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userLiveData.setValue(null);
            }
        });
        return userLiveData;
    }
}
