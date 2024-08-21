package com.example.allamvizsga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewAlreadyCreatedHomeworkAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mHomeworkTitles;
    private ArrayList<String> mDatesOfHomework;
    private ArrayList<String> mTimesOfHomework;

    public ViewAlreadyCreatedHomeworkAdapter(Context context, ArrayList<String> homeworkTitles, ArrayList<String> datesOfHomework, ArrayList<String> timesOfHomework) {
        mContext = context;
        mHomeworkTitles = homeworkTitles;
        mDatesOfHomework = datesOfHomework;
        mTimesOfHomework = timesOfHomework;
    }

    @Override
        public int getCount() {
            return mHomeworkTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public void AddHomework(String homeworkTitle, String dateOfHomework, String timeOfHomework) {
        mHomeworkTitles.add(homeworkTitle);
        mDatesOfHomework.add(dateOfHomework);
        mTimesOfHomework.add(timeOfHomework);
    }

    public void setHomework(ArrayList<String> homeworkTitles, ArrayList<String> datesOfHomework, ArrayList<String> timesOfHomework) {
        mHomeworkTitles.clear();
        mDatesOfHomework.clear();
        mTimesOfHomework.clear();
        mHomeworkTitles.addAll(homeworkTitles);
        mDatesOfHomework.addAll(datesOfHomework);
        mTimesOfHomework.addAll(timesOfHomework);
        notifyDataSetChanged();
    }
    public void clear() {
        mHomeworkTitles.clear();
        mDatesOfHomework.clear();
        mTimesOfHomework.clear();
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.items_of_view_already_created_homeworks, parent, false);
        }
        TextView homeworkTitle = listItemView.findViewById(R.id.homeworkTitleForStudent);
        TextView dateOfHomework = listItemView.findViewById(R.id.gradedTime);
        TextView timeOfHomework = listItemView.findViewById(R.id.timeOfHomeworkForStudent);

        homeworkTitle.setText(mHomeworkTitles.get(position));
        dateOfHomework.setText(mDatesOfHomework.get(position));
        timeOfHomework.setText(mTimesOfHomework.get(position));

        return listItemView;
    }
}