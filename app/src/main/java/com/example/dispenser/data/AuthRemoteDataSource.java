package com.example.dispenser.data;

import android.util.Log;

import com.example.dispenser.callback.CallbackLoginRegister;
import com.example.dispenser.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class AuthRemoteDataSource {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    DatabaseReference rootDatabase= FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    private String username;


    public void login(String email, String password,CallbackLoginRegister<Result<User>> callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        callback.onComplete(new Result.Success<>(new User(user.getUid(), email)));
                    } else {
                        callback.onComplete(new Result.Error(task.getException()));
                    }
                });
    }

    public void  register(String email, String password, CallbackLoginRegister<Result<User>> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        callback.onComplete(new Result.Success<>(new User(user.getUid(), email)));
                    } else {
                        callback.onComplete(new Result.Error(task.getException()));
                    }
                });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}