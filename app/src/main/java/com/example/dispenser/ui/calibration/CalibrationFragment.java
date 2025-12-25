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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalibrationFragment extends Fragment {

    private DatabaseReference dbRef;

    private DispenserRepository repo;
    private String deviceId ; // Sesuaikan dengan ID alatmu

    private EditText inputP1, inputP2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calibration, container, false);
        repo = new DispenserRepository(this.getActivity().getApplication());
        deviceId = repo.getDispenserLastId();

        // Inisialisasi Firebase
        dbRef = FirebaseDatabase.getInstance("https://dispenser-dc485-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("dispenser/" + deviceId);

        // Inisialisasi View
        inputP1 = root.findViewById(R.id.input_ml_p1);
        inputP2 = root.findViewById(R.id.input_ml_p2);
        Button btnTestP1 = root.findViewById(R.id.btn_test_p1);
        Button btnSaveP1 = root.findViewById(R.id.btn_save_p1);
        Button btnTestP2 = root.findViewById(R.id.btn_test_p2);
        Button btnSaveP2 = root.findViewById(R.id.btn_save_p2);

        // Logika Pompa 1
        btnTestP1.setOnClickListener(v -> {
            dbRef.child("calibration/command").setValue("RUN_P1");
            Toast.makeText(getContext(), "Pompa 1 Berjalan 5 Detik...", Toast.LENGTH_SHORT).show();
        });

        btnSaveP1.setOnClickListener(v -> saveCalibration(1, inputP1.getText().toString()));

        // Logika Pompa 2
        btnTestP2.setOnClickListener(v -> {
            dbRef.child("calibration/command").setValue("RUN_P2");
            Toast.makeText(getContext(), "Pompa 2 Berjalan 5 Detik...", Toast.LENGTH_SHORT).show();
        });

        btnSaveP2.setOnClickListener(v -> saveCalibration(2, inputP2.getText().toString()));

        return root;
    }

    private void saveCalibration(int pumpNumber, String mlValue) {
        if (mlValue.isEmpty()) {
            Toast.makeText(getContext(), "Masukkan hasil ukur ml!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float measuredMl = Float.parseFloat(mlValue);
            // Rumus: ml_per_ms = volume / waktu (5000ms)
            double newGain = measuredMl / 5000.0;

            String path = (pumpNumber == 1) ? "calibration/ml_per_ms_p1" : "calibration/ml_per_ms_p2";

            dbRef.child(path).setValue(newGain);
            dbRef.child("calibration/command").setValue("IDLE");

            Toast.makeText(getContext(), "Kalibrasi Pompa " + pumpNumber + " Berhasil!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format angka salah!", Toast.LENGTH_SHORT).show();
        }
    }
}