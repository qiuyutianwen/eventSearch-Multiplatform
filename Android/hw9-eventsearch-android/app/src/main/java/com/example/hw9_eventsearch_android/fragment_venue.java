package com.example.hw9_eventsearch_android;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class fragment_venue extends Fragment implements OnMapReadyCallback {

    private ProgressBar progressBar;

    private LinearLayout nameRow;
    private LinearLayout addressRow;
    private LinearLayout cityRow;
    private LinearLayout phoneRow;
    private LinearLayout openHoursRow;
    private LinearLayout generalRuleRow;
    private LinearLayout childRuleRow;

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView cityTextView;
    private TextView phoneTextView;
    private TextView openHoursTextView;
    private TextView generalRuleTextView;
    private TextView childRuleTextView;

    private String name;
    private String address;
    private String city;
    private String phone;
    private String openHours;
    private String generalRule;
    private String childRule;

    public fragment_venue() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_venue, container, false);
        progressBar = v.findViewById(R.id.progressbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.ad_fv_map);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.ad_fv_map, mapFragment).commit();
        }

        String venueId = getArguments().getString("venueId");

        progressBar.setVisibility(View.VISIBLE);
        try {
            getVenueDetails(venueId, v);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mapFragment.getMapAsync(this);
        return v;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("fragment_venue", "onMapReady: map ready");
        Double destinationLocationLat = getArguments().getDouble("latitude");
        Double destinationLocationLng = getArguments().getDouble("longitude");
        Log.d("location", destinationLocationLat + ", " + destinationLocationLng);
        String destinationName = getArguments().getString("name");
        LatLng destinationLocation = new LatLng(destinationLocationLat, destinationLocationLng);

        googleMap.addMarker(new MarkerOptions().position(destinationLocation).title(destinationName));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLocation));
    }

    private void getVenueDetails(String venueId, final View v){
        final RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String venueDetailsUrl = NetworkUtils.buildVenueDetailsUrl(venueId);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, venueDetailsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject details = new JSONObject(response);
                    name = details.optString("name", null);
                    if (name != null) {
                        nameRow = v.findViewById(R.id.ad_fv_ll_name);
                        nameRow.setVisibility(View.VISIBLE);
                        nameTextView = v.findViewById(R.id.ad_tv_venue_name);
                        nameTextView.setText(name);
                        Log.d("nameTextView", name);
                    }
                    Log.d("venue name", name);
                    if(!details.isNull("address")){
                        JSONObject line1 = details.getJSONObject("address");
                        if(!line1.isNull("line1")){
                            address = line1.getString("line1");
                        }
                    }
                    if (address != null) {
                        addressRow = v.findViewById(R.id.ad_fv_ll_address);
                        addressRow.setVisibility(View.VISIBLE);
                        addressTextView = v.findViewById(R.id.ad_tv_venue_address);
                        addressTextView.setText(address);
                    }
                    Log.d("venue address", address);
                    if(!details.isNull("city")){
                        JSONObject cityObject = details.getJSONObject("city");
                        if(!cityObject.isNull("name")){
                            city = cityObject.getString("name");
                        }
                    }
                    if(!details.isNull("state")){
                        JSONObject stateObject = details.getJSONObject("state");
                        if(!stateObject.isNull("name")){
                            city += ", " + stateObject.getString("name");
                        }
                    }
                    if (city != null) {
                        cityRow = v.findViewById(R.id.ad_fv_ll_city);
                        cityRow.setVisibility(View.VISIBLE);
                        cityTextView = v.findViewById(R.id.ad_tv_venue_city);
                        cityTextView.setText(city);
                    }
                    if(!details.isNull("boxOfficeInfo")){
                        JSONObject boxOfficeInfo = details.getJSONObject("boxOfficeInfo");
                        if(!boxOfficeInfo.isNull("phoneNumberDetail")){
                            phone = boxOfficeInfo.getString("phoneNumberDetail");
                        }
                        if(!boxOfficeInfo.isNull("openHoursDetail")){
                            openHours = boxOfficeInfo.getString("openHoursDetail");
                        }
                    }
                    if (phone != null) {
                        phoneRow = v.findViewById(R.id.ad_fv_ll_phone);
                        phoneRow.setVisibility(View.VISIBLE);
                        phoneTextView = v.findViewById(R.id.ad_tv_venue_phone);
                        phoneTextView.setText(phone);
                    }
                    if (openHours != null) {
                        openHoursRow = v.findViewById(R.id.ad_fv_ll_openHours);
                        openHoursRow.setVisibility(View.VISIBLE);
                        openHoursTextView = v.findViewById(R.id.ad_tv_venue_openHours);
                        openHoursTextView.setText(openHours);
                    }
                    if(!details.isNull("generalInfo")){
                        JSONObject generalInfo = details.getJSONObject("generalInfo");
                        if(!generalInfo.isNull("generalRule")){
                            generalRule = generalInfo.getString("generalRule");
                        }
                        if(!generalInfo.isNull("childRule")){
                            childRule = generalInfo.getString("childRule");
                        }
                    }
                    if (generalRule != null) {
                        generalRuleRow = v.findViewById(R.id.ad_fv_ll_generalRule);
                        generalRuleRow.setVisibility(View.VISIBLE);
                        generalRuleTextView = v.findViewById(R.id.ad_tv_venue_generalRule);
                        generalRuleTextView.setText(generalRule);
                    }
                    if (childRule != null) {
                        childRuleRow = v.findViewById(R.id.ad_fv_ll_childRule);
                        childRuleRow.setVisibility(View.VISIBLE);
                        childRuleTextView = v.findViewById(R.id.ad_tv_venue_childRule);
                        childRuleTextView.setText(childRule);
                    }
                    if(!details.isNull("location")){
                        JSONObject location = details.getJSONObject("location");
                        Bundle args = getArguments();
                        if(!location.isNull("latitude")){
                            Double latitude = location.getDouble("latitude");
                            args.putDouble("latitude", latitude);
                        }
                        if(!location.isNull("longitude")){
                            Double longitude = location.getDouble("longitude");
                            args.putDouble("longitude", longitude);
                        }
                        setArguments(args);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "A network error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        queue.add(stringRequest);
    }
}
