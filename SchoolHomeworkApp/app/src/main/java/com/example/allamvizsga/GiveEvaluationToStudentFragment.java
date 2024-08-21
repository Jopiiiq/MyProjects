package com.example.allamvizsga;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GiveEvaluationToStudentFragment extends Fragment {
    String homeworkID;
    String homeworkTitle;
    String homeworkDate;
    String homeworkTime;
    String homeworkDescription;
    String className;
    String selectedDate;
    String selectedTime;
    String description;
    String tantargyID;
    String trueTantargyID;
    String studentName;
    String studentID;
    String goodTrueHomeworkIdInStudent;

    TextView evaluationHomeworkDate;
    TextView evaluationHomeworkTime;
    TextView evaloutionSolutionText;
    TextView evaluationStudentName;
    TextView evaluationHomeworkName;
    private RatingBar howFastStudentRating;
    private RatingBar howAccurateStudentRating;
    private RatingBar howGrownStudentRating;
    private RatingBar howUniqueStudentRating;
    String solution;
    Button giveEvaluationButton;

    public GiveEvaluationToStudentFragment() {
        // Required empty public constructor
    }

    public static GiveEvaluationToStudentFragment newInstance() {
        return new GiveEvaluationToStudentFragment();
    }
    public interface SolutionCallback {
        void onSolutionReceived(String solution);
        void onError(String error);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_give_evaluation_to_student, container, false);
        //((InClass) getActivity()).setBackgroundBlur(true);
        Bundle bundle = getArguments();
        //Get needed data from previous fragment
        if (bundle != null) {
             homeworkID = bundle.getString("homeworkID");
             Log.d("GiveEvaluationToStudentFragment", "homeworkID: " + homeworkID);
             homeworkTitle = bundle.getString("homeworkTitle");
             homeworkDate = bundle.getString("homeworkDate");
             homeworkTime = bundle.getString("homeworkTime");
             homeworkDescription = bundle.getString("homeworkDescription");
             className = bundle.getString("className");
             selectedDate = bundle.getString("selectedDate");
             selectedTime = bundle.getString("selectedTime");
             description = bundle.getString("description");
             tantargyID = bundle.getString("tantargyID");
             trueTantargyID = bundle.getString("trueTantargyID");
             Log.d("GiveEvaluationToStudentFragment", "trueTantargyID: " + trueTantargyID);
             studentName=bundle.getString("studentName");
             Log.d("GiveEvaluationToStudentFragment", "studentName: " + studentName);
             studentID = bundle.getString("studentID");
             goodTrueHomeworkIdInStudent = bundle.getString("goodTrueHomeworkIdInStudent");
             Log.d("GiveEvaluationToStudentFragment", "studentID: " + studentID);


        }

        evaluationHomeworkDate = view.findViewById(R.id.evaluationHomeworkDate);
        evaluationHomeworkTime = view.findViewById(R.id.evaluationHomeworkTime);
        evaloutionSolutionText = view.findViewById(R.id.evaloutionSolutionText);
        evaluationStudentName = view.findViewById(R.id.evaluationStudentName);
        evaluationHomeworkName = view.findViewById(R.id.evaluationHomeworkName);
        howFastStudentRating = view.findViewById(R.id.howFastStudentRating);
        howAccurateStudentRating = view.findViewById(R.id.howAccurateStudentRating);
        howGrownStudentRating = view.findViewById(R.id.howGrownStudentRating);
        howUniqueStudentRating = view.findViewById(R.id.howUniqueStudentRating);

        giveEvaluationButton = view.findViewById(R.id.evaluationButton);
        giveEvaluationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.classNewFragment); // R.id.fragment_container helyére írd be a saját Fragment Containered ID-ját
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                }
            }
        });



        evaluationHomeworkDate.setText(homeworkDate);
        evaluationHomeworkTime.setText(homeworkTime);
        evaluationStudentName.setText(studentName);
        evaluationHomeworkName.setText(homeworkTitle);
        //get Solution
        getStudentSolution(new SolutionCallback() {
            @Override
            public void onSolutionReceived(String receivedSolution) {
                solution = receivedSolution;
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            evaloutionSolutionText.setText(solution);
                            Log.d("GiveEvaluationToStudentFragment", "solution: " + solution);
                        }
                    });
                }
            }
            @Override
            public void onError(String error) {

            }
        });

        howFastStudentRating.setRating(3.5f);

        howFastStudentRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveRatingInDatabase(rating,"howFastStudentSolved");
            }
        });


        howAccurateStudentRating.setRating(3.5f);
        howAccurateStudentRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveRatingInDatabase(rating,"howAccurateStudentSolved");

            }
        });

        howGrownStudentRating.setRating(3.5f);

        howGrownStudentRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveRatingInDatabase(rating,"howGrownStudentSolved");
            }
        });


        howUniqueStudentRating.setRating(3.5f);

        howUniqueStudentRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveRatingInDatabase(rating,"howUniqueStudentSolved");
            }
        });



        return view;

    }


    public void getStudentSolution(SolutionCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("diakok")
                .child(studentID)
                .child("homework");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String homeworkIdFromDB = ds.child("homeworkID").getValue(String.class);
                    if (homeworkIdFromDB != null && homeworkIdFromDB.equals(goodTrueHomeworkIdInStudent)) {
                        String solution = ds.child("solution").getValue(String.class);
                        if (solution != null) {
                            callback.onSolutionReceived(solution);
                        } else {
                            callback.onError("Solution not found");
                        }
                        return;
                    }
                }
                callback.onError("Homework not found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void saveRatingInDatabase(float rating, String ratingName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("diakok")
                .child(studentID)
                .child("homework");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(calendar.getTime());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String homeworkIdFromDB = ds.child("homeworkID").getValue(String.class);
                    if (homeworkIdFromDB != null && homeworkIdFromDB.equals(goodTrueHomeworkIdInStudent)) {

                        ds.child("gradedTime").getRef().setValue(currentDateAndTime);
                        ds.child("graded").getRef().setValue(true);


                        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference()
                                .child("diakok")
                                .child(studentID)
                                .child("homework")
                                .child(ds.getKey())
                                .child("rating");

                        // Olvasd be a meglévő értékeléseket
                        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ratingSnapshot) {
                                Map<String, Object> ratingData = new HashMap<>();

                                // Ha már létezik valamilyen értékelés, hozzáadjuk a meglévőkhöz
                                if (ratingSnapshot.exists()) {
                                    for (DataSnapshot existingRating : ratingSnapshot.getChildren()) {
                                        ratingData.put(existingRating.getKey(), existingRating.getValue());
                                    }
                                }

                                // Hozzáadjuk vagy frissítjük az új értékelést
                                ratingData.put(ratingName, rating);

                                ratingRef.setValue(ratingData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("GiveEvaluationFragment", "Rating successfully saved to database");
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("GiveEvaluationFragment", "Failed to read existing ratings", error.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GiveEvaluationFragment", "Database error", error.toException());
            }
        });
    }






}
