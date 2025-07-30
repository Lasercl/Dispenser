package com.example.dispenser.data;

import android.util.Log;

import com.example.dispenser.data.model.User;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class AuthRemoteDataSource {
    DatabaseReference rootDatabase= FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    public Result<User> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            User fakeUser =
                    new User(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<User> register(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            User user =
                    new User(
                            java.util.UUID.randomUUID().toString(),
                            username);
            user.setPassword(password);
            rootDatabase.setValue(user)
                    .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "✅ Data berhasil disimpan"))
                    .addOnFailureListener(e -> Log.e("FIREBASE", "❌ Gagal simpan data", e));

            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}