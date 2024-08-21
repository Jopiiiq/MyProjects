package com.example.allamvizsga;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class InClass extends AppCompatActivity implements  HomeworkCreaterFagment.DateAndTimeListener {
    ImageView firstImageButton;
    ImageView secondImageButton;
    ImageView thirdImageButton;

    ImageView fourthImageButton;
    TextView classNameForThisOne;
    String currentUserId;
    ImageView blueBackground;
    private CloudFunction cloudFunctionClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_in_class);

        // ActionBar ellenőrzése, hogy elkerüljük a NullPointerException-t
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cloudFunctionClient = new CloudFunction();

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");
        Log.d("DashboardLog", "currentUserId: " + currentUserId);
        classNameForThisOne = findViewById(R.id.classNameForThisOne);
        classNameForThisOne.setText(getIntent().getStringExtra("className"));

        firstImageButton = findViewById(R.id.firstImageButton);
        secondImageButton = findViewById(R.id.secondImageButton);
        thirdImageButton = findViewById(R.id.thirdImageButton);
        fourthImageButton = findViewById(R.id.fourthImageButton);

        firstImageButton.setVisibility(View.INVISIBLE);
        secondImageButton.setVisibility(View.INVISIBLE);
        thirdImageButton.setVisibility(View.INVISIBLE);
        fourthImageButton.setVisibility(View.INVISIBLE);

        UserData.isDiak(currentUserId, isDiak -> {
            runOnUiThread(() -> {
                Log.d("UserData", "isDiak: " + currentUserId);
                Log.d("UserData", "isDiak result: " + isDiak);
            });
            if (isDiak) {
                callCloudFunction();
                firstImageButton.setVisibility(View.VISIBLE);
                secondImageButton.setVisibility(View.VISIBLE);

                firstImageButton.setImageResource(R.drawable.homework_student);
                secondImageButton.setImageResource(R.drawable.grades_icon);
                thirdImageButton.setImageResource(R.drawable.evaluation);

                firstImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    HomeworkViewForStudentFragment fragment = new HomeworkViewForStudentFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("studentFirebaseID", getIntent().getStringExtra("studentFirebaseID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    fragment.setArguments(args);

                    fragmentManager.beginTransaction()

                            .replace(R.id.classNewFragment, fragment)
                            .addToBackStack(null)
                            .commit();
                });
                secondImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    GetViewGradedHomework fragment = new GetViewGradedHomework();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("studentFirebaseID", getIntent().getStringExtra("studentFirebaseID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction()

                            .replace(R.id.classNewFragment, fragment)
                            .addToBackStack(null)
                            .commit();

                });


            }
        });

        UserData.isTanar(currentUserId, isTeacher -> {
            runOnUiThread(() -> {
                Log.d("UserData", "isTanar result: " + isTeacher);
            });
            if (isTeacher) {
                callCloudFunction();
                firstImageButton.setImageResource(R.drawable.homework_for_teacher);
                secondImageButton.setImageResource(R.drawable.grades_icon);
                thirdImageButton.setImageResource(R.drawable.evaluation);

                firstImageButton.setVisibility(View.VISIBLE);
                secondImageButton.setVisibility(View.VISIBLE);
                thirdImageButton.setVisibility(View.VISIBLE);
                fourthImageButton.setVisibility(View.VISIBLE);

                firstImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    HomeworkCreaterFagment fragment = new HomeworkCreaterFagment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    args.putString("numberOfStudents", getIntent().getStringExtra("numberOfStudents"));
                    args.putString("imageUrl", getIntent().getStringExtra("imageUrl"));

                    fragment.setArguments(args);


                    fragmentManager.beginTransaction()

                            .replace(R.id.classNewFragment, fragment)
                            .addToBackStack(null)
                            .commit();
                });
                secondImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    callCloudFunction();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    args.putString("numberOfStudents", getIntent().getStringExtra("numberOfStudents"));
                    args.putString("imageUrl", getIntent().getStringExtra("imageUrl"));

                    ViewAlreadyCreatedHomeworkFragment fragment = new ViewAlreadyCreatedHomeworkFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();

                    fragment.setArguments(args);

                    fragmentManager.beginTransaction()
                            .replace(R.id.classNewFragment, fragment)
                            .addToBackStack(null)
                            .commit();
                });

                thirdImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    callCloudFunction();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    args.putString("numberOfStudents", getIntent().getStringExtra("numberOfStudents"));
                    args.putString("imageUrl", getIntent().getStringExtra("imageUrl"));


                    EvaluationFragment fragment = new EvaluationFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragment.setArguments(args);
                    fragmentManager.beginTransaction()
                            .replace(R.id.classNewFragment, fragment)
                            .addToBackStack(null)
                            .commit();
                });

                fourthImageButton.setOnClickListener(view -> {
                    blueBackground = findViewById(R.id.blurImageView);
                    RequestOptions requestOptions = new RequestOptions()
                            .transform(new BlurTransformation(10, 5));

                    Glide.with(this)
                            .load(R.drawable.classroombackground2)
                            .apply(requestOptions)
                            .into(blueBackground);
                    callCloudFunction();
                    Bundle args = new Bundle();
                    args.putString("tantargyID", getIntent().getStringExtra("tantargyID"));
                    args.putString("className", getIntent().getStringExtra("className"));
                    args.putString("creatorOfTheClass", getIntent().getStringExtra("creatorOfTheClass"));
                    args.putString("numberOfStudents", getIntent().getStringExtra("numberOfStudents"));
                    args.putString("imageUrl", getIntent().getStringExtra("imageUrl"));

                    Fragment fragment1 = new AddStudentToClassFragment();

                    fragment1.setArguments(args);


                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.classNewFragment, fragment1)
                            .commit();
                });
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDateAndTimeSelected(String date, String time) {

    }
    private void callCloudFunction() {
        cloudFunctionClient.callCloudFunction();
    }

    public void setBackgroundBlur(Boolean blur){
        View roootview = findViewById(R.id.main);
        if(blur){
           roootview.setBackgroundResource(R.drawable.background_blur);
        }
        else{
            roootview.setBackgroundResource(0);
        }
    }
}