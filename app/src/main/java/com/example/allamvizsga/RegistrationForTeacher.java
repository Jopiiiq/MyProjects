package com.example.allamvizsga;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class RegistrationForTeacher extends Fragment {
    private EditText username;
    private EditText fullname;
    private EditText password;
    private EditText checkpassword;
    private Button registerButton;
    ImageView blueBackground;
    private List<UserData.Tanarok> tanarokList = new ArrayList<>();

    public RegistrationForTeacher() {
        // Required empty public constructor
    }

    public static RegistrationForTeacher newInstance() {
        return new RegistrationForTeacher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_for_teacher, container, false);

        blueBackground = view.findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(10, 5));

        Glide.with(requireContext())
                .load(R.drawable.main_background)
                .apply(requestOptions)
                .into(blueBackground);

        username= view.findViewById(R.id.username);
        password= view.findViewById(R.id.password);
        checkpassword= view.findViewById(R.id.checkPassword);
        registerButton= view.findViewById(R.id.registerButton);
        fullname= view.findViewById(R.id.fullName);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                String checkPasswordText = checkpassword.getText().toString();
                String fullNameText = fullname.getText().toString();

                if(usernameText.isEmpty() || passwordText.isEmpty() || checkPasswordText.isEmpty()){
                    Toast.makeText(getContext(), "Tölsd ki mindent mezőt", Toast.LENGTH_SHORT).show();
                }else{
                    String generatedTanarID = tanarIDGenerator(tanarokList);
                    saveUserDataForTeacher(fullNameText,usernameText,passwordText,generatedTanarID);
                }

            }
        });

        return view;
    }

    private void saveUserDataForTeacher(String fullname, String username, String password,String tanarID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String tanarok = databaseReference.child("tanarok").push().getKey();


        UserData.Tanarok tanar = new UserData.Tanarok(fullname, username, password,tanarID);
        databaseReference.child("tanarok").child(tanarok).setValue(tanar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Registration", "Sikeres regisztráció");
                        Toast.makeText(getContext(), "Sikeres regisztrálás", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new LoginFragmentForTeacher());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Registration", "Hiba a regisztrációhoz");
                        Toast.makeText(getContext(), "Hiba a regisztrációhoz", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public String tanarIDGenerator(List<UserData.Tanarok> tanarokList) {
        Random random = new Random();
        String tanarID;
        int randomNumber;
        do{
            randomNumber= random.nextInt(1000);

            tanarID="T-"+randomNumber;

        }while (containsTanarID(tanarokList, tanarID));
        return tanarID;



    }
    private boolean containsTanarID(List<UserData.Tanarok> tanarokList, String tanarID) {
        for (UserData.Tanarok tanar : tanarokList) {
            if (tanar.getTanarID().equals((tanarID))) {
                return true;
            }
        }
        return false;
    }
}
