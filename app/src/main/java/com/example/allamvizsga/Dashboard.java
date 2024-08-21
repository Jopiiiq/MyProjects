package com.example.allamvizsga;

import static com.example.allamvizsga.UserData.Tantargyak.tantargyakList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Dashboard extends AppCompatActivity {
    String currentUserId;
    ImageView blueBackground;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        AtomicBoolean isItDiak = new AtomicBoolean(false);
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tantargyak");
        Log.d("Dashboard1", "onCreate: "+currentUserId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        blueBackground = findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(10, 5));

        Glide.with(this)
                .load(R.drawable.main_background)
                .apply(requestOptions)
                .into(blueBackground);



        UserData.isDiak(currentUserId, isDiak -> {
            if (isDiak) {
                Log.d("UserData", "A felhasználó diak.");
                isItDiak.set(true);
                List<UserData.Tantargyak> tantargyakList = new ArrayList<>();
                DatabaseReference tantargyakRef = FirebaseDatabase.getInstance().getReference().child("tantargyak");
                tantargyakRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot tantargySnapshot : snapshot.getChildren()) {
                            String tantargyID = tantargySnapshot.getKey();
                            DatabaseReference diakWantedToJoinRef = tantargySnapshot.child("diakWantedToJoin").getRef();
                            diakWantedToJoinRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        for (DataSnapshot ds2 : ds.getChildren()) {
                                            String studentID = ds2.getKey();
                                            boolean isAccepted = Boolean.parseBoolean(ds2.getValue(String.class));
                                            if (isAccepted && studentID.equals(currentUserId)) {
                                                DatabaseReference tantargyRef = FirebaseDatabase.getInstance().getReference().child("tantargyak").child(tantargyID);
                                                tantargyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        UserData.Tantargyak tantargy = snapshot.getValue(UserData.Tantargyak.class);
                                                        if (tantargy != null) {
                                                            tantargyakList.add(tantargy);
                                                        }
                                                        updateUI(tantargyakList);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("UserData", "Error getting data", error.toException());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("UserData", "Error getting data", error.toException());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserData", "Error getting data", error.toException());
                    }
                });
            }
        });

        UserData.isTanar(currentUserId, isTeacher -> {
            if (isTeacher && !isItDiak.get()) {
                Log.d("UserData", "A felhasználó tanár.");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<UserData.Tantargyak> tantargyakList = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserData.Tantargyak tantargy = ds.getValue(UserData.Tantargyak.class);
                            assert tantargy != null;
                            UserData.getCurrentTanarID(currentUserId, new UserData.TanarIDCallback() {
                                @Override
                                public void onResult(String tanarID) {
                                    if(tantargy.getTanarID().equals(tanarID)) {
                                        tantargyakList.add(tantargy);
                                    }
                                    updateUI(tantargyakList);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DatabaseError", "Adatbázis hiba: " + error.getMessage());
                        Toast.makeText(Dashboard.this, "Adatbázis hiba: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d("UserData", "A felhasználó nem tanár.");
            }
        });

        UserData.isSzulo(currentUserId, isSzulo -> {
            if (isSzulo) {
                runOnUiThread(() -> {
                    updateUIForParent();
                    Log.d("DashboardLog", "A felhasználó szülő.");
                });
            } else {
                // Ha nem szülő
                Log.d("DashboardLog", "A felhasználó nem szülő.");
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem createClassItem = menu.findItem(R.id.createClass);
        MenuItem joinClassItem = menu.findItem(R.id.joinClass);

        UserData.isDiak(currentUserId, isDiak -> {
            runOnUiThread(() -> {
                createClassItem.setVisible(!isDiak);
            });
        });
        UserData.isTanar(currentUserId, isTanar -> {
            runOnUiThread(() -> {
                joinClassItem.setVisible(!isTanar);
            });
        });


        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.joinClass) {
            UserData.isDiak(currentUserId, isDiak -> {
                Log.d("UserData", "isDiak: " + currentUserId);
                Log.d("UserData1", "isDiak result: " + isDiak);
                runOnUiThread(() -> {
                    Log.d("UserData", "isTanar result: " + isDiak);
                    if (isDiak) {

                        Intent intent = new Intent(this, JoinClass.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(this, "Nem vagy diák!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else if (id == R.id.createClass) {
            UserData.isTanar(currentUserId, isTeacher -> {
                runOnUiThread(() -> {
                    Log.d("UserData", "isTanar result: " + isTeacher);
                    if (isTeacher) {
                        startCreteNewClassActivity();
                    } else {
                        Toast.makeText(this, "Nem vagy tanár!", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }

        return super.onOptionsItemSelected(item);
    }
    private void updateUI(List<UserData.Tantargyak> tantargyakList) {
        Log.d("Updateui", "belepes");
        LinearLayout layout = findViewById(R.id.selected_classes_layout);

        layout.removeAllViews();

        for (UserData.Tantargyak tantargyak : tantargyakList) {
            // Get the values from the Class
            String className = tantargyak.getNev();
            String creatorOfTheClass = tantargyak.getTanarID();
            Log.d("Updateui", "tanrid");
            String numberOfStudents = "TODO";
            String imageUrl = tantargyak.getKep();
            String tantargyID = tantargyak.getTantargyID();

            View listItem = getLayoutInflater().inflate(R.layout.dashboard_list, null);
            ImageView imageView = listItem.findViewById(R.id.imageView);
            TextView classNameTextView = listItem.findViewById(R.id.className);
            TextView creatorTextView = listItem.findViewById(R.id.creatorOfTheClass);
            TextView numberOfStudentsTextView = listItem.findViewById(R.id.numberOfStudents);


            classNameTextView.setText("Osztály neve: " + className);
            creatorTextView.setText("Tanár: " + creatorOfTheClass);
            numberOfStudentsTextView.setText("Tanulok száma" + "ToDo");

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Log.d("Updateui", "kep megadas");
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child(tantargyID + "_image.jpg");
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    String downloadUrl = uri.toString();

                    Picasso.get().load(downloadUrl).into(imageView);
                }).addOnFailureListener(e -> {

                    Toast.makeText(this, "Hiba történt a kép letöltésekor", Toast.LENGTH_SHORT).show();
                    Log.e("Updateui", "Kép letöltése sikertelen: " + e.getMessage());
                });
                listItem.setOnClickListener(v -> {
                    Intent intent = new Intent(this, InClass.class);
                    intent.putExtra("className", className);
                    intent.putExtra("creatorOfTheClass", creatorOfTheClass);
                    intent.putExtra("numberOfStudents", numberOfStudents);
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("tantargyID", tantargyID);
                    startActivity(intent);
                });
            }
            layout.addView(listItem);
        }
    }



    private void startCreteNewClassActivity() {
        Intent intent = new Intent(this, CreateNewClass.class);
        startActivity(intent);
    }

    //Search for homework for parent`s child
    public void updateUIForParent() {
        HomehomeViewAdapterForParent adapter = new HomehomeViewAdapterForParent();
        adapter.setContext(this);
        ListView listView = findViewById(R.id.listViewForDashboard);
        listView.setAdapter(adapter);
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("currentUserId", null);
        Log.d("DashboardLog", "currentUserId: " + currentUserId);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("szulok").child(currentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DashboardLog", "snapshot: " + snapshot);
                if (snapshot.exists()) {
                    String studentID = snapshot.child("gyermekSzam").getValue(String.class);
                    Log.d("DashboardLog", "studentID: " + studentID);

                    if (studentID != null) {

                        DatabaseReference searchForHomeworks = FirebaseDatabase.getInstance().getReference("diakok");
                        searchForHomeworks.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    Log.d("DashboardLog", "childSnapshot2: " + childSnapshot);
                                    String diakID = childSnapshot.child("diakID").getValue(String.class);
                                    Log.d("DashboardLog", "diakId, studentID: " + diakID + ", " + studentID);
                                    if (diakID.equals(studentID)) {
                                            for (DataSnapshot homeworkSnapshot : childSnapshot.child("homework").getChildren()) {
                                                Log.d("DashboardLog", "homeworkSnapshot3: " + homeworkSnapshot);
                                                String homeworkID = homeworkSnapshot.getKey();
                                                Log.d("DashboardLog", "homeworkID: " + homeworkID);
                                                if (homeworkSnapshot.child("rating").exists()) {
                                                    Log.d("DashboardLog", "belep: ");
                                                    String classId = homeworkSnapshot.child("tantargyID").getValue(String.class);
                                                    Log.d("DashboardLog", "classId: " + classId);
                                                    String solution = homeworkSnapshot.child("solution").getValue(String.class);
                                                    Log.d("DashboardLog", "solution: " + solution);
                                                    String homeworkName = homeworkSnapshot.child("homeworkTitle").getValue(String.class);
                                                    Log.d("DashboardLog", "homeworkName: " + homeworkName);
                                                    String homeworkDescription = homeworkSnapshot.child("homeworkDescription").getValue(String.class);
                                                    Log.d("DashboardLog", "homeworkDescription: " + homeworkDescription);
                                                    String homeworkTime = homeworkSnapshot.child("homeworkTime").getValue(String.class);
                                                    Log.d("DashboardLog", "homeworkTime: " + homeworkTime);
                                                    String homeworkDate = homeworkSnapshot.child("homeworkDate").getValue(String.class);
                                                    Log.d("DashboardLog", "homeworkDate: " + homeworkDate);
                                                    Long homeworkDeadlineLong = homeworkSnapshot.child("solutionTime").getValue(Long.class);
                                                    String homeworkDeadline = homeworkDeadlineLong.toString();
                                                    Log.d("DashboardLog", "homeworkDeadline: " + homeworkDeadline);




                                                    DatabaseReference getClassName = FirebaseDatabase.getInstance().getReference("tantargyak").child(classId);
                                                    getClassName.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String className = snapshot.child("nev").getValue(String.class);
                                                            Log.d("DashboardLog", "className: " + className);
                                                            adapter.addHomework(homeworkName, className, homeworkDate, homeworkTime, homeworkDeadline, homeworkDescription, solution);
                                                            adapter.notifyDataSetChanged();

                                                           listView.setOnItemClickListener((parent, view, position, id) -> {
                                                               Intent intent = new Intent(Dashboard.this, CheckStudentHomework.class);
                                                               intent.putExtra("homeworkName", homeworkName);
                                                               intent.putExtra("className", className);
                                                               intent.putExtra("homeworkDate", homeworkDate);
                                                               intent.putExtra("homeworkTime", homeworkTime);
                                                               intent.putExtra("homeworkDeadline", homeworkDeadline);
                                                               intent.putExtra("homeworkDescription", homeworkDescription);
                                                               intent.putExtra("solution", solution);
                                                               intent.putExtra("classId", classId);
                                                               intent.putExtra("homeworkId", homeworkID);
                                                               intent.putExtra("studentID", studentID);


                                                               startActivity(intent);
                                                           });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("DashboardLog", "getClassName onCancelled: " + error.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("DashboardLog", "searchForHomeworks onCancelled: " + error.getMessage());
                            }
                        });
                    } else {
                        Log.d("DashboardLog", "No studentID found for current user");
                    }
                } else {
                    Log.d("DashboardLog", "No snapshot exists for current user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardLog", "databaseReference onCancelled: " + error.getMessage());
            }
        });

    }
}
