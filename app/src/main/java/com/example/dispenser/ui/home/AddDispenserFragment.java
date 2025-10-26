package com.example.dispenser.ui.home;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.dispenser.R;
import com.example.dispenser.adapter.DispenserAdapter;
import com.example.dispenser.data.DispenserRemoteDataSource;
import com.example.dispenser.data.model.Dispenser;

import java.util.ArrayList;
import java.util.List;

public class AddDispenserFragment extends DialogFragment {
    private AddDispenserViewModel viewModel;
    private  LiveData<List<Dispenser>> listDispenser;
    private int anchorX; // posisi X tombol
    private int anchorY;
    private int height;
    private int width;

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private ArrayList<Dispenser>listData=new ArrayList<>();
    private AddDispenserViewModel mViewModel;
    RecyclerView recyclerViewContainer;

    public AddDispenserFragment(int anchorX, int anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_dispenser, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddDispenserViewModel.class);

        DispenserAdapter adapter=new DispenserAdapter(getActivity(), listData);

        recyclerViewContainer=view.findViewById(R.id.recyclerViewContainer);
        recyclerViewContainer.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewContainer.setAdapter(adapter);
        viewModel.listDispenser().observe(getViewLifecycleOwner(), dispensers -> {
            listData.clear();
            if (dispensers != null) {
                listData.addAll(dispensers);
            }
            adapter.notifyDataSetChanged();

        });
        adapter.setOnDispenserClickListener(dispenser -> {
            // Contoh: tampilkan log atau tutup dialog
            Log.d("AddDispenserFragment", "Dipilih: " + dispenser.getDeviceName());
            Bundle result = new Bundle();
            if(dispenser.getStatus().equalsIgnoreCase("Available")){
                result.putString("dispenserName", dispenser.getDeviceName()); // contoh field
                result.putString("dispenserStatus", dispenser.getStatus());
                result.putString("waterLevel", dispenser.getWaterlevel());

                dispenser.setStatus("Unavailable");
                viewModel.updateDispenser(dispenser);
                adapter.notifyDataSetChanged();
                getParentFragmentManager().setFragmentResult("selected_dispenser", result);

                dismiss();

            }


            // Misal: kirim data balik ke activity/fragment lain
            // atau cukup tutup dialog
            dismiss();

            // Bisa juga pakai callback ke fragment/aktivitas utama
        });



    }
    public void prepareData(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_TITLE, R.style.PopupAnimation);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();

            // bikin background transparan
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // set posisi
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = anchorX;
//            params.y = anchorY;
            params.y = anchorY-100;
//            params.width=width;
//            params.height=height;
            window.setAttributes(params);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddDispenserViewModel.class);
        // TODO: Use the ViewModel
    }

}