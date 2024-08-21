    package com.example.allamvizsga;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import android.os.Bundle;
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
    import java.util.Arrays;

    public class ViewAlreadyCreatedHomeworkFragment extends Fragment {
        Button ongoingHomeworkButton, finishedHomeworkButton;
        ListView homeworkListView;
        String tantargyID;
        String studentFirebaseID;
        String className;
        String selectedDate;
        String selectedTime;
        String description;
        String trueTantargyID;
        private ArrayList<String> homeworkTitles = new ArrayList<>();
        private ArrayList<String> homeworkDates = new ArrayList<>();
        private ArrayList<String> homeworkTimes = new ArrayList<>();
        private ArrayList<String> homeworkDescriptions = new ArrayList<>();
        private ArrayList<String> homeworkIDs = new ArrayList<>();

        private ViewAlreadyCreatedHomeworkAdapter adapter;
        private CloudFunction cloudFunctionClient;
        public ViewAlreadyCreatedHomeworkFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_view_already_created_homework, container, false);
            cloudFunctionClient = new CloudFunction();
            ongoingHomeworkButton = view.findViewById(R.id.ongoingHomeworkButton);
            finishedHomeworkButton = view.findViewById(R.id.finishedHomeworkButton);
            homeworkListView = view.findViewById(R.id.homeworkListView);
            callCloudFunction();
            adapter = new ViewAlreadyCreatedHomeworkAdapter(getContext(), homeworkTitles, homeworkDates, homeworkTimes);
            homeworkListView.setAdapter(adapter);
            Bundle bundle = getArguments();
            if (bundle != null) {
                tantargyID = bundle.getString("tantargyID");
                Log.d("ViewAlreadyCreatedHomeworkFragment", "tantargyID: " + tantargyID);
                studentFirebaseID = bundle.getString("studentFirebaseID");
                className = bundle.getString("className");
                selectedDate = bundle.getString("selectedDate");
                selectedTime = bundle.getString("selectedTime");
                description = bundle.getString("description");
            }


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tantargyak");

            databaseReference.orderByChild("tantargyID").equalTo(tantargyID).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            trueTantargyID = dataSnapshot.getKey();
                            Log.d("ViewAlreadyCreatedHomeworkFragment", "trueTantargyID: " + trueTantargyID);
                            processHomeworkListTrue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ongoingHomeworkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processHomeworkListTrue();

                }
            });

            finishedHomeworkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processHomeworkListFalse();

                }
            });
            homeworkListView.setOnItemClickListener((parent, view1, position, id) -> {
                String selectedHomeworkTitle = homeworkTitles.get(position);
                String selectedHomeworkDate = homeworkDates.get(position);
                String selectedHomeworkTime = homeworkTimes.get(position);
                String selectedDescription = homeworkDescriptions.get(position);
                String selectedHomeworkID = homeworkIDs.get(position);

            });



            return view;
        }

        private void processHomeworkListTrue() {
            callCloudFunction();
            homeworkTitles.clear();
            homeworkDates.clear();
            homeworkTimes.clear();
            homeworkDescriptions.clear();
            homeworkIDs.clear();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("tantargyak")
                    .child(trueTantargyID)
                    .child("homework");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot homeworkSnapshot : snapshot.getChildren()) {
                        boolean homeworkOngoingOrNot = false;
                        if (homeworkSnapshot.child("ongoingOrNot").exists()) {
                            homeworkOngoingOrNot = homeworkSnapshot.child("ongoingOrNot").getValue(Boolean.class);
                        }
                        if (homeworkOngoingOrNot) {
                            homeworkTitles.add(homeworkSnapshot.child("homeworkTitle").getValue(String.class));
                            homeworkDates.add(homeworkSnapshot.child("homeworkDate").getValue(String.class));
                            homeworkTimes.add(homeworkSnapshot.child("homeworkTime").getValue(String.class));
                            homeworkIDs.add(homeworkSnapshot.getKey());
                            homeworkDescriptions.add(homeworkSnapshot.child("homeworkDescription").getValue(String.class));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


        private void processHomeworkListFalse() {
            callCloudFunction();
            homeworkTitles.clear();
            homeworkDates.clear();
            homeworkTimes.clear();
            homeworkDescriptions.clear();
            homeworkIDs.clear();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("tantargyak")
                    .child(trueTantargyID)
                    .child("homework");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot homeworkSnapshot : snapshot.getChildren()) {
                        boolean homeworkOngoingOrNot = false;
                        if (homeworkSnapshot.child("ongoingOrNot").exists()) {
                            Log.d("ViewAlreadyCreatedHomeworkFragment", "exists"+homeworkSnapshot.getKey());
                            homeworkOngoingOrNot = homeworkSnapshot.child("ongoingOrNot").getValue(Boolean.class);
                            Log.d("ViewAlreadyCreatedHomeworkFragment", "homeworkOngoingOrNot: " + homeworkOngoingOrNot);
                        }
                        if (!homeworkOngoingOrNot) {
                            homeworkTitles.add(homeworkSnapshot.child("homeworkTitle").getValue(String.class));
                            homeworkDates.add(homeworkSnapshot.child("homeworkDate").getValue(String.class));
                            homeworkTimes.add(homeworkSnapshot.child("homeworkTime").getValue(String.class));
                            homeworkIDs.add(homeworkSnapshot.getKey());
                            homeworkDescriptions.add(homeworkSnapshot.child("homeworkDescription").getValue(String.class));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void callCloudFunction() {
            cloudFunctionClient.callCloudFunction();
        }
    }