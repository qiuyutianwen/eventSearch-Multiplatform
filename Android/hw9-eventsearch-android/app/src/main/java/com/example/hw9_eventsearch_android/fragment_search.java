package com.example.hw9_eventsearch_android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fragment_search extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "SearchFragment";

    private ProgressBar progressBar;
    private EditText keywordEditText;
    private Spinner categorySpinner;
    private EditText distanceEditText;
    private Spinner distanceUnitSpinner;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private EditText radio2LocationEditText;

    private Button searchButton;
    private Button clearButton;

    private String keyword;
    private String category;
    private String distance;
    private String distance_unit;
    private String radio2Location;
    private String here;
    private String location;


    private boolean locationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final int LOCATION_REQUEST_CODE = 1234;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // get references to views
        keywordEditText = view.findViewById(R.id.main_fs_et_keyword);
        categorySpinner = view.findViewById(R.id.main_fs_sp_category);
        distanceEditText = view.findViewById(R.id.main_fs_et_distance);
        distanceUnitSpinner = view.findViewById(R.id.main_fs_sp_distance_unit);
        radioButton1 = view.findViewById(R.id.main_fs_radio1);
        radioButton2 = view.findViewById(R.id.main_fs_radio2);
        radio2LocationEditText = view.findViewById(R.id.main_fs_et_radio2_location);
        progressBar = view.findViewById(R.id.progressbar);

        // attach event listeners to search and clear buttons
        searchButton = view.findViewById(R.id.main_fs_button_search);
        searchButton.setOnClickListener(this);
        clearButton = view.findViewById(R.id.main_fs_button_clear);
        clearButton.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location loc : locationResult.getLocations()) {
                    location = Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude());
                    Log.d(TAG, "onLocationResult: After making location request, location = " + location);
                    break;
                }
            }
        };
        getLocationPermissions();

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // search button clicked
            case R.id.main_fs_button_search:

                performSearch();
                break;

            // clear button clicked
            case R.id.main_fs_button_clear:
                performClear();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void getLocationPermissions() {

        // request permission if not granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermissions: permissions not granted. Trying to request for permissions");
            locationPermissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "getLocationPermissions: permissions granted. Trying to get location");
            locationPermissionGranted = true;
            getCurrentLocation();
        }
    }

    public void performClear() {
        clearErrorMessages();
        resetInputs();
        progressBar.setVisibility(View.GONE);
    }

    public void clearErrorMessages() {
        keywordEditText.setError(null);
        radio2LocationEditText.setError(null);
    }

    public void resetInputs() {
        keywordEditText.setText("");
        categorySpinner.setSelection(0);
        distanceUnitSpinner.setSelection(0);
        distanceEditText.setText("10");
        radioButton1.setChecked(true);
        radio2LocationEditText.setText("");
    }

    public void performSearch() {
        clearErrorMessages();

        // get user inputs
        keyword = keywordEditText.getText().toString();
        category = categorySpinner.getSelectedItem().toString();
        distance = distanceEditText.getText().toString();
        distance_unit = distanceUnitSpinner.getSelectedItem().toString();
        radio2Location = radio2LocationEditText.getText().toString();

        try {
            Double.parseDouble(distance);
        } catch (Exception e) {
            distance = "10";
        }

        Log.d(TAG, "performSearch: Trying to search");

        if (radioButton1.isChecked()) {
            here = "true";
            getCurrentLocation();
            Log.d(TAG, "performSearch: " + location);
            if (location == null) {
//                location = "34.0266,-118.2831"; // fallback
            }

        } else {
            here = "false";
        }
        if(isFormValid()){
            progressBar.setVisibility(View.VISIBLE);

            // build URL using form data
            String url = NetworkUtils.buildEventUrl(keyword, category, distance, distance_unit, here, location, radio2Location).toString();

            final RequestQueue queue = Volley.newRequestQueue(getActivity());

            final Intent intent = new Intent(getActivity(), Activity_results.class);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray formatted_data = new JSONArray();
                        if(jsonObject.isNull("_embedded")){
                            intent.putExtra(Intent.EXTRA_TEXT, formatted_data.toString());
                            startActivity(intent);
                        }
                        JSONArray resultsArray = jsonObject.getJSONObject("_embedded").getJSONArray("events");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject event = resultsArray.getJSONObject(i);
                            JSONObject formatted_event = new JSONObject();
                            String icon = "no_icon";
                            if(!event.isNull("classifications")){
                                String segment = event.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
                                switch (segment){
                                    case "Music":
                                        icon = "music_icon";
                                        break;
                                    case "Sports":
                                        icon = "ic_sport_icon";
                                        break;
                                    case "Arts & Theatre":
                                        icon = "art_icon";
                                        break;
                                    case "Film":
                                        icon = "film_icon";
                                        break;
                                    case "Miscellaneous":
                                        icon = "miscellaneous_icon";
                                        break;
                                }
                            }
                            String name = event.getString("name");
                            String venue = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
                            String date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                            String event_id = event.getString("id");
                            formatted_event.put("icon", icon);
                            formatted_event.put("name", name);
                            formatted_event.put("venue",venue);
                            formatted_event.put("date", date);
                            formatted_event.put("event_id",event_id);
                            formatted_data.put(formatted_event);
                        }
                        Log.d("formatted_data", formatted_data.toString());
                        intent.putExtra(Intent.EXTRA_TEXT, formatted_data.toString());
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "A network error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

            queue.add(stringRequest);

        }
    }

    public boolean isFormValid() {
        boolean validity = true;

        if (keyword == null || keyword.equals("")) {
            keywordEditText.setError("Please enter mandatory field");
            validity = false;
        }

        if (radioButton2.isChecked() && (radio2Location.equals("") || radio2Location == null)) {
            radio2LocationEditText.setError("Please enter mandatory field");
            validity = false;
        }

        return validity;
    }

    public void getCurrentLocation() {

        try {
            if (locationPermissionGranted) {
                Log.d(TAG, "getCurrentLocation: Creating new task to get location");
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location locationResult) {
                                // Got last known location. In some rare situations this can be null.
                                Log.d(TAG, "onSuccess: successfully obtained location");

                                if (locationResult != null) {
                                    updateLocation(Double.toString(locationResult.getLatitude()) + "," + Double.toString(locationResult.getLongitude()));
                                    Log.d(TAG, "onSuccess: location retrieved: " + location);
                                }

                                createLocationRequest();
                            }
                        });
            }

        } catch (SecurityException e) {
            Toast.makeText(getActivity(), "Location permission denied. Cannot access device location.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void createLocationRequest() {
        final LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                try {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                } catch (SecurityException e) {

                }
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                0x1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    public void updateLocation(String l) {
        location = l;
        Log.d(TAG, "updateLocation: " + location);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
