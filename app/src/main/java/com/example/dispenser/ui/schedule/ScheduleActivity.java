package com.example.dispenser.ui.schedule;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import com.example.dispenser.R;
import com.example.dispenser.data.PresetModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private PresetModel presetModelGlobal;
    private final String ADD_NEW_CATEGORY_TITLE = "➕ Add New Category";
    private final CompositeDisposable disposables = new CompositeDisposable();
    private TextInputEditText inputBottleCount;

    private NumberPicker pickerAmpm;
    private NumberPicker pickerHour;
    private NumberPicker pickerMinute;
    private TextView inputDate;
    private Button buttonConfirm;
    private  ScheduleController scheduleController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setTitle("Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_schedule);
        TextView anchorView = findViewById(R.id.spinner_category);
        // Dapatkan referensi\
        scheduleController=new ScheduleController(this.getApplication());
        buttonConfirm=findViewById(R.id.button_confirm);
        pickerAmpm = findViewById(R.id.picker_ampm);
        pickerHour = findViewById(R.id.picker_hour);
        pickerMinute = findViewById(R.id.picker_minute);
        inputDate = findViewById(R.id.input_date);
        inputBottleCount=findViewById(R.id.input_bottleCount);
        // Set listener yang akan memanggil DatePickerDialog saat diklik
//        buttonConfirm.setOnClickListener(view -> {
//            scheduleController.getLastDispenser().observe(this, dispenser ->{
//                if (dispenser != null) {
//                    long scheduledTimestamp = getSelectedDateTimeMillis();
//                    scheduleController.confirmSchedule(dispenser.getDeviceId(),presetModelGlobal,scheduledTimestamp);
//                }
//            });
//        });
        buttonConfirm.setOnClickListener(view -> {
            String lastDispenserId=scheduleController.getDispenserLastId();
                if (lastDispenserId != null) {
                    long scheduledTimestamp = getSelectedDateTimeMillis();
                    int bottleCount=Integer.parseInt(inputBottleCount.getText().toString().trim());

                    scheduleController.confirmSchedule(lastDispenserId,presetModelGlobal,scheduledTimestamp,bottleCount);
                }
        });
        inputDate.setOnClickListener(v -> showDatePickerDialog());
        setupTimePickers();
        anchorView.setOnClickListener(v->showDynamicPopupMenuRx(anchorView));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private long getSelectedDateTimeMillis() {
        return scheduleController.calculateDateTimeMillis(
                inputDate.getText().toString(),
                pickerHour.getValue(),
                pickerMinute.getValue(),
                pickerAmpm.getValue()
        );
    }


    private void setupTimePickers() {

        // A. Konfigurasi AM/PM
        // Nilai yang ditampilkan: AM dan PM
        final String[] amPmStrings = {"AM", "PM"};
        pickerAmpm.setMinValue(0);
        pickerAmpm.setMaxValue(amPmStrings.length - 1);
        pickerAmpm.setDisplayedValues(amPmStrings);
        // Menonaktifkan fokus turunan agar pengguna tidak bisa mengetik (sesuai XML)
        pickerAmpm.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // B. Konfigurasi Jam (Hour)
        // Nilai jam: 1 sampai 12
        pickerHour.setMinValue(1);
        pickerHour.setMaxValue(12);
        pickerHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // C. Konfigurasi Menit (Minute)
        // Nilai menit: 00 sampai 59
        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        // Menambahkan padding nol (0) agar terlihat 00, 01, 02, dst.
        pickerMinute.setFormatter(i -> String.format("%02d", i));
        pickerMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // D. Atur Waktu Default (Opsional: Waktu Saat Ini)
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR); // Jam dalam format 1-11
        if (currentHour == 0) currentHour = 12; // Jika jam 0 (midnight/noon), set ke 12
        int currentMinute = c.get(Calendar.MINUTE);
        int currentAmPm = c.get(Calendar.AM_PM); // 0 untuk AM, 1 untuk PM

        pickerHour.setValue(currentHour);
        pickerMinute.setValue(currentMinute);
        pickerAmpm.setValue(currentAmPm);
    }

    // --- Di dalam Activity Anda ---
    private void showDatePickerDialog() {
        // Ambil tanggal yang saat ini ditampilkan di TextView (atau tanggal hari ini jika belum ada)
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Buat DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, // Context

                // Listener yang akan dijalankan saat tanggal dipilih
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // monthOfYear adalah 0-based (0 = Januari, 11 = Desember)
                        updateDateLabel(year, monthOfYear, dayOfMonth);
                    }
                },

                // Nilai awal yang ditampilkan di dialog
                year, month, day
        );

        datePickerDialog.show();
        //
    }
    private void updateDateLabel(int year, int month, int day) {
        // Objek Calendar untuk menampung tanggal yang dipilih
        Calendar selectedDate = Calendar.getInstance();
        // Penting: Masukkan nilai monthOfYear apa adanya, Calendar akan menanganinya
        selectedDate.set(year, month, day);

        // Format tanggal: EEE (nama hari singkat), MMM (nama bulan singkat), d (tanggal), yyyy (tahun)
        String format = "EEE, MMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        // Set teks di TextView
        inputDate.setText(sdf.format(selectedDate.getTime()));
    }
    private void showDynamicPopupMenuRx(TextView anchorView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Anda harus login untuk melihat preset.", Toast.LENGTH_LONG).show();
            return;
        }
        String currentUserId = currentUser.getUid();

        disposables.add(
                scheduleController.fetchPresetsRx(currentUserId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(presetsFromFirestore -> {

                            // Tampilkan PopupMenu setelah data dimuat
                            showPopupMenu(anchorView, presetsFromFirestore);

                        }, error -> {
                            Log.e(TAG, "Error fetching presets", error);
                            Toast.makeText(this, "Gagal memuat resep: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        })
        );
    }

    private void handlePresetSelection(PresetModel selectedPreset) {
        String flag=" ▼";
        String line=" | ";
        TextView liquidATextView = findViewById(R.id.liquidAText);
        TextView liquidBTextView = findViewById(R.id.liquidBText);
        TextView spinnerCategoryTextView = findViewById(R.id.spinner_category);
        spinnerCategoryTextView.setText(selectedPreset.getNamePresets()+flag);
        liquidATextView.setText(selectedPreset.getLiquidA()+line+selectedPreset.getVolumeA()+" ml");
        liquidBTextView.setText(selectedPreset.getLiquidB()+line+selectedPreset.getVolumeB()+" ml");
        presetModelGlobal=selectedPreset;




    }

    private void handleAddNewCategory() {

        // 1. Inflate layout custom
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null); // Pastikan nama layout benar

        // 2. Dapatkan referensi ke EditText di layout dialog
        final EditText editCategory = dialogView.findViewById(R.id.edit_category);
        final EditText editLiquidA = dialogView.findViewById(R.id.edit_liquid_a);
        final EditText editVolumeA = dialogView.findViewById(R.id.edit_volume_a);
        final EditText editLiquidB = dialogView.findViewById(R.id.edit_liquid_b);
        final EditText editVolumeB = dialogView.findViewById(R.id.edit_volume_b);

        // 3. Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Recipe Preset");
        builder.setView(dialogView); // Set tampilan custom ke dialog builder

        // 4. Set tombol Positive (Save/Add)
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Ambil data dari EditTexts
            String categoryName = editCategory.getText().toString().trim();
            String liquidA = editLiquidA.getText().toString().trim();
            String volumeAStr = editVolumeA.getText().toString().trim();
            String liquidB = editLiquidB.getText().toString().trim();
            String volumeBStr = editVolumeB.getText().toString().trim();

            // Lakukan validasi dasar
            if (categoryName.isEmpty() || liquidA.isEmpty() || volumeAStr.isEmpty() || liquidB.isEmpty() || volumeBStr.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_LONG).show();
                // Catatan: Dialog akan tertutup, Anda mungkin ingin menanganinya agar tetap terbuka saat validasi gagal.
                return;
            }

            // Ubah volume ke integer
            try {
                int volumeA = Integer.parseInt(volumeAStr);
                int volumeB = Integer.parseInt(volumeBStr);

                // Panggil fungsi untuk menyimpan data ke Firestore
                scheduleController.saveNewPresetToFirestore(categoryName, liquidA, volumeA, liquidB, volumeB);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Volume harus berupa angka.", Toast.LENGTH_LONG).show();
            }
        });

        // 5. Set tombol Negative (Cancel)
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showPopupMenu(TextView anchorView, List<PresetModel> finalPresets) {
        PopupMenu popup = new PopupMenu(this, anchorView);

        // 1. Isi Menu dengan Preset
        for (int i = 0; i < finalPresets.size(); i++) {
            // Menggunakan indeks sebagai ID menu
            popup.getMenu().add(Menu.NONE, i, i, finalPresets.get(i).getNamePresets());
        }

        // 2. Tambahkan Opsi "Add New Category"
        final int ADD_ID = finalPresets.size();
        popup.getMenu().add(Menu.NONE, ADD_ID, ADD_ID, ADD_NEW_CATEGORY_TITLE);

        // 3. Atur Listener
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == ADD_ID) {
                handleAddNewCategory();
            } else {
                PresetModel selectedPreset = finalPresets.get(item.getItemId());
                anchorView.setText(selectedPreset.toString() + " ▼");
                handlePresetSelection(selectedPreset);
            }
            return true;
        });

        popup.show();
    }

    // --- PENTING: Membersihkan disposable saat Activity/Fragment hancur ---
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Penting untuk mencegah memory leaks
        disposables.clear();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // kembali ke activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}