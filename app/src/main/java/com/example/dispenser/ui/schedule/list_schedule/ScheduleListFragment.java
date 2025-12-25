package com.example.dispenser.ui.schedule.list_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.data.model.ScheduleRTDB;
import com.example.dispenser.ui.schedule.ScheduleActivity;
import com.example.dispenser.ui.schedule.ScheduleController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ScheduleListFragment extends Fragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private ScheduleController scheduleController;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<ScheduleRTDB> scheduleList = new ArrayList<>();
    private DatabaseReference dbRef;
    private String deviceId = "DEVICE123"; // Sesuaikan dengan ID alatmu

    public ScheduleListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment (Pastikan di XML ada RecyclerView dan FAB)
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        scheduleController=new ScheduleController(this.getActivity().getApplication());
        recyclerView = view.findViewById(R.id.rv_schedules); // Sesuaikan ID di XML
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_schedule);
        dbRef = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("dispenser/" + deviceId + "/schedules");

        // 1. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Setup Adapter dengan Listener Klik
        adapter = new ScheduleAdapter(scheduleList, new ScheduleAdapter.OnScheduleClickListener() {
            @Override
            public void onEditClick(int position, ScheduleRTDB schedule) {
                // Buka ScheduleActivity untuk Edit, kirim Index-nya
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("SLOT_INDEX", Integer.parseInt(schedule.getKey()));
                startActivity(intent);
            }

            @Override
            public void onToggleActive(int position, boolean isActive) {
                // Update status On/Off langsung ke Firebase
                dbRef.child(String.valueOf(position)).child("enabled").setValue(isActive ? 1 : 0);
            }
        });

        recyclerView.setAdapter(adapter);

        // 3. Setup Firebase Reference

        // 4. Load Data
        loadSchedulesFromFirebase();

        // 5. FAB Click untuk tambah jadwal baru
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), ScheduleActivity.class));
            });
        }

        return view;
    }

    private void loadSchedulesFromFirebase() {
        disposables.add(
                scheduleController.getAllSchedulesRx(deviceId)
                        .subscribeOn(Schedulers.io()) // Ambil data di thread background
                        .observeOn(AndroidSchedulers.mainThread()) // Tampilkan di UI thread
                        .subscribe(schedules -> {
                            scheduleList.clear();
                            scheduleList.addAll(schedules);
                            adapter.notifyDataSetChanged();
                        }, throwable -> {
                            Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }
}