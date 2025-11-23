package com.example.dispenser.data;

import com.example.dispenser.callback.CallbackLoginRegister;
import com.example.dispenser.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepositoryImpl {

    private static volatile AuthRepositoryImpl instance;

    private AuthRemoteDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    // private constructor : singleton access
    private AuthRepositoryImpl(AuthRemoteDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static AuthRepositoryImpl getInstance(AuthRemoteDataSource dataSource) {
        if (instance == null) {
            instance = new AuthRepositoryImpl(dataSource);
        }
        return instance;
    }

    public  boolean isLoggedIn() {
        return dataSource.isLogged();
    }

    public void logout() {
        dataSource.logout();
    }

    public void setLoggedInUser() {
        dataSource.setLogged();
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
    public FirebaseUser getUser(){
        return dataSource.getUser();
    }
    public void login(String username, String password, CallbackLoginRegister<Result<User>> callback) {
        // handle login
       dataSource.login(username, password,callback);
    }

    public void register(String email, String password, CallbackLoginRegister<Result<User>> callback) {
        dataSource.register(email, password, callback);
    }


}