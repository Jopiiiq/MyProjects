package com.example.allamvizsga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class GetViewGradedHomeworkAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<String> mHomeworkTitle;
    ArrayList<String> mGradedTime;

    public GetViewGradedHomeworkAdapter(Context context) {
        mContext = context;
        mHomeworkTitle = new ArrayList<>();
        mGradedTime = new ArrayList<>();
    }

    public void addViewGradedHomework(String homeworkTitle, String gradedTime) {
        mHomeworkTitle.add(homeworkTitle);
        mGradedTime.add(gradedTime);
        notifyDataSetChanged(); // Notify the adapter to refresh the list
    }

    @Override
    public int getCount() {
        return mHomeworkTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return mHomeworkTitle.get(position);
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
            listItemView = inflater.inflate(R.layout.items_get_view_greade_homework, parent, false);
        }

        TextView homeworkTitle = listItemView.findViewById(R.id.homeworkTitleForStudent);
        TextView gradedTime = listItemView.findViewById(R.id.gradedTime);

        homeworkTitle.setText(mHomeworkTitle.get(position));
        gradedTime.setText(mGradedTime.get(position));

        return listItemView;
    }
}
