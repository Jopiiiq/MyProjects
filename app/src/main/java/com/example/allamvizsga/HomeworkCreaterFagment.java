package com.example.allamvizsga;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class HomeworkCreaterFagment extends Fragment {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private TimePickerDialog timePickerDialog;
    private Button timeButton;
    private Button chooseStudents;
    private TextView homeworkDescription;
    private TextView nameOfTheHomework;
    private TextView createHomeworkButton;
    private DateAndTimeListener dateAndTimeListener;
    private String tantargyID;
    private String trueTantargyID;

    public HomeworkCreaterFagment() {
        // Required empty public constructor
    }

    public static HomeworkCreaterFagment newInstance() {
        return new HomeworkCreaterFagment();
    }
    public void setDateAndTimeListener(DateAndTimeListener listener) {
        this.dateAndTimeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_createragment, container, false);
        Bundle bundle = getArguments();

        if (bundle != null) {
            tantargyID = bundle.getString("tantargyID");
        }
        Log.d("HomeworkCreaterFagment", "tantargyID: " + tantargyID);



        homeworkDescription = view.findViewById(R.id.homeworkDescription);
        nameOfTheHomework = view.findViewById(R.id.nameOfTheHomework);

        // DateFormat and date picker
        initDatePicker();
        dateButton = view.findViewById(R.id.dateButton);
        dateButton.setText(getTodayDate());

        // Time picker
        initTimePicker();
        timeButton = view.findViewById(R.id.timeButton);
        timeButton.setText(getCurrentTime());

        // Choose students
        chooseStudents = view.findViewById(R.id.chooseStudents);
        chooseStudents.setOnClickListener(v -> {
            String selectedDate = dateButton.getText().toString();
            String selectedTime = timeButton.getText().toString();
            String homeworkTitle = nameOfTheHomework.getText().toString();
            String description = homeworkDescription.getText().toString();


            Fragment chooseStudentFragment = new ChooseStudentFragment();


            Bundle args = new Bundle();
            args.putString("tantargyID", tantargyID);
            args.putString("selectedDate", selectedDate);
            args.putString("selectedTime", selectedTime);
            args.putString("homeworkTitle", homeworkTitle);
            args.putString("description", description);

            chooseStudentFragment.setArguments(args);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.classNewFragment, chooseStudentFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });

        // Set click listeners for date and time buttons
        dateButton.setOnClickListener(v -> openDatePicker());
        timeButton.setOnClickListener(v -> openTimePicker(dateButton.getText().toString()));

        return view;
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(year, month, day);
                dateButton.setText(date);
                openTimePicker(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(requireActivity(), dateSetListener, year, month, day);
    }

    private String makeDateString(int year, int month, int day) {
        return year + " " + getMonthFormat(month) + " " + day;
    }

    public void openDatePicker() {
        datePickerDialog.show();
    }

    public String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(year, month, day);
    }

    public void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                String time = makeTimeString(hour, minute);
                timeButton.setText(time);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, hour, minute, true);
    }

    private String makeTimeString(int hour, int minute) {
        String hourString = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
        return hourString + ":" + minuteString;
    }

    public void openTimePicker(final String selectedDate) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(),
                (view, hourOfDay, minuteOfDay) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                    timeButton.setText(time);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private String getMonthFormat(int month) {
        String[] months = {"Január", "Február", "Március", "április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"};
        return months[month];
    }

    public interface DateAndTimeListener {
        void onDateAndTimeSelected(String date, String time);
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return String.format("%02d:%02d", hour, minute);
    }
}
