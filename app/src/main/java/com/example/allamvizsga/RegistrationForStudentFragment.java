package com.example.allamvizsga;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class RegistrationForStudentFragment extends Fragment {

    private EditText fullNameTextView;
    private EditText usernameTextView;
    private EditText passwordTextView;
    private EditText checkPasswordTextView;
    private EditText parentIDTextView;
    private Spinner classYearSpinner;

    private Spinner groupSpinner;

    private String fullName;
    private String username;
    private String password;
    private String checkPassword;
    private String classYear;
    private String group;
    private String parentID;

    private Button buttonReg;
    private List<UserData.Diakok> diakokList = new ArrayList<>();
    private ImageView blueBackground;

    public RegistrationForStudentFragment() {

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_for_student, container, false);
        blueBackground = view.findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(25, 1));

        Glide.with(requireContext())
                .load(R.drawable.student_login_background)
                .apply(requestOptions)
                .into(blueBackground);


        blueBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);


        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        passwordTextView = view.findViewById(R.id.passwordTextView);
        checkPasswordTextView = view.findViewById(R.id.checkPasswordTextView);

        classYearSpinner = view.findViewById(R.id.classYearSpinner);
        groupSpinner = view.findViewById(R.id.groupSpinner);

        buttonReg = view.findViewById(R.id.buttonReg);




        //Filling Spinners with Data
        String[] years={"0","1","2","3","4","5","6","7","8"};
        String[] groups={"A","B","C","D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,years);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classYearSpinner.setAdapter(adapter);
        groupSpinner.setAdapter(adapter2);


        //Registarion and saving data to database

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 fullName = fullNameTextView.getText().toString();
                 username = usernameTextView.getText().toString();
                 password = passwordTextView.getText().toString();
                 checkPassword = checkPasswordTextView.getText().toString();
                 classYear = classYearSpinner.getSelectedItem().toString();
                 group = groupSpinner.getSelectedItem().toString();



                 if(fullName.isEmpty() || username.isEmpty() || password.isEmpty() || checkPassword.isEmpty() || classYear.isEmpty()){
                     Toast.makeText(getContext(), "Tölsd ki mindent mezőt.", Toast.LENGTH_SHORT).show();
                 }else{
                     String genaretedDiakID=diakIDGenerator(diakokList);
                     Log.d("RegStudent",genaretedDiakID);

                     saveUserData(fullName,password,classYear,group,username,genaretedDiakID);
                 }


            }
        });

            return view;
    }
    public void saveUserData(String fullName,String password,  String classYear,String group, String username,String diakID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String diakIDs=databaseReference.child("diakok").push().getKey();


        UserData.Diakok diak = new UserData.Diakok(fullName,password,classYear,group,username,diakID);
        databaseReference.child("diakok").child(diakIDs).setValue(diak)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Registration", "Sikeres regisztráció");
                        Toast.makeText(getContext(), "Sikeres regisztrálás", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new StudentRegistrationFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Registration", "Sikertelen regisztráció", e);
                        Toast.makeText(getContext(), "Sikertelen regisztrálás", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public String diakIDGenerator(List<UserData.Diakok> diakokList) {
        Random random = new Random();
        String diakID;
        int randomNumber;
        do {
            randomNumber = random.nextInt(1000);
            diakID = "D-" + randomNumber;
            Log.d("RegStudent", "diakID: " + diakID);
        } while (containsDiakID(diakokList, diakID));
        return diakID;
    }

    private boolean containsDiakID(List<UserData.Diakok> diakokList, String diakID) {
        for (UserData.Diakok diak : diakokList) {
            if (diak.getDiakID().equals(diakID)) {
                Log.d("RegStudent", "containsDiakID: true");
                return true;
            }
        }
        Log.d("RegStudent", "containsDiakID: false");
        return false;
    }
}
