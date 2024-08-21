package com.example.allamvizsga;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class CheckStudentHomework extends AppCompatActivity {
    TextView nameOfTheClassForParent;
    TextView nameOfThehomework;
    TextView solutionForHomework;
    TextView homeworkDeadlineDate;
    TextView homeworkDeadlineTime;
    TextView descriptionOfTheHomework;
    TextView timeOfSolution;
    TextView getHowAccurateStudent;
    TextView getHowFast;
    TextView getHowStudentGrown;
    TextView getHowUnique;
    private String diakID;
    private String classId;
    private String homeworkID;
    ImageView blueBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_student_homework);
        blueBackground = findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(3, 5));

        Glide.with(this)
                .load(R.drawable.background_for_dashboard)
                .apply(requestOptions)
                .into(blueBackground);


        // Initialize views
        nameOfTheClassForParent = findViewById(R.id.nameOfTheClassForParent);
        nameOfThehomework = findViewById(R.id.nameOfThehomework);
        solutionForHomework = findViewById(R.id.solutionForHomework);
        homeworkDeadlineDate = findViewById(R.id.homeworkDeadlineDate);
        homeworkDeadlineTime = findViewById(R.id.homeworkDeadlineTime);
        descriptionOfTheHomework = findViewById(R.id.descriptionOfHomework);
        timeOfSolution = findViewById(R.id.timeOfSolution);
        getHowAccurateStudent = findViewById(R.id.getHowAccurateStudent);
        getHowFast = findViewById(R.id.getHowFast);
        getHowStudentGrown = findViewById(R.id.getHowStudentGrown);
        getHowUnique = findViewById(R.id.getHowUnique);

        // Get bundle data from Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.d("CheckStudentHomeworkLog", "bundle: " + bundle);
            nameOfTheClassForParent.setText(bundle.getString("className"));
            nameOfThehomework.setText(bundle.getString("homeworkName"));
            solutionForHomework.setText(bundle.getString("solution"));
            homeworkDeadlineDate.setText(bundle.getString("homeworkDate"));
            homeworkDeadlineTime.setText(bundle.getString("homeworkTime"));
            descriptionOfTheHomework.setText(bundle.getString("homeworkDescription"));
            timeOfSolution.setText(bundle.getString("homeworkDate"));
            classId  = bundle.getString("classId");
            diakID = bundle.getString("studentID");
            Log.d("CheckStudentHomeworkLog", "diakID: " + diakID);
            homeworkID = bundle.getString("homeworkId");
            Log.d("CheckStudentHomeworkLog", "homeworkID: " + homeworkID);
        }

        // Retrieve student Firebase ID
        DatabaseReference getStudentFirebaseID = FirebaseDatabase.getInstance().getReference("diakok");
        getStudentFirebaseID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                        Log.d("CheckStudentHomeworkLog", "ds2: " + ds);
                        if (ds.child("diakID").getValue(String.class).equals(diakID)) {
                            String studentFireBaseID = ds.getKey();
                            Log.d("CheckStudentHomeworkLog", "studentFireBaseID: " + studentFireBaseID);
                            DatabaseReference getRatingForSolution = FirebaseDatabase.getInstance().getReference("diakok").child(studentFireBaseID).child("homework").child(homeworkID).child("rating");
                            getRatingForSolution.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                  for(DataSnapshot ds2: snapshot.getChildren()){
                                      Log.d("CheckStudentHomeworkLog", "snapshot: " + ds2);
                                      Long howAccurateLong = snapshot.child("howAccurateStudentSolved").getValue(Long.class);
                                      Long howFastLong = snapshot.child("howFastStudentSolved").getValue(Long.class);
                                      Long howStudentGrownLong = snapshot.child("howGrownStudentSolved").getValue(Long.class);
                                      Long howUniqueLong = snapshot.child("howUniqueStudentSolved").getValue(Long.class);


                                      String howAccurate= howAccurateLong!=null ? Long.toString(howAccurateLong) : null;
                                      String howFast = howFastLong!=null ? Long.toString(howFastLong) : null;
                                      String howStudentGrown = howStudentGrownLong!=null ? Long.toString(howStudentGrownLong) : null;
                                      String howUnique = howUniqueLong!=null ? Long.toString(howUniqueLong) : null;

                                      getHowAccurateStudent.setText(howAccurate);
                                      getHowFast.setText(howFast);
                                      getHowStudentGrown.setText(howStudentGrown);
                                      getHowUnique.setText(howUnique);

                                  }



                                    }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            }


                            );
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }
}
