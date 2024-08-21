package com.example.allamvizsga;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.allamvizsga.R;

import java.util.ArrayList;
import java.util.Locale;

public class HomehomeViewAdapterForParent extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mHomeworkTitles;
    private ArrayList<String> mClassName;
    private ArrayList<String> mHomeworkDate;
    private ArrayList<String> mHomeworkTime;
    private ArrayList<String> mHomeworkSolutiontime;
    private ArrayList<String> mHomeworkDescription;
    private ArrayList<String> mHomeworkSolution;

    public HomehomeViewAdapterForParent() {
        mHomeworkTitles = new ArrayList<>();
        mClassName = new ArrayList<>();
        mHomeworkDate = new ArrayList<>();
        mHomeworkTime = new ArrayList<>();
        mHomeworkSolutiontime = new ArrayList<>();
        mHomeworkDescription = new ArrayList<>();
        mHomeworkSolution = new ArrayList<>();
    }

    public HomehomeViewAdapterForParent(Context context, ArrayList<String> homeworkTitles, ArrayList<String> className, ArrayList<String> homeworkDate, ArrayList<String> homeworkTime, ArrayList<String> homeworkSolutiontime, ArrayList<String> homeworkDescription, ArrayList<String> homeworkSolution) {
        mContext = context;
        mHomeworkTitles = homeworkTitles;
        mClassName = className;
        mHomeworkDate = homeworkDate;
        mHomeworkTime = homeworkTime;
        mHomeworkSolutiontime = homeworkSolutiontime;
        mHomeworkDescription = homeworkDescription;
        mHomeworkSolution = homeworkSolution;
    }
    public void addHomework(String homeworkName, String className, String homeworkDate, String homeworkTime, String homeworkDeadline, String homeworkDescription, String solution) {
        mHomeworkTitles.add(homeworkName);
        mClassName.add(className);
        mHomeworkDate.add(homeworkDate);
        mHomeworkTime.add(homeworkTime);
        mHomeworkSolutiontime.add(homeworkDeadline);
        mHomeworkDescription.add(homeworkDescription);
        mHomeworkSolution.add(solution);
        notifyDataSetChanged();
    }
    public HomehomeViewAdapterForParent(Context context) {
        this();
        mContext = context;
    }
    public void setContext(Context context) {
        mContext = context;
    }





    @Override
    public int getCount() {
        return mHomeworkTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return mHomeworkTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.dashoard_list_for_parent, parent, false);
        }

        TextView className = listItemView.findViewById(R.id.nameOfTheClass);
        TextView homeworkTitle = listItemView.findViewById(R.id.titleOfTheHomework);
        TextView homeworkDate = listItemView.findViewById(R.id.deadlineDateOfHomework);
        TextView homeworkTime = listItemView.findViewById(R.id.deadlineTimeOfHomework);


        if (className != null && position < mClassName.size()) {
            className.setText(mClassName.get(position));
            Log.d("HomehomeViewAdapterForParent", "className: " + mClassName.get(position));
        }
        if (homeworkTitle != null && position < mHomeworkTitles.size()) {
            homeworkTitle.setText(mHomeworkTitles.get(position));
            Log.d("HomehomeViewAdapterForParent", "homeworkTitle: " + mHomeworkTitles.get(position));
        }
        if (homeworkDate != null && position < mHomeworkDate.size()) {
            homeworkDate.setText(mHomeworkDate.get(position));
        }
        if (homeworkTime != null && position < mHomeworkTime.size()) {
            homeworkTime.setText(mHomeworkTime.get(position));
        }


        return listItemView;
    }
}
