package com.example.allamvizsga;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChooseStudentFragment extends Fragment {
    Button choosenStudents;

    String tantargyID;
    String trueTantargyID;
    String studentFirebaseID;
    String TrueStudentFirebaseID;
    String homeworkTitle;
    String selectedDate;
    String selectedTime;
    String description;



    ArrayList<String> studentsIDs = new ArrayList<>();
    private ArrayList<String> studentsNames = new ArrayList<>();


    public ChooseStudentFragment() {
        // Required empty public constructor
    }

    public static ChooseStudentFragment newInstance() {
        return new ChooseStudentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_student, container, false);
        choosenStudents = view.findViewById(R.id.choosenStudents);
        choosenStudents.setBackgroundColor(Color.RED);

        Bundle bundle = getArguments();

        if (bundle != null) {
            tantargyID = bundle.getString("tantargyID");
            studentFirebaseID = bundle.getString("studentFirebaseID");
            homeworkTitle = bundle.getString("homeworkTitle");
            selectedDate = bundle.getString("selectedDate");
            selectedTime = bundle.getString("selectedTime");
            description = bundle.getString("description");



            Log.d("ChooseStudentFragment", "tantargyID: " + tantargyID);
        }
        DatabaseReference trueIdInFirebase = FirebaseDatabase.getInstance().getReference().child("tantargyak");

        trueIdInFirebase.orderByChild("tantargyID").equalTo(tantargyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        trueTantargyID = dataSnapshot.getKey();
                        Log.d("ChooseStudentFragment", "trueTantargyID: " + trueTantargyID);

                        initListView(view);
                    }
                    choosenStudents.setEnabled(true);
                } else {
                    Log.d("ChooseStudentFragment", "Nem található a megfelelő tantárgy a Firebase adatbázisban.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChooseStudentFragment", "Firebase lekérdezés megszakítva: " + error.getMessage());
            }
        });
        DatabaseReference trueStudentID = FirebaseDatabase.getInstance().getReference().child("diakok");

        trueStudentID.orderByChild("studentFirebaseID").equalTo(studentFirebaseID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TrueStudentFirebaseID = dataSnapshot.getKey();
                        Log.d("ChooseStudentFragment", "studentFirebaseID: " + studentFirebaseID);
                    }
                } else {
                    Log.d("ChooseStudentFragment", "Nem található a megfelelő diak a Firebase adatbázisban.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        choosenStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String homeworkId = homeworkIDCreator();
                Map<String, Object> diakIdMap = new HashMap<>();
                for (String studentId : studentsIDs) {


                    DatabaseReference studentHomeworkRef = databaseReference.child("diakok").child(studentId).child("homework").child(homeworkId);

                    studentHomeworkRef.child("homeworkTitle").setValue(homeworkTitle);
                    studentHomeworkRef.child("tantargyID").setValue(trueTantargyID);
                    studentHomeworkRef.child("homeworkID").setValue(homeworkId);
                    studentHomeworkRef.child("homeworkDescription").setValue(description);
                    studentHomeworkRef.child("homeworkDate").setValue(selectedDate);
                    studentHomeworkRef.child("homeworkTime").setValue(selectedTime);
                    studentHomeworkRef.child("finished").setValue(false);
                    studentHomeworkRef.child("ongoingOrNot").setValue(true);
                    Log.d("ChooseStudentFragment", "Homework added for student: " + studentId);

                    DatabaseReference classHomeworkRef = databaseReference.child("tantargyak").child(trueTantargyID).child("homework").child(homeworkId);
                    classHomeworkRef.child("homeworkTitle").setValue(homeworkTitle);
                    classHomeworkRef.child("diakID").setValue(studentId);
                    classHomeworkRef.child("homeworkID").setValue(homeworkId);
                    classHomeworkRef.child("homeworkDate").setValue(selectedDate);
                    classHomeworkRef.child("homeworkTime").setValue(selectedTime);
                    classHomeworkRef.child("homeworkDescription").setValue(description);
                    classHomeworkRef.child("tantargyID").setValue(trueTantargyID);
                    classHomeworkRef.child("ongoingOrNot").setValue(true);
                    classHomeworkRef.child("diakID").child(studentId).setValue("");
                    diakIdMap.put(studentId, "");
                    classHomeworkRef.child("diakID").updateChildren(diakIdMap);

            }


                Toast.makeText(getContext(), "Sikeresen hozzaadva", Toast.LENGTH_SHORT).show();
                Fragment HomeworkCreaterFragment = new HomeworkCreaterFagment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.classNewFragment, HomeworkCreaterFragment);
                transaction.addToBackStack(null);

                transaction.commit();
                initListView(getView());

            }
        });

        return view;
    }

    private void initListView(View view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak").child(trueTantargyID).child("diakWantedToJoin");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                for (DataSnapshot diakWantedToJoin : snapshot.getChildren()) {
                    for (DataSnapshot childSnapshoot : diakWantedToJoin.getChildren()) {
                        String studentId = childSnapshoot.getKey();
                        String wantedToJoinValue = childSnapshoot.getValue(String.class);
                        assert wantedToJoinValue != null;
                        if (wantedToJoinValue.equals("true")) {
                            studentsIDs.add(studentId);
                            DatabaseReference studentNamesFirebase = FirebaseDatabase.getInstance().getReference().child("diakok").child(studentId);
                            studentNamesFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String studentName = snapshot.child("nev").getValue(String.class);
                                    Log.d("ChooseStudentFragment", "studentName: " + studentName);
                                    studentsNames.add(studentName);
                                    if (studentsNames.size() == studentsIDs.size()) {
                                        ListView listView = view.findViewById(R.id.chooseStudentListView);
                                        StudentNameAdapter adapter = new StudentNameAdapter(getContext(), studentsNames);
                                        listView.setAdapter((ListAdapter) adapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("ChooseStudentFragment", "Nem tölti fel a StudentName: " + studentId + " " + error.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String homeworkIDCreator() {
        return "H-" + UUID.randomUUID().toString();
    }


}
