package com.example.allamvizsga;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class JoinClass extends AppCompatActivity {

    Button jointToClassBottom;
    String currentUserId;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak");
    EditText authenticationCode;
    String authenticationCodeText;
    ImageView blueBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");
        blueBackground = findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(2, 3));

        Glide.with(this)
                .load(R.drawable.background_for_dashboard)
                .apply(requestOptions)
                .into(blueBackground);

        authenticationCode = findViewById(R.id.authenticationCode);
        authenticationCodeText = "";

        jointToClassBottom = findViewById(R.id.jointToClassBottom);

        jointToClassBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationCodeText = authenticationCode.getText().toString();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d("JoinClass", "tantargyID from database: " + ds.child("tantargyID").getValue(String.class));
                            String tantargyID = ds.child("tantargyID").getValue(String.class);
                            if (tantargyID != null && tantargyID.equals(authenticationCodeText)) {
                                Log.d("JoinClass", "Matching tantargyID found: " + tantargyID);
                                DatabaseReference selectedClassRef = databaseReference.child(ds.getKey());
                                selectedClassRef.child("diakWantedToJoin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        boolean alreadyJoined = false;
                                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                            Log.d("JoinClass", "childSnapshot: " + childSnapshot);
                                            String userId = childSnapshot.getKey();
                                            if (userId != null && userId.equals(currentUserId)) {
                                                alreadyJoined = true;
                                                break;
                                            }
                                        }
                                        if (!alreadyJoined) {
                                            String newId = selectedClassRef.child("diakWantedToJoin").push().getKey();
                                            selectedClassRef.child("diakWantedToJoin").child(newId).setValue(currentUserId);
                                            selectedClassRef.child("diakWantedToJoin").child(newId).child(currentUserId).setValue("false");
                                            Toast.makeText(JoinClass.this, "Sikeres jelentkezett a tantargyhoz.", Toast.LENGTH_SHORT).show();
                                            finish();

                                            Log.d("JoinClass", "User added to diakWantedToJoin for tantargyID: " + tantargyID);
                                        } else {
                                            Log.d("JoinClass", "User already in diakWantedToJoin for tantargyID: " + tantargyID);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("JoinClass", "Database operation cancelled: " + error.getMessage());
                                    }
                                });

                                break;
                            } else {
                                Log.d("JoinClass", "No match found for authenticationCodeText: " + authenticationCodeText);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("JoinClass", "Database operation cancelled: " + error.getMessage());
                    }
                });

            }
        });
    }
}

