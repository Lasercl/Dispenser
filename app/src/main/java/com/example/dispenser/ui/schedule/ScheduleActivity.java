package com.example.dispenser.ui.schedule;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dispenser.R;
import com.example.dispenser.data.PresetModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ScheduleActivity extends AppCompatActivity {

    private PresetModel presetModelGlobal;
    private final String ADD_NEW_CATEGORY_TITLE = "➕ Add New Category";
    private final CompositeDisposable disposables = new CompositeDisposable();

    private EditText inputBottleCount;
    private TimePicker timePicker;
    private CheckBox checkSun, checkMon, checkTue, checkWed, checkThu, checkFri, checkSat;
    private Button buttonConfirm;
    private TextView liquidATextView, liquidBTextView;
    private TextView spinnerCategoryTextView;
    private ScheduleController scheduleController;

    // Variabel untuk Edit Mode
    private int slotIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);

        // Ambil SLOT_INDEX dari Intent
        slotIndex = getIntent().getIntExtra("SLOT_INDEX", -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(slotIndex == -1 ? "Set Schedule" : "Edit Schedule");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inisialisasi View (Sama seperti sebelumnya)
        initViews();

        // 4. Jika Mode EDIT: Ambil data dari Firebase
        if (slotIndex != -1) {
            loadExistingScheduleData(slotIndex);
        }
    }

    private void initViews() {
        scheduleController = new ScheduleController(this.getApplication());
        spinnerCategoryTextView = findViewById(R.id.spinner_category);
        liquidATextView = findViewById(R.id.liquidAText);
        liquidBTextView = findViewById(R.id.liquidBText);
        timePicker = findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        checkSun = findViewById(R.id.check_sun);
        checkMon = findViewById(R.id.check_mon);
        checkTue = findViewById(R.id.check_tue);
        checkWed = findViewById(R.id.check_wed);
        checkThu = findViewById(R.id.check_thu);
        checkFri = findViewById(R.id.check_fri);
        checkSat = findViewById(R.id.check_sat);

        inputBottleCount = findViewById(R.id.input_bottleCount);
        buttonConfirm = findViewById(R.id.button_confirm);

        spinnerCategoryTextView.setOnClickListener(v -> showDynamicPopupMenuRx(spinnerCategoryTextView));
        buttonConfirm.setOnClickListener(view -> saveScheduleToMachine());
    }

    // Fungsi untuk mengambil data lama jika Edit Mode
    private void loadExistingScheduleData(int index) {
        String deviceId = scheduleController.getDispenserLastId();
        disposables.add(
                scheduleController.getSingleScheduleRx(deviceId, index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            // Isi TimePicker
                            timePicker.setHour(s.hour);
                            timePicker.setMinute(s.minute);

                            // Isi Bottle Count
                            inputBottleCount.setText(String.valueOf(s.count));

                            // Isi Checkboxes (Bitmask decoder)
                            checkSun.setChecked((s.dowMask & (1 << 0)) != 0);
                            checkMon.setChecked((s.dowMask & (1 << 1)) != 0);
                            checkTue.setChecked((s.dowMask & (1 << 2)) != 0);
                            checkWed.setChecked((s.dowMask & (1 << 3)) != 0);
                            checkThu.setChecked((s.dowMask & (1 << 4)) != 0);
                            checkFri.setChecked((s.dowMask & (1 << 5)) != 0);
                            checkSat.setChecked((s.dowMask & (1 << 6)) != 0);

                            // Set Preset Manual (karena data volume ada di schedule)
                            spinnerCategoryTextView.setText(s.categoryName + " ▼");
                            liquidATextView.setText("Liquid A: " + s.volA + " ml");
                            liquidBTextView.setText("Liquid B: " + s.volB + " ml");

                            // Buat preset dummy agar tidak null saat save
                            presetModelGlobal = new PresetModel();
                            presetModelGlobal.setNamePresets(s.categoryName);
                            presetModelGlobal.setVolumeA(s.volA);
                            presetModelGlobal.setVolumeB(s.volB);

                        }, throwable -> Log.e("EDIT", "Gagal muat data", throwable))
        );
    }

    private void saveScheduleToMachine() {
        String deviceId = scheduleController.getDispenserLastId();
        if (deviceId == null || presetModelGlobal == null) {
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int dowMask = calculateDowMask();
        if (dowMask == 0) {
            Toast.makeText(this, "Pilih minimal satu hari!", Toast.LENGTH_SHORT).show();
            return;
        }

        String countStr = inputBottleCount.getText().toString().trim();
        int bottleCount = countStr.isEmpty() ? 1 : Integer.parseInt(countStr);

        if (slotIndex != -1) {
            // MODE EDIT
            scheduleController.saveToRTDB(deviceId, slotIndex, presetModelGlobal, hour, minute, dowMask, bottleCount);
            Toast.makeText(this, "Jadwal diperbarui", Toast.LENGTH_SHORT).show();
        } else {
            // MODE TAMBAH
            scheduleController.findEmptySlotAndSave(deviceId, presetModelGlobal, hour, minute, dowMask, bottleCount);
            Toast.makeText(this, "Jadwal baru disimpan", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // Logika pengubahan Checkbox ke angka bitmask (0-127)
    private int calculateDowMask() {
        int mask = 0;
        if (checkSun.isChecked()) mask |= (1 << 0); // 1
        if (checkMon.isChecked()) mask |= (1 << 1); // 2
        if (checkTue.isChecked()) mask |= (1 << 2); // 4
        if (checkWed.isChecked()) mask |= (1 << 3); // 8
        if (checkThu.isChecked()) mask |= (1 << 4); // 16
        if (checkFri.isChecked()) mask |= (1 << 5); // 32
        if (checkSat.isChecked()) mask |= (1 << 6); // 64
        return mask;
    }

    private void showDynamicPopupMenuRx(TextView anchorView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        disposables.add(
                scheduleController.fetchPresetsRx(currentUser.getUid())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(presets -> {
                            showPopupMenu(anchorView, presets);
                        }, error -> {
                            Log.e(TAG, "Error fetching presets", error);
                        })
        );
    }

    private void showPopupMenu(TextView anchorView, List<PresetModel> finalPresets) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        for (int i = 0; i < finalPresets.size(); i++) {
            popup.getMenu().add(Menu.NONE, i, i, finalPresets.get(i).getNamePresets());
        }

        final int ADD_ID = finalPresets.size();
        popup.getMenu().add(Menu.NONE, ADD_ID, ADD_ID, ADD_NEW_CATEGORY_TITLE);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == ADD_ID) {
                handleAddNewCategory();
            } else {
                PresetModel selected = finalPresets.get(item.getItemId());
                handlePresetSelection(selected);
            }
            return true;
        });
        popup.show();
    }

    private void handlePresetSelection(PresetModel selectedPreset) {
        presetModelGlobal = selectedPreset;
        spinnerCategoryTextView.setText(selectedPreset.getNamePresets() + " ▼");
        liquidATextView.setText("Liquid A: " + selectedPreset.getVolumeA() + " ml");
        liquidBTextView.setText("Liquid B: " + selectedPreset.getVolumeB() + " ml");
    }

    private void handleAddNewCategory() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Create New Recipe Preset");
        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            EditText editCategory = dialogView.findViewById(R.id.edit_category);
            EditText editLiquidA = dialogView.findViewById(R.id.edit_liquid_a);
            EditText editVolumeA = dialogView.findViewById(R.id.edit_volume_a);
            EditText editLiquidB = dialogView.findViewById(R.id.edit_liquid_b);
            EditText editVolumeB = dialogView.findViewById(R.id.edit_volume_b);

            try {
                scheduleController.saveNewPresetToFirestore(
                        editCategory.getText().toString(),
                        editLiquidA.getText().toString(),
                        Integer.parseInt(editVolumeA.getText().toString()),
                        editLiquidB.getText().toString(),
                        Integer.parseInt(editVolumeB.getText().toString())
                );
            } catch (Exception e) {
                Toast.makeText(this, "Input tidak valid", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}