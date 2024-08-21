package com.example.allamvizsga;
import java.io.IOException;

import okhttp3.*;
public class CloudFunction {

    private static final String CLOUD_FUNCTION_URL=" https://us-central1-allamvizsga-81936.cloudfunctions.net/checkHomeworkStatus";

    public void callCloudFunction() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(CLOUD_FUNCTION_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle successful response
                String responseBody = response.body().string();
                // Process the response body as needed
                System.out.println("Response: " + responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
            }
        });
    }
}