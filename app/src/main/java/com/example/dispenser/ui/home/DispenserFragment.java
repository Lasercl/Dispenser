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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispenser.R;
import com.example.dispenser.data.DispenserUtility;
import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.ui.dispenser.DispenserDetailActivity;
import com.example.dispenser.ui.schedule.ScheduleActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DispenserFragment extends Fragment {

    private DispenserViewModel mViewModel;
    private TextView categoryLabel;
    private TextView liquidAFill;
    private TextView liquidBFill;
    private TextView numberOfProduction;
    private TextView productionCompleted;
    private TextView remainingToComplete;
    private TextView liquidTankA;
    private TextView liquidTankB;
    private TextView deviceNameUi;
    private TextView deviceInUseUi;
    private TextView deviceWaterLevelUiA;
    private TextView deviceWaterLevelUiB;
    private TextView scheduleDate;
    private TextView scheduleClock;
    private ImageView checklist;



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



        Button schedule=root.findViewById(R.id.btn_create_schedule);
        schedule.setOnClickListener(view -> {
           Intent intent=new Intent(getActivity(), ScheduleActivity.class);
           startActivity(intent);
        });

        return root;
    }

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
    private void startRealtime(String deviceId) {
        mViewModel.listenDispenser(deviceId)
                .observe(getViewLifecycleOwner(), this::updateDispenserUI);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checklist = view.findViewById(R.id.imgCheck);
        deviceNameUi = view.findViewById(R.id.deviceName);
        deviceInUseUi = view.findViewById(R.id.deviceInUse);
        liquidAFill = view.findViewById(R.id.waterFilledLiquidA);
        liquidBFill=view.findViewById(R.id.waterFilledLiquidB);
        categoryLabel=view.findViewById(R.id.label_category_dispenser);
        deviceWaterLevelUiA = view.findViewById(R.id.numberTankLiquidA);
        deviceWaterLevelUiB = view.findViewById(R.id.numberTankLiquidB);
        scheduleDate = view.findViewById(R.id.scheduleDate);
        scheduleClock = view.findViewById(R.id.scheduleClock);
        numberOfProduction=view.findViewById(R.id.numberOfProduction);
        productionCompleted=view.findViewById(R.id.productionCompleted);
        remainingToComplete=view.findViewById(R.id.remainingToComplete);
        liquidTankA=view.findViewById(R.id.nameLiquidTankA);
        liquidTankB=view.findViewById(R.id.nameLiquidTankB);







        // Dengarkan hasil dari AddDispenserFragment
        getParentFragmentManager().setFragmentResultListener("selected_dispenser", this,
                (requestKey, bundle) -> {
                    Dispenser dispenser=bundle.getParcelable(AddDispenserFragment.DISPENSER_SELECTED);

                    updateDispenserUI(dispenser);


                    mViewModel.saveLastUsedDispenser(dispenser.getDeviceId());

                    Log.d("HomeFragment", "Dipilih: " + dispenser.getDeviceName());
                });

    }
    private void updateDispenserUI(Dispenser dispenser) {
        if (dispenser == null) return;
        String liquidA=dispenser.getLiquidNameA()+ ": "+dispenser.getVolumeFilledA()+" ml";
        String liquidB=dispenser.getLiquidNameB()+ ": "+dispenser.getVolumeFilledB()+" ml";
        String categoryName=dispenser.getCategory();
        String dispenserName = dispenser.getDeviceName();
        String dispenserStatus = DispenserUtility.getStatus(dispenser.getStatus());
        int waterlevelTankA = dispenser.getWaterLevelTankA();

        // Checklist/Status Icon
        if (dispenserStatus.equalsIgnoreCase("Available") || dispenser.getUserId() != null) {
            checklist.setImageResource(R.drawable.check);
        } else {
            // Misalnya set ke icon lain jika sedang In Use oleh user lain
            // checklist.setImageResource(R.drawable.uncheck);
        }

        // Update TextViews

        deviceNameUi.setText("Device Name: " + dispenserName);
        deviceInUseUi.setText("Device in Use:");
        scheduleDate.setText(getScheduleDate(dispenser.getTimeStart()));
        scheduleClock.setText(getScheduleTime(dispenser.getTimeStart()));
        liquidAFill.setText(liquidA);
        liquidBFill.setText(liquidB);
        categoryLabel.setText("Category: "+categoryName);
        // Asumsi Tank A
        liquidTankA.setText(dispenser.getLiquidNameA());
        deviceWaterLevelUiA.setText(waterlevelTankA + " ml");
        // Asumsi Tank B
        liquidTankB.setText(dispenser.getLiquidNameB());
        deviceWaterLevelUiB.setText(dispenser.getWaterLevelTankB() + " ml");
        //production
        numberOfProduction.setText("Number of Production: "+dispenser.getBottleCount());
        productionCompleted.setText("Production Completed: "+dispenser.getCurrentBottle());
        remainingToComplete.setText("Remaining to complete: "+(dispenser.getBottleCount()-dispenser.getCurrentBottle()));
    }
    private String getScheduleDate(long timeStart) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timeStart));
    }
    private String getScheduleTime(long timeStart) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timeStart));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DispenserViewModel.class);
        // TODO: Use the ViewModel
        showLastDispenser();

    }

}