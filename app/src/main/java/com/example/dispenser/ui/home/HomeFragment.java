package com.example.dispenser.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dispenser.R;
import com.example.dispenser.data.DispenserUtility;
import com.example.dispenser.data.model.Dispenser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView deviceName;
    private TextView nameLiquidTankA;
    private TextView nameLiquidTankB;
    private TextView volumeLiquidTankA;
    private TextView volumeLiquidTankB;
    private ImageView checklist;

    private HomeViewModel mViewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceName = view.findViewById(R.id.tvDeviceName);
        volumeLiquidTankA = view.findViewById(R.id.tvLiquidPercentA);
        volumeLiquidTankB=view.findViewById(R.id.tvLiquidPercentB);
        nameLiquidTankA=view.findViewById(R.id.tvLabelA);
        nameLiquidTankB=view.findViewById(R.id.tvLabelB);
        checklist=view.findViewById(R.id.imageCheckHome);
    }
    private void updateDispenserUI(Dispenser dispenser) {
        if (dispenser == null) return;
        String dispenserStatus = DispenserUtility.getStatus(dispenser.getStatus());
        // Checklist/Status Icon
        if (dispenserStatus.equalsIgnoreCase("Available") || dispenser.getUserId() != null) {
            checklist.setImageResource(R.drawable.check);
        } else {
            // Misalnya set ke icon lain jika sedang In Use oleh user lain
            // checklist.setImageResource(R.drawable.uncheck);
        }
        nameLiquidTankA.setText("Liquid A: "+dispenser.getLiquidNameA());
        nameLiquidTankB.setText("Liquid B: "+dispenser.getLiquidNameB());
        volumeLiquidTankA.setText("Liquid: "+dispenser.getVolumeFilledA()+" %");
        volumeLiquidTankB.setText("Liquid: "+dispenser.getVolumeFilledB()+" %");
        deviceName.setText("Device Name: "+dispenser.getDeviceName());
        // Asumsi Tank A");
    }
    private void startRealtime(String deviceId) {
        mViewModel.listenDispenser(deviceId)
                .observe(getViewLifecycleOwner(), this::updateDispenserUI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MaterialCardView cardProfile = root.findViewById(R.id.cardProfile);
        MaterialCardView cardDevice=root.findViewById(R.id.cardDevice);
        HomeActivity main = (HomeActivity) getActivity();

        cardProfile.setOnClickListener(v -> {
            BottomNavigationView bottomNav = main.findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.navigation_profile); // ganti dengan menu tujuanmu
        });
        cardDevice.setOnClickListener(v->{
            BottomNavigationView bottomNav = main.findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.navigation_dispenser);

        });

//        Toolbar toolbar = root.findViewById(R.id.customToolbarHome);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
//
//        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        return root;
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        AppCompatActivity activity = (AppCompatActivity) requireActivity();
//        ActionBar actionBar = activity.getSupportActionBar();
//
//        if (actionBar != null) {
//            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setLogo(R.drawable.dispensericon); // ganti logomu
//            actionBar.setDisplayUseLogoEnabled(true);
//
//            actionBar.setDisplayShowTitleEnabled(false); // hilangin judul
//            actionBar.setDisplayHomeAsUpEnabled(false);  // hilangin tombol back
//        }
//    }

    private void showLastDispenser() {
//        mViewModel.getLastDispenser().observe(getViewLifecycleOwner(), dispenser -> {
//            if (dispenser != null){
//                startRealtime(dispenser.getDeviceId());
//            }
//        });
        String lastDispenserId = mViewModel.getDispenserLastId();
        if (lastDispenserId != null) {
            startRealtime(lastDispenserId);
        }

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
        showLastDispenser();

    }
}