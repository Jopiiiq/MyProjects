package com.example.allamvizsga;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ForHomeworkSolutionInputFragment extends Fragment {
    EditText solutionEditText;
    Button saveSolutionButton;
    String homeworkID, currentUserId;
    public ForHomeworkSolutionInputFragment() {
        // Required empty public constructor
    }
    public interface OnSolutionEnteredListener {
        void onSolutionEntered(String solution);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_homework_solution_input, container, false);

        solutionEditText = view.findViewById(R.id.solutionEditText);
        saveSolutionButton = view.findViewById(R.id.saveSolutionButton);
        Bundle bundle = getArguments();

        if (bundle != null) {
            homeworkID = bundle.getString("homeworkID");
            currentUserId = bundle.getString("currentUserId");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("diakok").child(currentUserId).child("homework").child(homeworkID);
            databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String solution = snapshot.child("solution").getValue(String.class);
                        Log.d("ForHomeworkSolutionInputFragment", "Solution: " + solution);
                        solutionEditText.setText(solution);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




        saveSolutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String solution = solutionEditText.getText().toString();
                Log.d("ForHomeworkSolutionInputFragment", "Solution: " + solution);
                Bundle bundle = new Bundle();
                bundle.putString("solution", solution);
                getParentFragmentManager().setFragmentResult("solutionKey", bundle);
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

}