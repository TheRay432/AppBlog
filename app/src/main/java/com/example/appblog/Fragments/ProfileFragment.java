package com.example.appblog.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appblog.Activitys.HomeActivity;
import com.example.appblog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

  FirebaseAuth mAuth;
  FirebaseUser currentUser;
  private ImageView profileImg;
  private TextView profileName,profileMail;

    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HomeActivity activity=(HomeActivity)getActivity();
        // Inflate the layout for this fragment
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        profileImg=(ImageView)v.findViewById(R.id.profile_userImg);
        profileName=(TextView)v.findViewById(R.id.profile_name);
        profileMail=(TextView)v.findViewById(R.id.profile_mail);

        profileName.setText(currentUser.getDisplayName());
        profileMail.setText(currentUser.getEmail());
        Glide.with(activity).load(currentUser.getPhotoUrl()).into(profileImg);


        return v;

    }
}