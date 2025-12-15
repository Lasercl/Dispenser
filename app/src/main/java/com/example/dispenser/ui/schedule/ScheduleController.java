package com.example.dispenser.ui.schedule;
import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import com.example.dispenser.data.PresetModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {

// --- Di dalam Activity atau Repository Class Anda ---

    public Single<List<PresetModel>> fetchPresetsRx(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return Single.create(emitter -> {

            // Menambahkan filter .whereEqualTo("createdBy", userId)
            db.collection("presets")
                    .whereEqualTo("createdBy", userId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<PresetModel> presets = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            try {
                                String id = document.getId();
                                String namePreset=document.getString("namePresets");
                                String createdBy = document.getString("createdBy");
                                String liquidA = document.getString("liquidA");
                                String liquidB = document.getString("liquidB");
                                int volA = document.get("volumeA",Integer.class);
                                int volB = document.get("volumeB",Integer.class);

                                if (liquidA != null && liquidB != null && volA != -1 && volB != -1) {
                                    PresetModel preset = new PresetModel(
                                            id,
                                            namePreset,
                                            createdBy,
                                            liquidA,
                                            liquidB,
                                            volA,
                                            volB
                                    );
                                    presets.add(preset);
                                }
                            } catch (Exception e) {
                                Log.e("Firestore", "Error parsing preset document: " + e.getMessage());
                            }
                        }
                        emitter.onSuccess(presets);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
    public void saveNewPresetToFirestore(String categoryName, String liquidA, int volumeA, String liquidB, int volumeB) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
//            Toast.makeText(this, "Anda harus login untuk menyimpan resep.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> presetData = new HashMap<>();
        // Gunakan categoryName sebagai nama dokumen atau field tambahan di sini
        presetData.put("namePresets", categoryName); // Tambahkan field nama agar mudah dibaca
        presetData.put("createdBy", user.getUid());
        presetData.put("liquidA", liquidA);
        presetData.put("liquidB", liquidB);
        presetData.put("volumeA", volumeA);
        presetData.put("volumeB", volumeB);

        db.collection("presets")
                .add(presetData)
                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(this, "Resep " + categoryName + " berhasil disimpan!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving preset", e);
//                    Toast.makeText(this, "Gagal menyimpan resep: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
