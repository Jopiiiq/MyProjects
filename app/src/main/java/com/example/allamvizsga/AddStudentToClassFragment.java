package com.example.allamvizsga;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.allamvizsga.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AddStudentToClassFragment extends Fragment {
    String tantargyID;
    String trueTantargyID;


    ArrayList<String> studentNames;

    public AddStudentToClassFragment() {
        // Üres konstruktor kötelező
    }

    public static AddStudentToClassFragment newInstance(String param1, String param2) {
        AddStudentToClassFragment fragment = new AddStudentToClassFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_student, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            tantargyID = bundle.getString("tantargyID");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak");

        databaseReference.orderByChild("tantargyID").equalTo(tantargyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        trueTantargyID = dataSnapshot.getKey();
                        Log.d("AddStudentToClassFragment", "trueTantargyID: " + trueTantargyID);

                        processStudentList(view);
                    }
                } else {

                    Log.d("AddStudentToClassFragment", "Nem található a megfelelő tantárgy a Firebase adatbázisban.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("AddStudentToClassFragment", "Firebase lekérdezés megszakítva: " + error.getMessage());
            }
        });

        return view;
    }
    //Show the list of students wanting to join
    private void processStudentList(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("tantargyak")
                .child(trueTantargyID)
                .child("diakWantedToJoin");

        studentNames = new ArrayList<>();
        ArrayList<String> studentIds = new ArrayList<>();
        ListView listView = view.findViewById(R.id.chooseStudentListView);
        StudentNameAdapter adapter = new StudentNameAdapter(requireContext(), studentNames);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot diakWantedToJoinSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : diakWantedToJoinSnapshot.getChildren()) {
                     String studentId=childSnapshot.getKey();
                     String wantedToJoinValue = childSnapshot.getValue(String.class);
                     assert wantedToJoinValue != null;
                        if(wantedToJoinValue.equals("false")){
                            studentIds.add(studentId);
                            DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference()
                                    .child("diakok")
                                    .child(studentId);
                            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String studentName = dataSnapshot.child("nev").getValue(String.class);
                                    Log.d("AddStudentToClassFragment", "studentName: " + studentName);
                                    Log.d("AddStudentToClassFragment", "studentId: " + studentId);
                                    studentNames.add(studentName);

                                    adapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("AddStudentToClassFragment", "Firebase lekérdezés megszakítva: " + error.getMessage());
                                }
                            });
                        }



                    }
                }

                // Az adapter beállítása a listához
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddStudentToClassFragment", "Firebase lekérdezés megszakítva: " + error.getMessage());
            }
        });

        listView.setAdapter(adapter);
        //The button will use updateStudentStatus to fill up the ListView with the student Names and Id
        // of the selected students wanting to join and accept them
        Button choosenStudents = view.findViewById(R.id.choosenStudents);
        choosenStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedStudentIds = new ArrayList<>();
                ArrayList<Boolean> selectedItems = adapter.getSelectedItems();
                assert selectedItems != null;

                Log.d("AddStudentToClassFragment", "getSelected: " + selectedItems.toString());
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.get(i)) {
                        selectedStudentIds.add(studentIds.get(i));
                    }
                }
                Log.d("AddStudentToClassFragment", "selectedStudents: " + selectedStudentIds);
                updateStudentStatus(selectedStudentIds);
                adapter.notifyDataSetChanged();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processStudentList(view);
                    }
                }, 500);
            }
        });
    }

    private void updateStudentStatus(ArrayList<String> selectedStudentIds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("tantargyak")
                .child(trueTantargyID)
                .child("diakWantedToJoin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String studentId : selectedStudentIds) {
                    for (DataSnapshot diakSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot studentSnapshot = diakSnapshot.child(studentId);
                        if (studentSnapshot.exists()) {
                            String wantedToJoinValue = studentSnapshot.getValue(String.class);
                            if (wantedToJoinValue != null && wantedToJoinValue.equals("false")) {
                                studentSnapshot.getRef().setValue("true");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddStudentToClassFragment", "Firebase lekérdezés megszakítva: " + error.getMessage());
            }
        });
    }
}





