package com.example.allamvizsga;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GetViewGradedHomework extends Fragment {
    private String currentUserId;

    private String homeworkDate;
    private String homeworkTime;
    private String homeworkTitle;
    private String homeworkID;

    private String solution;
    private String classID;

    private String howFastStudentRating;
    private String howAccurateStudentRating;
    private String howGrownStudentRating;
    private String howUniqueStudentRating;


    private String description;
private String gradedTime;
    private GetViewGradedHomeworkAdapter adapter;
    public GetViewGradedHomework() {
        // Required empty public constructor
    }

    public static GetViewGradedHomework newInstance() {
        return new GetViewGradedHomework();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_view_graded_homework, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");
        Log.d("GetViewGradedHomework", currentUserId);

        adapter = new GetViewGradedHomeworkAdapter(getContext());

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        fetchHomeworkData();
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Az adapterből kinyerjük az adatokat a kattintott pozíció alapján
            homeworkTitle = adapter.mHomeworkTitle.get(position);
            gradedTime = adapter.mGradedTime.get(position);

            // Bundle létrehozása és adatok hozzáadása
            Bundle bundle = new Bundle();
            bundle.putString("homeworkTitle", homeworkTitle);
            bundle.putString("homeworkDate", homeworkDate);
            bundle.putString("homeworkTime", homeworkTime);
            bundle.putString("homeworkID", homeworkID);
            bundle.putString("description", description);
            bundle.putString("solution", solution);
            bundle.putString("classID", classID);
            bundle.putString("howFastStudentRating", howFastStudentRating);
            bundle.putString("howAccurateStudentRating", howAccurateStudentRating);
            bundle.putString("howGrownStudentRating", howGrownStudentRating);
            bundle.putString("howUniqueStudentRating", howUniqueStudentRating);
            bundle.putString("gradedTime", gradedTime);


            ViewGradedHomework fragment = new ViewGradedHomework();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.classNewFragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });





        return view;
    }

    private void fetchHomeworkData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("diakok").child(currentUserId).child("homework");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("GetViewGradedHomework", "snapshot exists: " + snapshot.exists());
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("GetViewGradedHomework", "Processing ds: " + ds.getKey());
                        homeworkTitle = ds.child("homeworkTitle").getValue(String.class);
                        Boolean gradedValue = ds.child("graded").getValue(Boolean.class);
                        String gradedTime = ds.child("gradedTime").getValue(String.class);


                        if (homeworkTitle != null && gradedValue != null && gradedValue) {
                            if(ds.child("rating").exists()) {
                                Long howFastStudentSolved = ds.child("rating").child("howFastStudentSolved").getValue(Long.class);
                                Long howAccurateStudentSolved = ds.child("rating").child("howAccurateStudentSolved").getValue(Long.class);
                                Long howGrownStudentSolved = ds.child("rating").child("howGrownStudentSolved").getValue(Long.class);
                                Long howUniqueStudentSolved = ds.child("rating").child("howUniqueStudentSolved").getValue(Long.class);

                                if (howFastStudentSolved != null) {
                                    howFastStudentRating = String.valueOf(howFastStudentSolved);
                                }else{
                                    howFastStudentRating = "0";
                                }
                                if (howAccurateStudentSolved != null) {
                                    howAccurateStudentRating = String.valueOf(howAccurateStudentSolved);
                                }else{
                                    howAccurateStudentRating = "0";
                                }

                                if (howGrownStudentSolved != null) {
                                    howGrownStudentRating = String.valueOf(howGrownStudentSolved);
                                }
                                else{
                                    howGrownStudentRating = "0";
                                }
                                if (howUniqueStudentSolved != null) {
                                    howUniqueStudentRating = String.valueOf(howUniqueStudentSolved);
                                }
                                else{
                                    howUniqueStudentRating = "0";
                                }


                                // Only add to adapter if gradedValue is true
                                homeworkTitle = ds.child("homeworkTitle").getValue(String.class);
                                Log.d("GetViewGradedHomework", "homeworkTitle: " + homeworkTitle);
                                homeworkDate = ds.child("homeworkDate").getValue(String.class);
                                Log.d("GetViewGradedHomework", "homeworkDate: " + homeworkDate);
                                homeworkTime = ds.child("homeworkTime").getValue(String.class);
                                Log.d("GetViewGradedHomework", "homeworkTime: " + homeworkTime);
                                homeworkID = ds.child("homeworkID").getValue(String.class);
                                Log.d("GetViewGradedHomework", "homeworkID: " + homeworkID);
                                description = ds.child("homeworkDescription").getValue(String.class);
                                Log.d("GetViewGradedHomework", "homeworkDescription: " + description);
                                solution = ds.child("solution").getValue(String.class);
                                classID = ds.child("TantargyID").getValue(String.class);


                                Log.d("GetViewGradedHomework", "solution: " + solution);
                                Log.d("GetViewGradedHomework", "howAccurateStudentRating: " + howAccurateStudentRating);
                                Log.d("GetViewGradedHomework", "howGrownStudentRating: " + howGrownStudentRating);
                                Log.d("GetViewGradedHomework", "howUniqueStudentRating: " + howUniqueStudentRating);
                                Log.d("GetViewGradedHomework", "howFastStudentRating: " + howFastStudentRating);


                                adapter.addViewGradedHomework(homeworkTitle, gradedTime);
                                adapter.notifyDataSetChanged();
                                Log.d("GetViewGradedHomework", "Added to adapter: " + homeworkTitle + ", " + gradedTime);
                            }
                        } else {
                            Log.w("GetViewGradedHomework", "Homework title or graded time is null for ds: " + ds.getKey());
                        }
                    }
                } else {
                    Log.d("GetViewGradedHomework", "Snapshot does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GetViewGradedHomework", "Database error: " + error.getMessage());
            }
        });
    }
}
