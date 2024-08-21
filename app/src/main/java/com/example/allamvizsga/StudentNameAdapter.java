package com.example.allamvizsga;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class StudentNameAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> studentsNames;
    private ArrayList<Boolean> selectedItems;

    public StudentNameAdapter(Context context, ArrayList<String> studentsNames) {
        super(context, R.layout.choose_students_list, studentsNames);
        this.context = context;
        this.studentsNames = studentsNames;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (selectedItems == null || selectedItems.size() != studentsNames.size()) {
            selectedItems = new ArrayList<>(Collections.nCopies(studentsNames.size(), false));
        }
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.choose_students_list, parent, false);
        }

        TextView studentNameTextView = listItemView.findViewById(R.id.studentName);
        studentNameTextView.setText(studentsNames.get(position));

        // Ellenőrizzük, hogy van-e elegendő elem az 'selectedItems' listában
        if (selectedItems.size() > position) {
            CheckBox checkBox = listItemView.findViewById(R.id.checkBox);
            checkBox.setChecked(selectedItems.get(position));

            // Az állapot frissítése a kattintás alapján
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    selectedItems.set(position, checkBox.isChecked());

                }
            });
        }

        return listItemView;
    }
    public ArrayList<String> getStudentsNames() {
        return studentsNames;
    }
    public ArrayList<Boolean> getSelectedItems() {
        return selectedItems;
    }
}
