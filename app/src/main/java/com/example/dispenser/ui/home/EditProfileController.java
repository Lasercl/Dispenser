package com.example.dispenser.ui.home;

import android.content.Context;
import android.net.Uri;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.Map;

import com.cloudinary.*;


import android.net.Uri;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Map;

public class EditProfileController {
    private FirebaseAuth auth;

    public EditProfileController() {
        auth = FirebaseAuth.getInstance();
    }

    public interface OnUpdateListener {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public void updateProfileData(String name, String email, Uri newImageUri, OnUpdateListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        if (newImageUri != null) {
            // Upload ke Cloudinary menggunakan preset "dispenser"
            MediaManager.get().upload(newImageUri)
                    .unsigned("dispenser")
                    .callback(new UploadCallback() {
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String publicUrl = (String) resultData.get("secure_url");
                            saveToAuth(user, name, email, Uri.parse(publicUrl), listener);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            listener.onFailure("Cloudinary Error: " + error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }

                        @Override public void onStart(String requestId) {}
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                    }).dispatch();
        } else {
            // Jika tidak ganti foto, pakai foto yang sudah ada di Firebase
            saveToAuth(user, name, email, user.getPhotoUrl(), listener);
        }
    }

    private void saveToAuth(FirebaseUser user, String name, String email, Uri photoUri, OnUpdateListener listener) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photoUri)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Cek apakah email berubah
                if (!email.equals(user.getEmail())) {
                    user.updateEmail(email).addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            listener.onSuccess("Profil & Email berhasil diperbarui!");
                        } else {
                            listener.onFailure("Gagal update email (mungkin perlu login ulang)");
                        }
                    });
                } else {
                    listener.onSuccess("Profil berhasil diperbarui!");
                }
            } else {
                listener.onFailure("Gagal update profil.");
            }
        });
    }
}