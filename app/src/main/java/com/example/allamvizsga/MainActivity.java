package com.example.allamvizsga;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    Button regButtonForTeacher;
    Button regButtonForStudent;
    Button regButtonForParent;
    TextView teacher;
    TextView student;
    TextView parent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regButtonForTeacher = findViewById(R.id.regButtonForTeacher);
        regButtonForStudent = findViewById(R.id.regButtonForStudent);
        regButtonForParent = findViewById(R.id.regButtonForParent);
        teacher=findViewById(R.id.teacher);
        student=findViewById(R.id.student);
        parent=findViewById(R.id.parent);

        showMainViews();



        regButtonForStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainViews();


                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new StudentRegistrationFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();




            }



        });
        regButtonForTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMainViews();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragmentForTeacher());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        regButtonForParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideMainViews();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new LoginFragmentForParent());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });




    } @Override
    protected void onResume() {
        super.onResume();
        // Itt nem csinálunk semmit, így a felhasználó visszatérésekor nem változik a láthatóság
    }

    private void showMainViews() {
        teacher.setVisibility(View.VISIBLE);
        student.setVisibility(View.VISIBLE);
        parent.setVisibility(View.VISIBLE);
        regButtonForStudent.setVisibility(View.VISIBLE);
        regButtonForTeacher.setVisibility(View.VISIBLE);
        regButtonForParent.setVisibility(View.VISIBLE);
    }

    private void hideMainViews() {
        teacher.setVisibility(View.GONE);
        student.setVisibility(View.GONE);
        parent.setVisibility(View.GONE);
        regButtonForStudent.setVisibility(View.GONE);
        regButtonForTeacher.setVisibility(View.GONE);
        regButtonForParent.setVisibility(View.GONE);
    }



}