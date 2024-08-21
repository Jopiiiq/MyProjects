package com.example.allamvizsga;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

public class CustomDasboardAdapter extends ArrayAdapter<UserData.Tantargyak> {
    private Context mContext;
    private int mResource;

    public CustomDasboardAdapter(Context context, int resource, List<UserData.Tantargyak> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Get the data item for this position
        UserData.Tantargyak tantargyak = getItem(position);

        // Get references to views
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView classNameTextView = convertView.findViewById(R.id.className);
        TextView creatorTextView = convertView.findViewById(R.id.creatorOfTheClass);
        TextView numberOfStudentsTextView = convertView.findViewById(R.id.numberOfStudents);

        // Set data to views

        classNameTextView.setText(tantargyak.getNev());
        creatorTextView.setText(tantargyak.getTanarID());
        numberOfStudentsTextView.setText("TODO");

        new DownloadImageTask(imageView).execute(tantargyak.getKep());

        return convertView;
    }
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> implements com.example.allamvizsga.DownloadImageTask {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }


        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
