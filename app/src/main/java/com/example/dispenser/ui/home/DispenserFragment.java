package com.example.dispenser.ui.home;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispenser.R;
import com.example.dispenser.ui.dispenser.DispenserDetailActivity;

public class DispenserFragment extends Fragment {

    private DispenserViewModel mViewModel;

    public static DispenserFragment newInstance() {
        return new DispenserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dispenser, container, false);
        Toolbar toolbar = root.findViewById(R.id.customToolbar);
//        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
//            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
//            v.setPadding(
//                    v.getPaddingLeft(),
//                    topInset, // tambahkan padding atas sesuai tinggi status bar / notch
//                    v.getPaddingRight(),
//                    v.getPaddingBottom()
//            );
//            return insets;
//        });
        // baru cari toolbar di dalam includeView
        ImageButton addButton = toolbar.findViewById(R.id.addHome);

        addButton.setOnClickListener(view -> {
            // Tambahkan logika untuk tombol "Add" di sini
            int[] location = new int[2];
            view.getLocationOnScreen(location);

            int anchorX = location[0];
            int anchorY = location[1] + view.getHeight();

            Toast.makeText(requireContext(), "WOI MASOK.", Toast.LENGTH_LONG).show();

            AddDispenserFragment listDispenserFragment = new AddDispenserFragment(anchorX, anchorY);
            int width = view.getWidth();
            int height = view.getHeight();
            listDispenserFragment.setWidth(width);
            listDispenserFragment.setHeight(height);

            // Tampilkan dialog fragment
            listDispenserFragment.show(getParentFragmentManager(), "AddDispenser");
        });

        View cardDispenser=root.findViewById(R.id.fragment_container);
        cardDispenser.setOnClickListener(v->{
            Intent intent=new Intent(getActivity(), DispenserDetailActivity.class);
            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Dengarkan hasil dari AddDispenserFragment
        getParentFragmentManager().setFragmentResultListener("selected_dispenser", this,
                (requestKey, bundle) -> {
                    String dispenserName = bundle.getString("dispenserName");
                    String dispenserStatus = bundle.getString("dispenserStatus");
                    String waterlevel = bundle.getString("waterLevel");
                    ImageView checklist=view.findViewById(R.id.imgCheck);
                    if (dispenserStatus.equalsIgnoreCase("unavailable")) {
                        checklist.setImageResource(R.drawable.notcheck);
                    }
                    TextView deviceNameUi = view.findViewById(R.id.deviceName);
                    TextView deviceInUseUi = view.findViewById(R.id.deviceInUse);
                    TextView deviceWaterLevelUi=view.findViewById(R.id.water_level);
                    deviceNameUi.setText("Device Name: " + dispenserName);
                    deviceInUseUi.setText("Device in Use:");
                    deviceWaterLevelUi.setText("Remaining Liquid: "+waterlevel+" ml");

                    Log.d("HomeFragment", "Dipilih: " + dispenserName + " (ID: " + dispenserStatus + ")");

                    // Lakukan sesuatu, misalnya tampilkan di UI
                    // textViewSelectedDispenser.setText(dispenserName);
                });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DispenserViewModel.class);
        // TODO: Use the ViewModel
    }

}