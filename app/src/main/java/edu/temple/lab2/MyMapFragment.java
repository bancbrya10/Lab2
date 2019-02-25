package edu.temple.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


public class MyMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_ID = "id";
    private static final double ARG_LONG = 0.0;
    private static final double ARG_LAT = 0.0;
    private static final String[] ARG_USERS = {};
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private String mId;
    double mLongitude;
    double mLatitude;
    ArrayList<Partner> userList;
    MapView mapView;
    GoogleMap map;

    public MyMapFragment() {
        // Required empty public constructor
    }

    public static MyMapFragment newInstance(String userId, double userLongitude, double userLatitude, String[] users) {
        MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, userId);
        args.putDouble(String.valueOf(ARG_LONG), userLongitude);
        args.putDouble(String.valueOf(ARG_LAT), userLatitude);
        args.putStringArray(String.valueOf(ARG_USERS), users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mId = getArguments().getString(ARG_ID);
            Log.d("MapFragmentLatLng1", "\n\nLat: " + mLatitude + "\n\nLong: " + mLongitude + "\n\n");
            userList = new ArrayList<Partner>();
            Partner tempPartner;
            JSONObject tempJson;
            String[] tempArray = getArguments().getStringArray(String.valueOf(ARG_USERS));
            String sb = "";
            for(int i = 0; i < tempArray.length; i++){
                sb += tempArray[i];
            }
            Log.d("Fragment user list", sb);
            for(int i = 0; i < tempArray.length; i++){
                try {
                    tempJson = new JSONObject(tempArray[i]);
                    tempPartner = new Partner(tempJson.getString("username")
                    ,(float)tempJson.getDouble("longitude")
                    ,(float)tempJson.getDouble("latitude"), new LatLng(mLatitude, mLongitude));
                    userList.add(tempPartner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(userList);
        mLongitude = userList.get(0).longitude;
        mLatitude = userList.get(0).latitude;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());
        map = googleMap;
        map.setMyLocationEnabled(true);
        for(int i=0; i < userList.size(); i++) {
            LatLng temp = new LatLng(userList.get(i).latitude, userList.get(i).longitude);
            map.addMarker(new MarkerOptions().position(temp).title("User: " + userList.get(i).id));
        }
        Log.d("MapFragmentLatLng2", "\n\nLat: " + mLatitude + "\n\nLong: " + mLongitude + "\n\n");
        LatLng currentLatLng = new LatLng(mLatitude,mLongitude);
        CameraPosition cp = CameraPosition.builder().target(currentLatLng).zoom(10).bearing(0).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }
}
