package com.example.dispenser.ui.schedule.list_schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.data.model.ScheduleRTDB;
import com.example.dispenser.ui.schedule.ScheduleActivity;
import com.example.dispenser.ui.schedule.ScheduleController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private com.google.android.material.button.MaterialButton btnRemoveConfirm;
    private FloatingActionButton fabAdd;
    private String deviceId = "DEVICE123";

    public ScheduleListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        scheduleController = new ScheduleController(this.getActivity().getApplication());
        recyclerView = view.findViewById(R.id.rv_schedules);
        btnRemoveConfirm = view.findViewById(R.id.btn_remove_confirm);
        fabAdd = view.findViewById(R.id.fab_add_schedule);

        dbRef = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("dispenser/" + deviceId + "/schedules");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- KONFIGURASI ADAPTER ---
        adapter = new ScheduleAdapter(scheduleList, new ScheduleAdapter.OnScheduleClickListener() {
            @Override
            public void onEditClick(int position, ScheduleRTDB schedule) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                intent.putExtra("SLOT_INDEX", Integer.parseInt(schedule.getKey()));
                startActivity(intent);
            }

            @Override
            public void onToggleActive(int position, boolean isActive) {
                dbRef.child(scheduleList.get(position).getKey()).child("enabled").setValue(isActive ? 1 : 0);
            }
        });

        // --- LOGIKA MULTI-SELECT (ALARM STYLE) ---
        adapter.setOnSelectionModeListener(new ScheduleAdapter.OnSelectionModeListener() {
            @Override
            public void onSelectionChanged(int count) {
                // Update teks tombol sesuai jumlah yang dicentang
                btnRemoveConfirm.setText("Delete Selected (" + count + ")");
            }

            @Override
            public void onSelectionModeToggle(boolean active) {
                if (active) {
                    btnRemoveConfirm.setVisibility(View.VISIBLE);
                    if (fabAdd != null) fabAdd.hide(); // Sembunyikan tombol tambah
                    vibrate(100);
                } else {
                    btnRemoveConfirm.setVisibility(View.GONE);
                    if (fabAdd != null) fabAdd.show(); // Munculkan kembali tombol tambah
                }
            }
        });

        // --- EKSEKUSI HAPUS MASSAL ---
        btnRemoveConfirm.setOnClickListener(v -> {
            List<ScheduleRTDB> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) return;

            for (ScheduleRTDB item : selectedItems) {
                dbRef.child(item.getKey()).removeValue();
            }

            Toast.makeText(getContext(), selectedItems.size() + " Jadwal dihapus", Toast.LENGTH_SHORT).show();
            adapter.exitSelectionMode(); // Kembali ke tampilan normal
        });

        recyclerView.setAdapter(adapter);
        loadSchedulesFromFirebase();

        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> startActivity(new Intent(getActivity(), ScheduleActivity.class)));
        }

        return view;
    }

    private void vibrate(int duration) {
        android.os.Vibrator v = (android.os.Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
        if (v != null) v.vibrate(duration);
    }

    private void loadSchedulesFromFirebase() {
        disposables.add(
                scheduleController.getAllSchedulesRx(deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(schedules -> {
                            scheduleList.clear();
                            scheduleList.addAll(schedules);
                            adapter.notifyDataSetChanged();
                        }, throwable -> {
                            Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}