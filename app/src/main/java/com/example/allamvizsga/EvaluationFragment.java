package com.example.allamvizsga;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.HashMap;

public class EvaluationFragment extends Fragment {

    private ListView finishedHomeworkListView;
    String currentUserId;
    private HomeworkViewForStudentAdapter adapter;
    String className;
    String selectedDate;
    String selectedTime;
    String description;
    String tantargyID;
    String trueTantargyID;
    String studentName;
    String studentID;
    String studentNameToSave;

    private ArrayList<String> homeworkTitles = new java.util.ArrayList<>();
    private ArrayList<String> homeworkDates = new ArrayList<>();
    private ArrayList<String> homeworkTimes = new ArrayList<>();
    private ArrayList<String> homeworkDescriptions = new ArrayList<>();
    private ArrayList<String> studentNames = new ArrayList<>();
    private ArrayList<String> studentIDs = new ArrayList<>();
    private ArrayList<String> goodTrueHomeworkIdInStudentList = new ArrayList<>();
    private ArrayList<String> homeworkIDs = new ArrayList<>();


    public EvaluationFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);

        finishedHomeworkListView = view.findViewById(R.id.finishedHomeworkListView);

        // Retrieve currentUserId from SharedPreferences
        Context context = getActivity();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", Context.MODE_PRIVATE);
            currentUserId = sharedPreferences.getString("currentUserId", "");
            Log.d("EvaluationFragment", "Retrieved currentUserId: " + currentUserId);
        } else {
            Log.e("EvaluationFragment", "Activity is null");
        }

        // Initialize adapter
        adapter = new HomeworkViewForStudentAdapter(getContext(), homeworkTitles, homeworkDates, homeworkTimes, studentNames);
        finishedHomeworkListView.setAdapter(adapter);

        // Retrieve bundle arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            className = bundle.getString("className");
            selectedDate = bundle.getString("selectedDate");
            selectedTime = bundle.getString("selectedTime");
            description = bundle.getString("description");
            tantargyID = bundle.getString("tantargyID");
        }

        // Fetch data from Firebase based on tantargyID
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak");
        databaseReference.orderByChild("tantargyID").equalTo(tantargyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        trueTantargyID = dataSnapshot.getKey();
                        Log.d("EvaluationFragment", "trueTantargyID: " + trueTantargyID);
                        improvedProcessFinishedHomeworkList();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EvaluationFragment", "Database query cancelled: " + error.getMessage());
            }
        });

        // Handle item click on finishedHomeworkListView
        finishedHomeworkListView.setOnItemClickListener((parent, view1, position, id) -> {
            String homeworkID = homeworkIDs.get(position);
            String homeworkTitle = homeworkTitles.get(position);
            String homeworkDate = homeworkDates.get(position);
            String homeworkTime = homeworkTimes.get(position);
            String homeworkDescription = homeworkDescriptions.get(position);
            String studentName = studentNames.get(position);
            String studentID = studentIDs.get(position);
            String goodTrueHomeworkIdInStudent = goodTrueHomeworkIdInStudentList.get(position);

            Bundle args = new Bundle();
            args.putString("homeworkID", homeworkID);
            args.putString("homeworkTitle", homeworkTitle);
            args.putString("homeworkDate", homeworkDate);
            args.putString("homeworkTime", homeworkTime);
            args.putString("homeworkDescription", homeworkDescription);
            args.putString("className", className);
            args.putString("selectedDate", selectedDate);
            args.putString("selectedTime", selectedTime);
            args.putString("description", description);
            args.putString("tantargyID", tantargyID);
            args.putString("trueTantargyID", trueTantargyID);
            args.putString("studentName", studentNameToSave);
            Log.d("EvaluationFragment", "studentNameToSave: " + studentNameToSave);
            args.putString("studentID", studentID);
            args.putString("goodTrueHomeworkIdInStudent", goodTrueHomeworkIdInStudent);


            GiveEvaluationToStudentFragment viewHomeworkFragment = new GiveEvaluationToStudentFragment();
            viewHomeworkFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.classNewFragment, viewHomeworkFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }



    public void improvedProcessFinishedHomeworkList() {
        Log.d("HomeworkProcessing", "Starting processFinishedHomeworkList...");

        // Clear lists before fetching new data
        homeworkTitles.clear();
        homeworkDates.clear();
        homeworkTimes.clear();
        homeworkDescriptions.clear();
        homeworkIDs.clear();
        studentNames.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("tantargyak")
                .child(trueTantargyID)
                .child("homework");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HomeworkProcessing", "onDataChange: Received " + snapshot.getChildrenCount() + " homework items.");

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("ongoingOrNot").exists()) {
                        boolean ongoingOrNot = ds.child("ongoingOrNot").getValue(Boolean.class);
                        Boolean graded = ds.child("graded").getValue(Boolean.class);
                        Log.d("HomeworkProcessing", "ongoingOrNot: " +  ds.getKey() + ", graded: " + graded);
                        Log.d("HomeworkProcessing", "Processing homework ID: " + ds.getKey() + ", ongoingOrNot: " + ongoingOrNot);

                        if (graded != null) {
                            Log.d("HomeworkProcessing", "Skipping already graded homework: " + ds.getKey());
                            continue;
                        }

                        if (!ongoingOrNot ) {
                            String homeworkID = ds.child("homeworkID").getValue(String.class);
                            Log.d("HomeworkProcessing", "belepet: " );

                            // Check if diakID is HashMap or String
                            Object diakIDObject = ds.child("diakID").getValue();
                            if (diakIDObject instanceof HashMap) {
                                // If diakID is a HashMap
                                HashMap<String, Object> diakIDMap = (HashMap<String, Object>) diakIDObject;
                                for (String idKey : diakIDMap.keySet()) {
                                    String studentID = idKey;
                                    Log.d("HomeworkProcessing", "Student ID from diakID: " + studentID);
                                    if (!studentID.isEmpty()) {
                                        Log.d("HomeworkProcessing", "Processing student homework for ID: " + studentID);
                                        processStudentHomework(homeworkID, studentID);
                                    }
                                }
                            } else if (diakIDObject instanceof String) {
                                // If diakID is a String (fallback mechanism if the structure changes)
                                String studentID = (String) diakIDObject;
                                Log.d("HomeworkProcessing", "Student ID from diakID: " + studentID);
                                if (!studentID.isEmpty()) {
                                    Log.d("HomeworkProcessing", "Processing student homework for ID: " + studentID);
                                    processStudentHomework(homeworkID, studentID);
                                }
                            } else {
                                Log.e("HomeworkProcessing", "Unexpected diakID format: " + diakIDObject.getClass().getSimpleName());
                                // Handle unexpected format as per your application logic
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeworkProcessing", "databaseReference onCancelled: " + error.getMessage());
            }
        });
    }

    private void processStudentHomework(String homeworkID, String studentID) {
        DatabaseReference getStudentRef = FirebaseDatabase.getInstance().getReference().child("diakok").child(studentID);

        getStudentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("homework")) {
                        for (DataSnapshot ds : snapshot.child("homework").getChildren()) {
                            if (ds.hasChild("homeworkID")) {
                                String save = ds.child("homeworkID").getValue(String.class);
                                if (save != null && save.equals(homeworkID) && !ds.child("graded").exists()) {
                                    {
                                        studentNameToSave = snapshot.child("nev").getValue(String.class);
                                        studentNames.add(studentNameToSave);
                                        Log.d("HomeworkProcessing1", "studentNameToSave: " + studentNameToSave);
                                    }
                                }
                            }
                        }
                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


            DatabaseReference getSolution = FirebaseDatabase.getInstance().getReference()
                .child("diakok")
                .child(studentID)
                .child("homework");

        getSolution.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("HomeworkProcessing1", "getSolution onDataChange: Received " + snapshot.getChildrenCount() + " solutions.");
                for (DataSnapshot secondds : snapshot.getChildren()) {
                    String studentHomeworkID = secondds.child("homeworkID").getValue(String.class);
                    Boolean graded = secondds.child("graded").getValue(Boolean.class);
                    if(graded != null) {
                        Log.d("HomeworkProcessing1", "Skipping already graded solution: " + secondds.getKey());

                    }else {
                        if (studentHomeworkID != null && studentHomeworkID.equals(homeworkID) ) {
                            Log.d("HomeworkProcessing1", "secondds.getKey(): " + secondds.getKey());
                            Log.d("HomeworkProcessing1", "homeworkID: " + homeworkID);
                            // Add homework details to lists
                            homeworkTitles.add(secondds.child("homeworkTitle").getValue(String.class));
                            homeworkDates.add(secondds.child("homeworkDate").getValue(String.class));
                            homeworkTimes.add(secondds.child("homeworkTime").getValue(String.class));
                            homeworkDescriptions.add(secondds.child("homeworkDescription").getValue(String.class));
                            homeworkIDs.add(secondds.getKey());

                            goodTrueHomeworkIdInStudentList.add(homeworkID);
                            studentIDs.add(studentID);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeworkProcessing", "getSolution onCancelled: " + error.getMessage());
            }
        });

    }
}
