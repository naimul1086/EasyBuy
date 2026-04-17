package com.example.easybuy.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.easybuy.Models.UserModel;
import com.example.easybuy.Repository.UserRepo;

public class UserViewModel extends ViewModel {
    private final UserRepo userRepository;

    public UserViewModel() {
        userRepository = new UserRepo();
    }

    public LiveData<UserModel> getUser(String userId) {
        return userRepository.getUser(userId);
    }
}
