package com.example.dispenser.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dispenser.MainActivity;
import com.example.dispenser.R;
import com.example.dispenser.ui.history.HistoryActivity;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    private ProfileViewModel viewModel;
    private TextView nameProfile;
    private TextView email;
    private ImageView pictureProfile;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        nameProfile=root.findViewById(R.id.txtName);
        email=root.findViewById(R.id.txtEmail);
        pictureProfile=root.findViewById(R.id.imgAvatar);
        updateUIProfile();
        Button buttonLogout=root.findViewById(R.id.btnLogout);
        TextView historyButton=root.findViewById(R.id.btnHistory);
        TextView editProfileButton=root.findViewById(R.id.btnEditProfile);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return root;
    }
        private void logout() {
            viewModel.logout();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            getActivity().finish();

        }

        public void updateUIProfile(){
            FirebaseUser user=viewModel.getCurrentUser();
            nameProfile.setText(user.getDisplayName());
            email.setText(user.getEmail());
            if(user.getPhotoUrl()!=null){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.outline_person_24)
                        .error(R.drawable.outline_person_24)
                        .into(pictureProfile);
            }
        }
}

