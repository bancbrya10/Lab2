package edu.temple.lab2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    private final int MY_LOCATION_REQUEST_CODE = 111;
    String username = "";
    float latitude;
    float longitude;
    EditText usernameEditText;
    TextView latitudeText;
    TextView longitudeText;
    Button submitButton;
    JSONArray userArray = null;
    LocationManager lm;
    LocationListener ll;
    Partner currentUser;
    String[] jsonStrUserList;
    ArrayList<Partner> userList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        latitudeText = findViewById(R.id.latitude_textbox);
        longitudeText = findViewById(R.id.longitude_textbox);
        submitButton = findViewById(R.id.submit_button);
        usernameEditText = findViewById(R.id.username_textbox);
        String userListTaskResponseStr;
        FragmentManager fm;
        FragmentTransaction ft;
        userList = new ArrayList<>();


        lm = getSystemService(LocationManager.class);
        ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                latitudeText.setText(String.valueOf(location.getLatitude()));
                longitudeText.setText(String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        requestLocationUpdates();
        try {
            userListTaskResponseStr = String.valueOf(new GetUserListTask().execute().get());
            Log.d("User List Task Response", userListTaskResponseStr);
            updateUserList(userListTaskResponseStr);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = usernameEditText.getText().toString();
                username = inputStr;
                currentUser = new Partner(username, longitude, latitude, new LatLng(latitude, longitude));
                boolean isNewUser = true;

                //Check to see if username is in database
                for(int i = 0; i < userArray.length(); i++){
                    try {
                        if(userArray.getJSONObject(i).get("username").equals(username)){
                            isNewUser = false;
                            requestLocationUpdates();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (isNewUser) {

                    try {
                        new AddUserTask().execute(currentUser.id, String.valueOf(currentUser.latitude), String.valueOf(currentUser.longitude)).get();
                        String userListTaskResponseStr2 = String.valueOf(new GetUserListTask().execute().get());
                        updateUserList(userListTaskResponseStr2);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Welcome back, " + username + ".\nLocation information updating", Toast.LENGTH_LONG).show();
                }
                Log.d("Pre Fragment Var Check", "Username: " + username
                        + "\nLongitude: " + longitude
                        + "\nLatitude: " + latitude
                        + "\nUser Array: " + userArray.toString());
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MyMapFragment mapFragment = MyMapFragment.newInstance(username, longitude, latitude, jsonStrUserList);
                ft.add(R.id.container1, (Fragment)mapFragment).addToBackStack(null).commit();
                submitButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ListButton:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                PartnerFragment partnerFragment = PartnerFragment.newInstance(latitude, longitude);
                ft.replace(R.id.container1, (Fragment)partnerFragment).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        lm.removeUpdates(ll);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            requestLocationUpdates();
        }
        else{
            Toast.makeText(MainActivity.this, "Please grant location permission to use this feature", Toast.LENGTH_LONG);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates(){
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    }

    private void updateUserList(String userListTaskResponseStr){
        try {
            JSONObject tempJson = new JSONObject(userListTaskResponseStr);
            userArray = tempJson.getJSONArray("users");
            Partner tempPartner;
            if (userArray != null) {
                jsonStrUserList = new String[userArray.length()];
                for (int i = 0; i < userArray.length(); i++) {
                    tempJson = userArray.getJSONObject(i);
                    jsonStrUserList[i] = tempJson.toString();
                }
                Log.d("Updated user list", userArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
