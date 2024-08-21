package com.example.allamvizsga;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class RegisterForParent extends Fragment {
    EditText parentUserName;
    EditText parentPassword;
    EditText childCode;
    EditText parentFullName;
    Button parentRegistration;

    ImageView blueBackground;



    private final List<UserData.Szulok> parentList = new ArrayList<>();


    public RegisterForParent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_for_parent, container, false);
        blueBackground = view.findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(10, 5));

        Glide.with(requireContext())
                .load(R.drawable.main_background)
                .apply(requestOptions)
                .into(blueBackground);

        parentUserName = view.findViewById(R.id.parentUsername);
        parentPassword = view.findViewById(R.id.parentPassword);
        childCode = view.findViewById(R.id.childCode);
        parentFullName = view.findViewById(R.id.parentFullName);
        parentRegistration = view.findViewById(R.id.parentRegistration);

        parentRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parentUserNameText = parentUserName.getText().toString();
                String parentPasswordText = parentPassword.getText().toString();
                String childCodeText = childCode.getText().toString();
                String parentFullNameText = parentFullName.getText().toString();
                if(parentUserNameText.isEmpty() || parentPasswordText.isEmpty() || childCodeText.isEmpty() || parentFullNameText.isEmpty()){
                    Toast.makeText(getContext(), "Tölsd ki mindent mezőt", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("diakok");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                            for(DataSnapshot ds : snapshot.getChildren()){
                               String diakID = ds.child("diakID").getValue(String.class);
                               if(diakID.equals(childCodeText)){
                                   String parentID=parentIDGenerator(parentList);
                                   saveUserDataForParent(parentFullNameText,parentUserNameText,parentPasswordText,childCodeText,parentID);
                               }
                            }
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        return view;
    }

    private void saveUserDataForParent(String parentFullNameText, String parentUserNameText, String parentPasswordText,String parentID, String childCodeText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String parentok = databaseReference.child("szulok").push().getKey();


        UserData.Szulok parent = new UserData.Szulok(parentFullNameText, parentUserNameText, parentPasswordText, parentID, childCodeText);
        databaseReference.child("szulok").child(parentok).setValue(parent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Registration", "Sikeres regisztráció");
                        Toast.makeText(getContext(), "Sikeres regisztrálás", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, new LoginFragmentForParent());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Registration", "Hiba a regisztrációhoz");
                    }
                });
    }

    public String parentIDGenerator(List<UserData.Szulok> parentList) {
        Random random = new Random();
        String parentID;
        int randomNumber;
        do{
            randomNumber= random.nextInt(1000);
            parentID="P-"+randomNumber;
        }while (containsParentID(parentList, parentID));
        return parentID;
    }
    public boolean containsParentID(List<UserData.Szulok> parentList, String parentID) {
        for (UserData.Szulok parent : parentList) {
            if (parent.getSzuloID().equals(parentID)) {
                return true;
            }
        }
        return false;
    }
}
