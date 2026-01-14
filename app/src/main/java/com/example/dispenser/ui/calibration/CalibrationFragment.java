package com.example.dispenser.ui.calibration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dispenser.R;
import com.example.dispenser.data.DispenserRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class CalibrationFragment extends Fragment {

    private DatabaseReference dbRef;
    private DispenserRepository repo;
    private String deviceId;

    private EditText inputP1, inputP2, inputFlowP1, inputFlowP2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calibration, container, false);
        repo = new DispenserRepository(this.getActivity().getApplication());
        deviceId = repo.getDispenserLastId();

        // Inisialisasi Firebase
        dbRef = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("dispenser/" + deviceId);

        // Inisialisasi View RTC (Lama)
        inputP1 = root.findViewById(R.id.input_ml_p1);
        inputP2 = root.findViewById(R.id.input_ml_p2);
        Button btnTestP1 = root.findViewById(R.id.btn_test_p1);
        Button btnSaveP1 = root.findViewById(R.id.btn_save_p1);
        Button btnTestP2 = root.findViewById(R.id.btn_test_p2);
        Button btnSaveP2 = root.findViewById(R.id.btn_save_p2);

        // Inisialisasi View Sensor Flow (Baru)
        inputFlowP1 = root.findViewById(R.id.input_flow_p1);
        inputFlowP2 = root.findViewById(R.id.input_flow_p2);
        Button btnMinusF1 = root.findViewById(R.id.btn_minus_f1);
        Button btnPlusF1 = root.findViewById(R.id.btn_plus_f1);
        Button btnDefaultF1 = root.findViewById(R.id.btn_default_f1);
        Button btnMinusF2 = root.findViewById(R.id.btn_minus_f2);
        Button btnPlusF2 = root.findViewById(R.id.btn_plus_f2);
        Button btnDefaultF2 = root.findViewById(R.id.btn_default_f2);

        // --- LOGIKA REALTIME SENSOR FLOW ---

        // Listener Tombol Pompa 1
        btnPlusF1.setOnClickListener(v -> updateFlowRealtime(inputFlowP1, "calibration/pulse1", 0.01f));
        btnMinusF1.setOnClickListener(v -> updateFlowRealtime(inputFlowP1, "calibration/pulse1", -0.01f));
        btnDefaultF1.setOnClickListener(v -> {
            inputFlowP1.setText("4.85");
            dbRef.child("calibration/pulse1").setValue(4.85f);
        });

        // Listener Tombol Pompa 2
        btnPlusF2.setOnClickListener(v -> updateFlowRealtime(inputFlowP2, "calibration/pulse2", 0.01f));
        btnMinusF2.setOnClickListener(v -> updateFlowRealtime(inputFlowP2, "calibration/pulse2", -0.01f));
        btnDefaultF2.setOnClickListener(v -> {
            inputFlowP2.setText("4.85");
            dbRef.child("calibration/pulse2").setValue(4.85f);
        });

        // Ambil data Pulse/ml saat ini dari Firebase agar sinkron
        syncFlowDataFromFirebase();

        // --- LOGIKA RTC (INPUT MANUAL) ---

        btnTestP1.setOnClickListener(v -> {
            dbRef.child("calibration/command").setValue("RUN_P1");
            Toast.makeText(getContext(), "Pompa 1 Berjalan 5 Detik...", Toast.LENGTH_SHORT).show();
        });
        btnSaveP1.setOnClickListener(v -> saveCalibration(1, inputP1.getText().toString()));

        btnTestP2.setOnClickListener(v -> {
            dbRef.child("calibration/command").setValue("RUN_P2");
            Toast.makeText(getContext(), "Pompa 2 Berjalan 5 Detik...", Toast.LENGTH_SHORT).show();
        });
        btnSaveP2.setOnClickListener(v -> saveCalibration(2, inputP2.getText().toString()));

        return root;
    }

    // Fungsi Update Real-time Sensor (Tanpa tombol simpan)
    private void updateFlowRealtime(EditText editText, String path, float delta) {
        String currentValStr = editText.getText().toString();
        float currentValue = currentValStr.isEmpty() ? 4.85f : Float.parseFloat(currentValStr);
        float newValue = currentValue + delta;

        // Update UI
        editText.setText(String.format(Locale.US, "%.2f", newValue));

        // Update Firebase seketika
        dbRef.child(path).setValue(newValue);
    }

    // Mengambil data Pulse/ml terakhir agar UI tidak balik ke 4.85 saat buka fragment
    private void syncFlowDataFromFirebase() {
        dbRef.child("calibration").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("pulse1")) {
                    Object val1 = snapshot.child("pulse_p1").getValue();
                    if (val1 != null) {
                        float f1 = Float.parseFloat(val1.toString());
                        inputFlowP1.setText(String.format(Locale.US, "%.2f", f1));
                    }
                }
                if (snapshot.hasChild("pulse2")) {
                    Object val2 = snapshot.child("pulse_p2").getValue();
                    if (val2 != null) {
                        float f2 = Float.parseFloat(val2.toString());
                        inputFlowP2.setText(String.format(Locale.US, "%.2f", f2));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveCalibration(int pumpNumber, String mlValue) {
        if (mlValue.isEmpty()) {
            Toast.makeText(getContext(), "Masukkan hasil ukur ml!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float measuredMl = Float.parseFloat(mlValue);
            double newGain = measuredMl / 5000.0;
            String path = (pumpNumber == 1) ? "calibration/ml_per_ms_p1" : "calibration/ml_per_ms_p2";

            dbRef.child(path).setValue(newGain);
            dbRef.child("calibration/command").setValue("IDLE");

            Toast.makeText(getContext(), "Kalibrasi Waktu P" + pumpNumber + " Berhasil!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format angka salah!", Toast.LENGTH_SHORT).show();
        }
    }
}