package com.example.allamvizsga;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewGradedHomework extends Fragment {

    // Define instance variables for data received from arguments
    private String homeworkTitle;
    private String homeworkDate;
    private String homeworkTime;
    private String homeworkID;
    private String solution;
    private String howFastStudentRating;
    private String howAccurateStudentRating;
    private String howGrownStudentRating;
    private String howUniqueStudentRating;
    private String description;
    private String gradedTime;

    // Define TextViews for UI elements
    private TextView nameOfThehomework;
    private TextView solutionForHomework;
    private TextView homeworkDeadlineDate;
    private TextView homeworkDeadlineTime;
    private TextView descriptionOfTheHomework;
    private TextView timeOfSolution;
    private TextView getHowAccurateStudent;
    private TextView getHowFast;
    private TextView getHowStudentGrown;
    private TextView getHowUnique;
    ImageView blueBackground;

    public ViewGradedHomework() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_graded_homework, container, false);

        blueBackground = view.findViewById(R.id.blurImageView);


        // Initialize TextViews from the inflated view
        nameOfThehomework = view.findViewById(R.id.nameOfThehomework);
        solutionForHomework = view.findViewById(R.id.solutionForHomework);
        homeworkDeadlineDate = view.findViewById(R.id.homeworkDeadlineDate); // Corrected ID
        homeworkDeadlineTime = view.findViewById(R.id.homeworkDeadlineTime); // Corrected ID
        descriptionOfTheHomework = view.findViewById(R.id.descriptionOfHomework);
        timeOfSolution = view.findViewById(R.id.timeOfSolution);
        getHowAccurateStudent = view.findViewById(R.id.getHowAccurateStudent);
        getHowFast = view.findViewById(R.id.getHowFast);
        getHowStudentGrown = view.findViewById(R.id.getHowStudentGrown);
        getHowUnique = view.findViewById(R.id.getHowUnique);

        // Retrieve data from arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            homeworkTitle = args.getString("homeworkTitle");
            homeworkDate = args.getString("homeworkDate");
            homeworkTime = args.getString("homeworkTime");
            homeworkID = args.getString("homeworkID");
            solution = args.getString("solution");
            description = args.getString("description");
            gradedTime = args.getString("gradedTime");
            howAccurateStudentRating = args.getString("howAccurateStudentRating");
            howFastStudentRating = args.getString("howFastStudentRating");
            howGrownStudentRating = args.getString("howGrownStudentRating");
            howUniqueStudentRating = args.getString("howUniqueStudentRating");
        }

        // Set retrieved data to corresponding TextViews
        nameOfThehomework.setText(homeworkTitle);
        homeworkDeadlineDate.setText(homeworkDate);
        homeworkDeadlineTime.setText(homeworkTime);
        descriptionOfTheHomework.setText(description);
        timeOfSolution.setText(gradedTime);
        solutionForHomework.setText(solution);
        getHowAccurateStudent.setText(howAccurateStudentRating);
        getHowFast.setText(howFastStudentRating);
        getHowStudentGrown.setText(howGrownStudentRating);
        getHowUnique.setText(howUniqueStudentRating);

        return view;
    }
}
