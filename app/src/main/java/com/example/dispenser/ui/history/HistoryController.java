package com.example.dispenser.ui.history;

import android.util.Log;

import com.example.dispenser.data.AuthRemoteDataSource;
import com.example.dispenser.data.AuthRepositoryImpl;
import com.example.dispenser.data.model.HistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class HistoryController {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private AuthRepositoryImpl authRepository=AuthRepositoryImpl.getInstance(new AuthRemoteDataSource());


    public HistoryController() {
        db = FirebaseFirestore.getInstance();
        authRepository.setLoggedInUser();
        user=authRepository.getUser();
    }



    public Single<List<HistoryModel>> getUserHistory() {
        String uid = user.getUid();
        Log.e("DEBUG_UID", "UID User: "+uid);
        return Single.create(emitter ->
                db.collection("UserHistory")
                        .document(user.getUid())
                        .collection("history")
                        .orderBy("timeNow", com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(snap -> {
                            List<HistoryModel> list = new ArrayList<>();
                            Log.e("FIRESTORE", "Total Docs = " + snap.size());
                            for (DocumentSnapshot ds : snap.getDocuments()) {
                                Log.e("FIRESTORE", "Doc ID: " + ds.getId() + " => " + ds.getData());
                                HistoryModel model = ds.toObject(HistoryModel.class);
                                list.add(model);
                            }
                            emitter.onSuccess(list);
                        })
                        .addOnFailureListener(emitter::onError)
        );
    }
//    public interface HistoryCallback {
//        void onSuccess(List<HistoryModel> history);
//        void onError(String error);
//    }
}
