package com.example.dispenser.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.AuthRemoteDataSource;
import com.example.dispenser.data.AuthRepository;
import com.example.dispenser.data.AuthRepositoryImpl;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private AuthRemoteDataSource authRemoteDataSource=new AuthRemoteDataSource();
    private AuthRepositoryImpl authRepository = AuthRepositoryImpl.getInstance(authRemoteDataSource);
    public FirebaseUser getCurrentUser(){
        return authRepository.getUser();
    }
    public void logout() {
        authRepository.logout();
    }
}