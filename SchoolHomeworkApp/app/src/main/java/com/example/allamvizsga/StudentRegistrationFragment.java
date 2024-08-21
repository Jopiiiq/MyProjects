package com.example.allamvizsga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class StudentRegistrationFragment extends Fragment {

    private EditText usernameLogin;
    private EditText passwordLogin;
    private TextView registerTextview;
    private Button loginButton;
    private FirebaseAuth mAuth;
    ImageView blueBackground;
    public StudentRegistrationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_registration, container, false);
        mAuth = FirebaseAuth.getInstance();

        blueBackground = view.findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(10, 5));

        Glide.with(requireContext())
                .load(R.drawable.student_login_background)
                .apply(requestOptions)
                .into(blueBackground);

        usernameLogin = view.findViewById(R.id.usernameLogin);


        passwordLogin = view.findViewById(R.id.passwordLogin);


        registerTextview = view.findViewById(R.id.registerTextview);

        loginButton= view.findViewById(R.id.loginButton);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("diakok");


        //Check if username and password are correct
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
                                    Intent intent = new Intent(getContext(), Dashboard.class);
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("currentUserId", childSnapshot.getKey());
                                    editor.apply();
                                    startActivity(intent);

                                } else {

                                    Toast.makeText(getContext(), "Helytelen jelszó", Toast.LENGTH_SHORT).show();

                                }
                            }
                        } else {

                            Toast.makeText(getContext(), "Helytelen felhasználónév", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getContext(), "Hiba történt a bejelentkezés közben!", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

        registerTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new RegistrationForStudentFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });



        return view;


    }
    //Saves userID from Firebase into currentUserId file

}
