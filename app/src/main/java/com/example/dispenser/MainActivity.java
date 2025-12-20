package com.example.dispenser;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.example.dispenser.data.AuthRemoteDataSource;
import com.example.dispenser.data.AuthRepositoryImpl;
import com.example.dispenser.ui.home.HomeActivity;
import com.example.dispenser.ui.login.LoginActivity;
import com.example.dispenser.ui.register.RegisterActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AuthRepositoryImpl authRepository=AuthRepositoryImpl.getInstance(new AuthRemoteDataSource());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        authRepository.setLoggedInUser();
        if(authRepository.isLoggedIn()){
            // Di MainActivity.java (atau sebelum masuk ke EditProfile)
            Map config = new HashMap();
            config.put("cloud_name", "dcvjtw1ig"); // Ganti ini!
            try {
                MediaManager.init(this, config);
            } catch (Exception e) {
                // MediaManager sudah diinisialisasi sebelumnya
            }
            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }else{
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }
}