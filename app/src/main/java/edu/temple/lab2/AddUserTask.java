package edu.temple.lab2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddUserTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {

        String response = "";
        StringBuilder sb = new StringBuilder();

        HttpURLConnection httpURLConnection = null;
        try {
            sb.append(URLEncoder.encode("user", "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(strings[0], "UTF-8"));
            sb.append("&");
            sb.append(URLEncoder.encode("latitude", "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(strings[1], "UTF-8"));
            sb.append("&");
            sb.append(URLEncoder.encode("longitude", "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(strings[2], "UTF-8"));
            byte[] userByteArray = sb.toString().getBytes("UTF-8");

            httpURLConnection = (HttpURLConnection) new URL("https://kamorris.com/lab/register_location.php").openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(userByteArray.length));
            httpURLConnection.setDoOutput(true);
            httpURLConnection.getOutputStream().write(userByteArray);

            Log.d("Username to post", strings[0]);
            Log.d("User lat to post", strings[1]);
            Log.d("User long to post", strings[2]);

            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                response += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        Log.d("Response from POST", response);
        return response;
    }
}
