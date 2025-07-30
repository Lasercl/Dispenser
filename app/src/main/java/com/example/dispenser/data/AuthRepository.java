package com.example.dispenser.data;

import androidx.lifecycle.LiveData;

import com.example.dispenser.data.model.User;

public interface AuthRepository {
    LiveData<Result<User>> login(String email, String password);
    LiveData<Result<User>> register(String name, String email, String password);
}