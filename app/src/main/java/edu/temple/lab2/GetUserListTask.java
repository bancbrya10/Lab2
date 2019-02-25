package edu.temple.lab2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetUserListTask extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            URL url = new URL("https://kamorris.com/lab/get_locations.php");
            HttpURLConnection request = (HttpURLConnection) (url.openConnection());
            request.setRequestMethod("GET");
            request.connect();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String response = "{\"users\":";
            String tmpResponse;
            tmpResponse = buffer.readLine();
            while(tmpResponse != null){
                response = response + tmpResponse;
                tmpResponse = buffer.readLine();
            }
            response += "}";
            Log.d("Response Message", response);
            request.disconnect();
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
