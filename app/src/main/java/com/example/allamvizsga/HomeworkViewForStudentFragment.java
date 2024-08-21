package com.example.allamvizsga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeworkViewForStudentFragment extends Fragment {
    Button stillOngoingHomework, wroteHomework;
    ListView homeworkViewForStudent;
    private ArrayList<String> homeworkTitles = new ArrayList<>();
    private ArrayList<String> homeworkDates = new ArrayList<>();
    private ArrayList<String> homeworkTimes = new ArrayList<>();
    private ArrayList<String> homeworkDescriptions = new ArrayList<>();
    private ArrayList<String> homeworkIDsList = new ArrayList<>();

    private ArrayList<String> homeworkIDs = new ArrayList<>();
    private HomeworkViewForStudentAdapter adapter;
    String tantargyID;
    Boolean homeworkOngoingOrNot;
    String studentFirebaseID;
    String className;
    String selectedDate;
    String selectedTime;
    String description;
    String trueTantargyID;
    String currentUserId;
    private CloudFunction cloudFunctionClient;


    public HomeworkViewForStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test2, container, false);
        stillOngoingHomework = view.findViewById(R.id.stillOngoingHomework);
        wroteHomework = view.findViewById(R.id.wroteHomework);
        cloudFunctionClient = new CloudFunction();
        callCloudFunction();
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");


        if (currentUserId != null && !currentUserId.isEmpty()) {
            Log.d("HomeworkViewForStudentFragment", "onCreateView: " + currentUserId);
            Bundle bundle = getArguments();
            if (bundle != null) {

                tantargyID = bundle.getString("tantargyID");

                studentFirebaseID = bundle.getString("studentFirebaseID");

                className = bundle.getString("className");

                selectedDate = bundle.getString("selectedDate");

                selectedTime = bundle.getString("selectedTime");


            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak");
            homeworkViewForStudent = view.findViewById(R.id.homeworkViewForStudent);

           adapter = new HomeworkViewForStudentAdapter(getContext(), homeworkTitles, homeworkDates, homeworkTimes,null);
            homeworkViewForStudent.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            DatabaseReference trueDatabaseReference = FirebaseDatabase.getInstance().getReference().child("diakok").child(currentUserId).child("homework");
            trueDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Access the "tantargyID" directly under the "diakok" node
                        String tantargyID = snapshot.child("tantargyID").getValue(String.class);
                        Log.d("HomeworkViewForStudentFragment", "tantargyID: " + tantargyID);
                        trueTantargyID=tantargyID;
                    }

                    Log.d("HomeworkViewForStudentFragment", "tantargyID: " + trueTantargyID);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });


            stillOngoingHomework.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    callCloudFunction();
                    Log.d("HomeworkViewFragment", "Still Ongoing Homework button clicked");
                    processHomeworkListTrue();
                }
            });

            wroteHomework.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    callCloudFunction();
                    Log.d("HomeworkViewFragment", "Wrote Homework button clicked");
                    processHomeworkListFalse();
                }
            });
        }
        homeworkViewForStudent.setOnItemClickListener((parent, view1, position, id) -> {
            callCloudFunction();

            String selectedHomeworkTitle = homeworkTitles.get(position);
            String selectedHomeworkDate = homeworkDates.get(position);
            String selectedHomeworkTime = homeworkTimes.get(position);
            String selectedDescription = homeworkDescriptions.get(position);
            String selectedHomeworkID = homeworkIDs.get(position);


            Fragment fragment = new SolveOngoingHomework();

            Bundle bundle = new Bundle();
            bundle.putString("selectedHomeworkTitle", selectedHomeworkTitle);
            bundle.putString("selectedHomeworkDate", selectedHomeworkDate);
            bundle.putString("selectedHomeworkTime", selectedHomeworkTime);
            bundle.putString("description", String.valueOf(homeworkDescriptions));
            bundle.putString("homeworkID", selectedHomeworkID);
            bundle.putString("trueTantargyID", trueTantargyID);

            bundle.putString("currentUserId", currentUserId);
            Log.d("HomeworkViewForStudentFragment", "currentUserId: " + currentUserId);
            fragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.classNewFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();






        });



        return view;
    }

    private void processHomeworkListTrue() {
        callCloudFunction();
        homeworkTitles.clear();
        homeworkDates.clear();
        homeworkTimes.clear();
        homeworkDescriptions.clear();
        homeworkIDsList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("diakok")
                .child(currentUserId)
                .child("homework");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot homeworkSnapshot : snapshot.getChildren()) {
                    Boolean homeworkFinished = homeworkSnapshot.child("finished").exists() && homeworkSnapshot.child("finished").getValue(Boolean.class);
                    String title = homeworkSnapshot.child("homeworkTitle").getValue(String.class);
                    String date = homeworkSnapshot.child("homeworkDate").getValue(String.class);
                    String time = homeworkSnapshot.child("homeworkTime").getValue(String.class);
                    String description = homeworkSnapshot.child("homeworkDescription").getValue(String.class);
                    String id = homeworkSnapshot.getKey();

                    if (!homeworkFinished) {
                        homeworkTitles.add(title);
                        homeworkDates.add(date);
                        homeworkTimes.add(time);
                        homeworkDescriptions.add(description);
                        homeworkIDs.add(id);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeworkViewForStudent2", "Database error: " + error.getMessage());
            }
        });
    }

    private void processHomeworkListFalse() {
        callCloudFunction();
        homeworkTitles.clear();
        homeworkDates.clear();
        homeworkTimes.clear();
        homeworkDescriptions.clear();
        homeworkIDsList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("diakok")
                .child(currentUserId)
                .child("homework");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot homeworkSnapshot : snapshot.getChildren()) {
                    Boolean homeworkFinished = homeworkSnapshot.child("finished").exists() && homeworkSnapshot.child("finished").getValue(Boolean.class);
                    String title = homeworkSnapshot.child("homeworkTitle").getValue(String.class);
                    String date = homeworkSnapshot.child("homeworkDate").getValue(String.class);
                    String time = homeworkSnapshot.child("homeworkTime").getValue(String.class);
                    String description = homeworkSnapshot.child("homeworkDescription").getValue(String.class);
                    String id = homeworkSnapshot.getKey();

                    if (homeworkFinished) {
                        homeworkTitles.add(title);
                        homeworkDates.add(date);
                        homeworkTimes.add(time);
                        homeworkDescriptions.add(description);
                        homeworkIDs.add(id);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeworkViewForStudent2", "Database error: " + error.getMessage());
            }
        });
    }
    private void callCloudFunction() {
        cloudFunctionClient.callCloudFunction();
    }

}