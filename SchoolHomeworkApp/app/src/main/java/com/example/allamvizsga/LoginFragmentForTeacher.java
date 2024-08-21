package com.example.allamvizsga;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class LoginFragmentForTeacher extends Fragment {
    private EditText usernameLogin;
    private EditText passwordLogin;
    private Button loginButton;
    private TextView registerTextView;
    ImageView blueBackground;

    public LoginFragmentForTeacher() {
        // Required empty public constructor
    }

    public static LoginFragmentForTeacher newInstance() {
        return new LoginFragmentForTeacher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_for_teacher, container, false);
        blueBackground = view.findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(10, 5));

        Glide.with(requireContext())
                .load(R.drawable.main_background)
                .apply(requestOptions)
                .into(blueBackground);

        usernameLogin = view.findViewById(R.id.loginUsername);
        passwordLogin = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);
        registerTextView = view.findViewById(R.id.registerTextView);


        //Login For Teacher
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tanarok");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameLogin.getText().toString();
                String password = passwordLogin.getText().toString();
                if(username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Tölsd ki mindent mezőt", Toast.LENGTH_SHORT).show();
                }
                databaseReference.orderByChild("felhasznalonev").equalTo(username.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                String passwordFromDB = childSnapshot.child("jelszo").getValue(String.class);
                                if (password.equals(passwordFromDB)) {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("currentUserId", childSnapshot.getKey());
                                    editor.apply();
                                    Intent intent = new Intent(getActivity(), Dashboard.class);
                                    startActivity(intent);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Hiba történt a bejelentkezés közben!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new RegistrationForTeacher());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });





        return view;
    }
}
