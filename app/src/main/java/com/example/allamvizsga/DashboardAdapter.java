package com.example.allamvizsga;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DashboardAdapter extends BaseAdapter {
    private Context context;
    private List<UserData.Tantargyak> tantargyakList;
    public DashboardAdapter(Context context, List<UserData.Tantargyak> tantargyakList) {
        this.context = context;
        this.tantargyakList = tantargyakList;
    }

    @Override
    public int getCount() {
        return tantargyakList.size();
    }

    @Override
    public Object getItem(int position) {
        return tantargyakList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dashboard_list, parent, false);
        }

        UserData.Tantargyak tantargyak = tantargyakList.get(position);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView classNameTextView = convertView.findViewById(R.id.className);
        TextView creatorTextView = convertView.findViewById(R.id.creatorOfTheClass);
        TextView numberOfStudentsTextView = convertView.findViewById(R.id.numberOfStudents);

        classNameTextView.setText("Osztály neve: " + tantargyak.getNev());
        creatorTextView.setText("Tanár: " + tantargyak.getTanarID());
        numberOfStudentsTextView.setText("Tanulok száma: " + "TODO");

        String imageUrl = tantargyak.getKep();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imageView);
        }

        return convertView;
    }
}
