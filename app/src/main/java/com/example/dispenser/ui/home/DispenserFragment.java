package com.example.dispenser.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dispenser.R;
import com.example.dispenser.data.DispenserRepository;
import com.example.dispenser.data.DispenserUtility;
import com.example.dispenser.data.PresetModel;
import com.example.dispenser.data.model.Dispenser;
import com.example.dispenser.ui.dispenser.DispenserDetailActivity;
import com.example.dispenser.ui.schedule.ScheduleActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DispenserFragment extends Fragment {
    private SwitchCompat switchPump1, switchPump2;
    TextView tvTankHeightA ;
    TextView tvTankHeightB ;
    private TextView powerLabel;
    private LinearLayout editHeightTankA;
    private LinearLayout editHeightTankB;

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
    private TextView statusDispenser;
    private ImageView checklist;
    private ImageView powerDevice;
    private PresetModel presetModelGlobal; // Untuk menyimpan resep yang dipilih user


    private DatabaseReference mDatabase;
    private String deviceId;
    private DispenserRepository repo;
    public static DispenserFragment newInstance() {
        return new DispenserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        repo=new DispenserRepository(this.getActivity().getApplication());
        deviceId=repo.getDispenserLastId();
        View root = inflater.inflate(R.layout.fragment_dispenser, container, false);
        Toolbar toolbar = root.findViewById(R.id.customToolbar);
        mDatabase = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("dispenser/" + deviceId);
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

            Log.d("DEBUG_DISPENSER", "WOI MASOK.");
            AddDispenserFragment listDispenserFragment = new AddDispenserFragment(anchorX, anchorY);
            int width = view.getWidth();
            int height = view.getHeight();
            listDispenserFragment.setWidth(width);
            listDispenserFragment.setHeight(height);

            // Tampilkan dialog fragment
            listDispenserFragment.show(getParentFragmentManager(), "AddDispenser");
        });
        HomeActivity main = (HomeActivity) getActivity();





        Button schedule=root.findViewById(R.id.btn_create_schedule);
        schedule.setOnClickListener(view -> {
//           Intent intent=new Intent(getActivity(), ScheduleActivity.class);
//           startActivity(intent);
            BottomNavigationView bottomNav = main.findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.navigation_schedule);

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
//        mViewModel.listenPower(deviceId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        powerLabel=view.findViewById(R.id.powerText);
        tvTankHeightA = view.findViewById(R.id.tv_tank_height_a);
        tvTankHeightB = view.findViewById(R.id.tv_tank_height_b);
        editHeightTankA=view.findViewById(R.id.layout_edit_tank_a);
        editHeightTankB=view.findViewById(R.id.layout_edit_tank_b);
        checklist = view.findViewById(R.id.imgCheck);
        deviceNameUi = view.findViewById(R.id.deviceName);
        deviceInUseUi = view.findViewById(R.id.deviceInUse);
        liquidAFill = view.findViewById(R.id.waterFilledLiquidA);
        liquidBFill=view.findViewById(R.id.waterFilledLiquidB);
        categoryLabel=view.findViewById(R.id.label_category_dispenser);
        deviceWaterLevelUiA = view.findViewById(R.id.numberTankLiquidA);
        deviceWaterLevelUiB = view.findViewById(R.id.numberTankLiquidB);
// Di dalam onViewCreated, setelah inisialisasi switch
        switchPump1 = view.findViewById(R.id.switch_pump_1);
        switchPump2 = view.findViewById(R.id.switch_pump_2);

        switchPump1.setOnClickListener(v -> {
            boolean isChecked = switchPump1.isChecked();
            if (deviceId != null) {
                // Mengirim true/false langsung ke dispenser/deviceId/pump1
                mDatabase.child("pump1").setValue(isChecked);
            }
        });

        switchPump2.setOnClickListener(v -> {
            boolean isChecked = switchPump2.isChecked();
            if (deviceId != null) {
                // Mengirim true/false langsung ke dispenser/deviceId/pump2
                mDatabase.child("pump2").setValue(isChecked);
            }
        });
//        scheduleDate = view.findViewById(R.id.scheduleDate);
//        scheduleClock = view.findViewById(R.id.scheduleClock);
//        statusDispenser=view.findViewById(R.id.status);
        numberOfProduction=view.findViewById(R.id.numberOfProduction);
        productionCompleted=view.findViewById(R.id.productionCompleted);
        remainingToComplete=view.findViewById(R.id.remainingToComplete);
        liquidTankA=view.findViewById(R.id.nameLiquidTankA);
        liquidTankB=view.findViewById(R.id.nameLiquidTankB);
        powerDevice=view.findViewById(R.id.powerDevice);
        editHeightTankA.setOnClickListener(v->{
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Height Tank A");
            input.setGravity(Gravity.CENTER);

            // Beri jarak (padding) agar EditText tidak nempel ke pinggir dialog
            FrameLayout container = new FrameLayout(getContext());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = 50; params.rightMargin = 50;
            input.setLayoutParams(params);
            container.addView(input);

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Set Tinggi Tank A")
                    .setMessage("Masukkan Tinggi botol dari tank A:")
                    .setView(container)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String val = input.getText().toString();
                        if (!val.isEmpty()) {
                            int heightTankA = Integer.parseInt(val);
                            tvTankHeightA.setText(heightTankA+" cm");
                            mViewModel.updateTankHeightA(mViewModel.getDispenserLastId(), heightTankA);
                            // Update sisa botol di UI juga agar sinkron

                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();

        });
        editHeightTankB.setOnClickListener(v->{
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Height Tank B");
            input.setGravity(Gravity.CENTER);

            // Beri jarak (padding) agar EditText tidak nempel ke pinggir dialog
            FrameLayout container = new FrameLayout(getContext());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = 50; params.rightMargin = 50;
            input.setLayoutParams(params);
            container.addView(input);

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Set Tinggi Tank B")
                    .setMessage("Masukkan Tinggi botol dari tank B:")
                    .setView(container)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String val = input.getText().toString();
                        if (!val.isEmpty()) {
                            int heightTankA = Integer.parseInt(val);
                            tvTankHeightB.setText(heightTankA+" cm");
                            mViewModel.updateTankHeightB(mViewModel.getDispenserLastId(), heightTankA);
                            // Update sisa botol di UI juga agar sinkron

                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();

        });
// Tambahkan di dalam onViewCreated atau di mana kamu init view
        View containerTarget = view.findViewById(R.id.container_target_edit);
        TextView tvTarget = view.findViewById(R.id.numberOfProduction);

        containerTarget.setOnClickListener(v -> {
            // Buat input angka
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Target");
            input.setGravity(Gravity.CENTER);

            // Beri jarak (padding) agar EditText tidak nempel ke pinggir dialog
            FrameLayout container = new FrameLayout(getContext());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.leftMargin = 50; params.rightMargin = 50;
            input.setLayoutParams(params);
            container.addView(input);

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Set Target Produksi")
                    .setMessage("Masukkan jumlah botol yang ingin diproduksi:")
                    .setView(container)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String val = input.getText().toString();
                        if (!val.isEmpty()) {
                            int target = Integer.parseInt(val);
                            tvTarget.setText("Number of Production: " + target);
                            // Update sisa botol di UI juga agar sinkron
                            TextView tvRemaining = view.findViewById(R.id.remainingToComplete);
                            tvRemaining.setText("Remaining: " + target);
                            mViewModel.updateBottleCount(mViewModel.getDispenserLastId(), target);

                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

// Cari View Kategori
        View layoutCategory = view.findViewById(R.id.layout_category);
        ImageView btnAddRecipe = view.findViewById(R.id.btn_add_preset_home);

// 1. Klik area oranye untuk pilih resep (PopupMenu)
        layoutCategory.setOnClickListener(v -> {
            showPresetPopupMenu(v);
        });

// 2. Klik icon plus untuk tambah resep (Material Dialog)
        if (btnAddRecipe != null) {
            btnAddRecipe.setOnClickListener(v -> {
                showAddRecipeDialog();
            });
        }



        // Dengarkan hasil dari AddDispenserFragment
        getParentFragmentManager().setFragmentResultListener("selected_dispenser", this,
                (requestKey, bundle) -> {
                    Dispenser dispenser=bundle.getParcelable(AddDispenserFragment.DISPENSER_SELECTED);

                    updateDispenserUI(dispenser);


                    mViewModel.saveLastUsedDispenser(dispenser.getDeviceId());

                    Log.d("HomeFragment", "Dipilih: " + dispenser.getDeviceName());
                });

    }
    private void showDeleteRecipeDialog() {
        mViewModel.getAllPresets().observe(getViewLifecycleOwner(), presets -> {
            if (presets == null || presets.isEmpty()) {
                Toast.makeText(getContext(), "Belum ada resep untuk dihapus", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] presetNames = new String[presets.size()];
            for (int i = 0; i < presets.size(); i++) {
                presetNames[i] = presets.get(i).getNamePresets();
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Pilih Resep yang Ingin Dihapus")
                    .setItems(presetNames, (dialog, which) -> {
                        // Ambil resep yang dipilih berdasarkan urutan klik
                        PresetModel selectedToDelete = presets.get(which);

                        // Munculkan konfirmasi final
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Konfirmasi Hapus")
                                .setMessage("Hapus resep '" + selectedToDelete.getNamePresets() + "'?")
                                .setPositiveButton("Hapus", (d, w) -> {
                                    // Panggil delete ke ViewModel
                                    mViewModel.deletePresetFromFirestore(selectedToDelete.getPresetId());
                                    Toast.makeText(getContext(), "Resep berhasil dihapus", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Batal", null)
                                .show();
                    })
                    .setNegativeButton("Tutup", null)
                    .show();
        });
    }
    private void showPresetPopupMenu(View anchor) {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(requireContext(), anchor);

        mViewModel.getAllPresets().observe(getViewLifecycleOwner(), presets -> {
            popup.getMenu().clear(); // Hindari menu duplikat saat data berubah

            // 1. Masukkan daftar resep untuk dipilih (Urutan 0 sampai n-1)
            for (int i = 0; i < presets.size(); i++) {
                popup.getMenu().add(0, i, i, presets.get(i).getNamePresets());
            }

            // 2. Tambahkan pilihan khusus di paling bawah untuk masuk ke menu hapus
            int MENU_DELETE_ID = 999;
            popup.getMenu().add(1, MENU_DELETE_ID, presets.size(), "🗑️ Kelola / Hapus Resep...");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == MENU_DELETE_ID) {
                    // JIKA KLIK HAPUS: Munculkan dialog daftar hapus tadi
                    showDeleteRecipeDialog();
                } else {
                    // JIKA KLIK RESEP: Jalankan logic pilih resep aslimu
                    presetModelGlobal = presets.get(item.getItemId());
                    String deviceId = mViewModel.getDispenserLastId();
                    if (deviceId != null) {
                        mViewModel.updateSelectedRecipe(
                                deviceId,
                                presetModelGlobal.getNamePresets(),
                                presetModelGlobal.getLiquidA(),
                                presetModelGlobal.getLiquidB(),
                                presetModelGlobal.getVolumeA(),
                                presetModelGlobal.getVolumeB()
                        );
                    }
                }
                return true;
            });
            popup.show();
        });
    }
//    private void showPresetPopupMenu(View anchor) {
//        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(requireContext(), anchor);
//
//        // Ambil data resep dari ViewModel/Firestore
//        mViewModel.getAllPresets().observe(getViewLifecycleOwner(), presets -> {
//            for (int i = 0; i < presets.size(); i++) {
//                popup.getMenu().add(0, i, i, presets.get(i).getNamePresets());
//            }
//
//            popup.setOnMenuItemClickListener(item -> {
//                presetModelGlobal = presets.get(item.getItemId());
//
//                // Update UI dashboard dengan resep yang dipilih
//                String deviceId = mViewModel.getDispenserLastId();
//                if (deviceId != null) {
//                    mViewModel.updateSelectedRecipe(
//                            deviceId,
//                            presetModelGlobal.getNamePresets(),
//                            presetModelGlobal.getLiquidA(),
//                            presetModelGlobal.getLiquidB(),
//                            presetModelGlobal.getVolumeA(),
//                            presetModelGlobal.getVolumeB()
//                    );
//                }
//                return true;
//            });
//            popup.show();
//        });
//    }
    private void showAddRecipeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText etName = dialogView.findViewById(R.id.edit_category);
        EditText etVolA = dialogView.findViewById(R.id.edit_volume_a);
        EditText etVolB = dialogView.findViewById(R.id.edit_volume_b);
        EditText etNameA=dialogView.findViewById(R.id.edit_liquid_a);
        EditText etNameB=dialogView.findViewById(R.id.edit_liquid_b);

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Tambah Resep Baru")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String name = etName.getText().toString();
                    int vA = Integer.parseInt(etVolA.getText().toString());
                    int vB = Integer.parseInt(etVolB.getText().toString());
                    String liquidA=etNameA.getText().toString();
                    String liquidB=etNameB.getText().toString();

                    // Panggil ViewModel untuk simpan ke Firestore
                    mViewModel.savePresetToFirestore(name, vA, vB, liquidA, liquidB);
                    Toast.makeText(getContext(), "Resep berhasil disimpan", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }


    // Interface callback
    interface OnHeightSetListener {
        void onHeightSet(int height);
    }
    private void updateDispenserUI(Dispenser dispenser) {
        if (dispenser == null) return;
        String liquidA=dispenser.getLiquidNameA()+ ": "+dispenser.getVolumeFilledA()+" ml";
        String liquidB=dispenser.getLiquidNameB()+ ": "+dispenser.getVolumeFilledB()+" ml";
        String categoryName=dispenser.getCategory();
        String dispenserName = dispenser.getDeviceName();
        String dispenserStatus = DispenserUtility.getStatus(dispenser.getStatus());
        int waterlevelTankA = dispenser.getWaterLevelTankA();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        int heightTankA=dispenser.getContainerHeightTankA();
        int heightTankB=dispenser.getContainerHeightTankB();
        // Pastikan user tidak null sebelum mengambil UID
//        if (user != null) {
//            if (dispenserStatus.equalsIgnoreCase("UnAvailable") || user.getUid().equals(dispenser.getUserId())) {
//                statusDispenser.setText("Connected");
//                statusDispenser.setTextColor(Color.parseColor("#4CAF50")); // Hijau
//            } else {
//                statusDispenser.setText("Not Connected");
//                statusDispenser.setTextColor(Color.RED); // Atau Color.parseColor("#F44336")
//            }
//        }
// Tambahkan di dalam method updateDispenserUI
        if (switchPump1 != null && switchPump2 != null) {
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Ambil sebagai Boolean
                        Boolean p1 = snapshot.child("pump1").getValue(Boolean.class);
                        Boolean p2 = snapshot.child("pump2").getValue(Boolean.class);

                        // Jika data tidak null, update posisi switch
                        if (p1 != null) switchPump1.setChecked(p1);
                        if (p2 != null) switchPump2.setChecked(p2);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Gagal load status pompa: " + error.getMessage());
                }
            });
        }
        // Checklist/Status Icon
        if (dispenserStatus.equalsIgnoreCase("Available") || dispenser.getUserId() != null) {
            checklist.setImageResource(R.drawable.check);
        } else {
            // Misalnya set ke icon lain jika sedang In Use oleh user lain
            // checklist.setImageResource(R.drawable.uncheck);
        }

        if (dispenser.isPower()) {
            powerDevice.setColorFilter(Color.RED); // Merah untuk memberi kesan "Bahaya/Stop"
            powerLabel.setTextColor(Color.RED);
            powerLabel.setText("Stop Production");
        } else {
            powerDevice.setColorFilter(Color.parseColor("#4CAF50")); // Hijau untuk "Go"
            powerLabel.setTextColor(Color.parseColor("#4CAF50"));
            powerLabel.setText("Start Production");
        }
        // Update TextViews

        deviceNameUi.setText("Device Name: " + dispenserName);
        deviceInUseUi.setText("Device in Use:");
//        scheduleDate.setText(getScheduleDate(dispenser.getTimeStart()));
//        scheduleClock.setText(getScheduleTime(dispenser.getTimeStart()));
        tvTankHeightA.setText(heightTankA+" cm");
        tvTankHeightB.setText(heightTankB+" cm");
        liquidAFill.setText(liquidA);
        liquidBFill.setText(liquidB);
        if(categoryName!=null){
            categoryLabel.setText("Category: "+categoryName);

        }else{
//            categoryLabel.setText("Category: "+"Tap here to Choose Category");

        }
        // Asumsi Tank A
        liquidTankA.setText(dispenser.getLiquidNameA());
        deviceWaterLevelUiA.setText(waterlevelTankA + " %");
        // Asumsi Tank B
        liquidTankB.setText(dispenser.getLiquidNameB());
        deviceWaterLevelUiB.setText(dispenser.getWaterLevelTankB() + " %");
        //ui tank
        updateWaterLevels(dispenser.getWaterLevelTankA(),dispenser.getWaterLevelTankB());
        //production
        numberOfProduction.setText("Number of Production: "+dispenser.getBottleCount());
        productionCompleted.setText("Production Completed: "+dispenser.getCurrentBottle());
        remainingToComplete.setText("Remaining to complete: "+(dispenser.getBottleCount()-dispenser.getCurrentBottle()));
        powerDevice.setOnClickListener(v->{
            if (dispenser.isPower()) {
                mViewModel.updatePowerStatus(dispenser.getDeviceId(), false);
//                dispenser.setPower(false);
            } else {
                mViewModel.updatePowerStatus(dispenser.getDeviceId(), true);
//                dispenser.setPower(true);

            }});
    }
    private void updateWaterLevels(int volA, int volB) {
        // Misalnya kapasitas tanki asli bapak adalah 1000ml
        int maxVolume = 100;

        // Hitung persentase untuk Tank A
        int levelA = (int) (((float) volA / maxVolume) * 10000);
        ImageView fillA = getView().findViewById(R.id.img_beaker_fill_a);
        fillA.setImageLevel(levelA);

        // Hitung persentase untuk Tank B
        int levelB = (int) (((float) volB / maxVolume) * 10000);
        ImageView fillB = getView().findViewById(R.id.img_beaker_fill_b);
        fillB.setImageLevel(levelB);

        // Logika Warna: Jika di bawah 10% (1000), ubah jadi merah
        if (levelA < 1000) fillA.setColorFilter(Color.RED);
        else fillA.setColorFilter(Color.parseColor("#FB8C00"));
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