package com.example.dispenser.ui.home;

import androidx.lifecycle.ViewModel;

import com.example.dispenser.data.AuthRemoteDataSource;
import com.example.dispenser.data.AuthRepository;
import com.example.dispenser.data.AuthRepositoryImpl;

public class ProfileViewModel extends ViewModel {
    private AuthRemoteDataSource authRemoteDataSource=new AuthRemoteDataSource();
    private AuthRepositoryImpl authRepository = AuthRepositoryImpl.getInstance(authRemoteDataSource);

    public void logout() {
        authRepository.logout();
    }
}