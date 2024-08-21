package com.example.allamvizsga;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class SolveOngoingHomework extends Fragment {
    private String selectedHomeworkTitle;
    private String selectedHomeworkDate;
    private String selectedHomeworkTime;
    private String description;
    private String homeworkID;
    private String trueTantargyID;
    private String currentUserId;

    Button addHomeworkToFinishedOnes, writeSolutionButton;
    TextView homeworkTitleForStudent, dateOfHomeworkForStudent, timeOfHomeworkForStudent, descriptionOfHomeworkForStudent;

    public SolveOngoingHomework() {}

    public static SolveOngoingHomework newInstance() {
        return new SolveOngoingHomework();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solve_ongoing_homework, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            selectedHomeworkTitle = bundle.getString("selectedHomeworkTitle");
            selectedHomeworkDate = bundle.getString("selectedHomeworkDate");
            selectedHomeworkTime = bundle.getString("selectedHomeworkTime");
            description = bundle.getString("description");
            homeworkID = bundle.getString("homeworkID");
            trueTantargyID = bundle.getString("trueTantargyID");
            currentUserId = bundle.getString("currentUserId");
        }


        addHomeworkToFinishedOnes = view.findViewById(R.id.addHomeworkToFinishedOnes);
        writeSolutionButton = view.findViewById(R.id.writeSolutionButton);
        homeworkTitleForStudent = view.findViewById(R.id.homeworkTitle);
        dateOfHomeworkForStudent = view.findViewById(R.id.dateOfHomework);
        timeOfHomeworkForStudent = view.findViewById(R.id.timeOfHomework);
        descriptionOfHomeworkForStudent = view.findViewById(R.id.descriptionForHomework);

        homeworkTitleForStudent.setText(selectedHomeworkTitle);
        dateOfHomeworkForStudent.setText(selectedHomeworkDate);
        timeOfHomeworkForStudent.setText(selectedHomeworkTime);
        descriptionOfHomeworkForStudent.setText(description);

        writeSolutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForHomeworkSolutionInputFragment fragment = new ForHomeworkSolutionInputFragment();

                // Pass the necessary data as arguments to the fragment
                Bundle args = new Bundle();
                args.putString("homeworkID", homeworkID);
                args.putString("currentUserId", currentUserId);
                fragment.setArguments(args);

                // Begin a transaction to replace the current fragment with the input fragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down);
                transaction.replace(R.id.classNewFragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        addHomeworkToFinishedOnes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Register a FragmentResultListener to receive the solution from ForHomeworkSolutionInputFragment
        getParentFragmentManager().setFragmentResultListener("solutionKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String solution = result.getString("solution");
                // Use the received solution here
                Log.d("SolveOngoingHomework", "Received solution: " + solution);
                // Now you can save the solution to Firebase or perform any other actions
                saveSolutionToFirebase(solution);
            }
        });

        return view;
    }

    private void saveSolutionToFirebase(String solution) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("diakok").child(currentUserId).child("homework").child(homeworkID);
        databaseReference.child("finished").setValue(true); // Assuming homework is finished when solution is added
        databaseReference.child("solution").setValue(solution);
        databaseReference.child("solutionTime").setValue(ServerValue.TIMESTAMP);
        Log.d("SolveOngoingHomework", "Homework added to finished ones with solution: " + solution);
    }
}
