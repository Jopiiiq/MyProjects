package com.example.allamvizsga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeworkViewForStudentAdapter  extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mHomeworkTitles;
    private ArrayList<String> mDatesOfHomework;
    private ArrayList<String> mTimesOfHomework;
    private ArrayList<String> studentNameInHomeworkList;


    public HomeworkViewForStudentAdapter(Context context, ArrayList<String> homeworkTitles, ArrayList<String> datesOfHomework, ArrayList<String> timesOfHomework, ArrayList<String> studentNameInHomework) {
        mContext = context;
        mHomeworkTitles = homeworkTitles;
        mDatesOfHomework = datesOfHomework;
        mTimesOfHomework = timesOfHomework;
        studentNameInHomeworkList = studentNameInHomework;
    }

    @Override
    public int getCount() {
        return mHomeworkTitles.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public void AddHomework(String homeworkTitle, String dateOfHomework, String timeOfHomework, String studentNameInHomework) {
        mHomeworkTitles.add(homeworkTitle);
        mDatesOfHomework.add(dateOfHomework);
        mTimesOfHomework.add(timeOfHomework);
        studentNameInHomeworkList.add(studentNameInHomework);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.item_of_view_homework_for_students, parent, false);
        }
        TextView homeworkTitle = listItemView.findViewById(R.id.homeworkTitleForStudent);
        TextView dateOfHomework = listItemView.findViewById(R.id.gradedTime);
        TextView timeOfHomework = listItemView.findViewById(R.id.timeOfHomeworkForStudent);
        TextView studentNameInHomework = listItemView.findViewById(R.id.studentNameInHomework);

        homeworkTitle.setText(mHomeworkTitles.get(position));
        dateOfHomework.setText(mDatesOfHomework.get(position));
        timeOfHomework.setText(mTimesOfHomework.get(position));
        if (studentNameInHomeworkList != null && studentNameInHomeworkList.size() > position) {
            studentNameInHomework.setText(studentNameInHomeworkList.get(position));
        } else {
            studentNameInHomework.setText(""); // Set empty string or handle null case
        }

        return listItemView;
    }

}