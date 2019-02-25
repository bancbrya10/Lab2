package edu.temple.lab2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class PartnerFragment extends Fragment {

    private static final double ARG_LAT = 0;
    private static final double ARG_LONG = 0;

    private JSONArray userArray;
    private ArrayList<Partner> userList;
    private double mLat;
    private double mLong;

    public PartnerFragment() {

    }

    public static PartnerFragment newInstance(double latitude, double longitude) {
        PartnerFragment fragment = new PartnerFragment();
        Bundle args = new Bundle();
        args.putDouble(String.valueOf(ARG_LAT), latitude);
        args.putDouble(String.valueOf(ARG_LONG), longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            userList = new ArrayList<Partner>();
            String userListResponseStr = String.valueOf(new GetUserListTask().execute().get());
            JSONObject tempJson = new JSONObject(userListResponseStr);
            Partner tempPartner;
            userArray= tempJson.getJSONArray("users");
            if (userList != null) {
                String[] jsonStrUserList = new String[userArray.length()];
                for (int i = 0; i < userArray.length(); i++) {
                    tempJson = userArray.getJSONObject(i);
                    tempPartner = new Partner(tempJson.getString("user"),
                            tempJson.getDouble("latitude"),
                            tempJson.getDouble("longitude"),
                            new LatLng(mLat, mLong));
                    userList.add(tempPartner);
                }
                Collections.sort(userList);
                Log.d("Updated user list", userArray.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partner_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setAdapter(new MyPartnerRecyclerViewAdapter(userList, mLat, mLong));
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

}
