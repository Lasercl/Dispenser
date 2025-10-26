package com.example.dispenser.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dispenser.R;
import com.example.dispenser.ui.FragmentDestinationMenu;
import com.example.dispenser.ui.add_dispenser.list_dispenser;
import com.example.dispenser.ui.login.LoginActivity;
import com.example.dispenser.ui.register.RegisterActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

//        if (savedInstanceState == null) {
//            replaceFragment(new HomeFragment());
//            bottomNav.setSelectedItemId(R.id.nav_home);
//        }

        // set listener untuk bottom nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewModel.setDestination(FragmentDestinationMenu.HOME);
                return true;
            } else if (id == R.id.nav_dispenser) {
                viewModel.setDestination(FragmentDestinationMenu.DISPENSER);
                return true;
            } else if (id == R.id.nav_profile) {
                viewModel.setDestination(FragmentDestinationMenu.PROFILE);
                return true;
            }
            return false;
        });
        viewModel.getCurrentDestination().observe(this, dest -> {
            if (dest == null) return;
            switch (dest) {
                case HOME:
                    replaceFragment(new HomeFragment());
                    break;
                case DISPENSER:
                    replaceFragment(new DispenserFragment());
                    break;
                case PROFILE:
                    replaceFragment(new ProfileFragment());
                    break;
            }
        });

    }
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentMenu, fragment)
                .commit();
    }
}