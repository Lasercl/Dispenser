package com.example.dispenser.data;

import com.example.dispenser.data.model.User;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepositoryImpl {

    private static volatile AuthRepositoryImpl instance;

    private AuthRemoteDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private User user = null;

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

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(User user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<User> login(String username, String password) {
        // handle login
        Result<User> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<User>) result).getData());
        }
        return result;
    }

    public Result<User> register(String username, String password) {
        Result<User> result = dataSource.register(username, password);
//        if (result instanceof Result.Success) {
//            setLoggedInUser(((Result.Success<User>) result).getData());
//        }
        return result;
    }
}