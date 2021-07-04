package com.example.hw9_eventsearch_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_results extends AppCompatActivity implements Adapter_results.ListItemClickListener {

    private ArrayList<EventResult> listData;
    private Adapter_results adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Toast favoritesToast;

    private SharedPreferences favoritesSharedPreferences;
    private SharedPreferences.Editor favoritesEditor;
    private JSONArray favorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        progressBar = findViewById(R.id.progressbar);
        listData = new ArrayList<>();

        favoritesSharedPreferences = getSharedPreferences(getString(R.string.fav_shared_pref_file), MODE_PRIVATE);
        favoritesEditor = favoritesSharedPreferences.edit();

        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
        }

        // get data from MainActivity
        Intent callingIntent = getIntent();
        if (callingIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String jsonData = callingIntent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                JSONArray events = new JSONArray(jsonData);
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String icon = event.getString("icon");
                    String name = event.getString("name");
                    String venue = event.getString("venue");
                    String date = event.getString("date");
                    String event_id = event.getString("event_id");
                    Utils.FavoriteCheckResult favChkRes = Utils.isFavorite(event_id, favorites);
                    listData.add(new EventResult(icon, name, venue, date, event_id, favChkRes.isFavorite()));
                }
                Log.d("ResultsActivity", "listData: ***********");
                for(int i = 0; i < listData.size(); ++i){
                    Log.d("ResultsActivity", i + " : "+"Icon: "+listData.get(i).getIcon()+" Name: "+listData.get(i).getName() + " Venue: "+listData.get(i).getVenue() + " Date: "+listData.get(i).getDate() + " Event_id: "+ listData.get(i).getEvent_id());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // new adapter
        adapter = new Adapter_results(listData, this);

        recyclerView = findViewById(R.id.rv_event_results);
        recyclerView.setAdapter(adapter);

        TextView emptyView = findViewById(R.id.ar_tv_no_results);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (listData.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onListItemDetailsSurfaceClicked(int clickedItemIndex) {
        // show progressBar
        progressBar.setVisibility(View.VISIBLE);

        final RequestQueue queue = Volley.newRequestQueue(this);

        String detailsUrl = NetworkUtils.buildDetailsUrl(listData.get(clickedItemIndex).getEvent_id());
        final Intent intent = new Intent(this, Activity_details.class);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, detailsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);

                intent.putExtra(Intent.EXTRA_TEXT, response);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "A network error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onListItemFavoriteToggleClicked(int clickedItemIndex) {
        EventResult currentResult = listData.get(clickedItemIndex);
        String favoritesToastMsg = null;

        // try to get shared preferences JSON file if it exists.
        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
        }

        Utils.FavoriteCheckResult favCheckResult = Utils.isFavorite(currentResult, favorites);

        // toggle favorites state
        // if selected item is currently a favorite item, un-favorite it (remove from sharedpreferences)
        if (favCheckResult.isFavorite()) {
            currentResult.setFavorite(false);
            favorites.remove(favCheckResult.getFavoritePosition());
            favoritesToastMsg = currentResult.getName() + " was removed from favorites.";
        } else {    // else, favorite it (add to sharedpreferences)

            currentResult.setFavorite(true);

            // create JSONObject containing data of new favorite
            JSONObject newFavorite = new JSONObject();
            try {
                newFavorite.put("event_id", currentResult.getEvent_id());
                newFavorite.put("name", currentResult.getName());
                newFavorite.put("date", currentResult.getDate());
                newFavorite.put("venue", currentResult.getVenue());
                newFavorite.put("icon", currentResult.getIcon());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // insert new favorite into JSONArray
            favorites.put(newFavorite);

            // create toast message
            favoritesToastMsg = currentResult.getName() + " was added to favorites.";
        }

        // put JSONArray back into shared preferences file
        favoritesEditor.putString(getString(R.string.fav_json), favorites.toString());

        // save changes
        favoritesEditor.apply();

        // tell adapter to update view
        adapter.notifyItemChanged(clickedItemIndex);

        if (favoritesToast != null) {
            favoritesToast.cancel();
        }

        favoritesToast = Toast.makeText(this, favoritesToastMsg, Toast.LENGTH_SHORT);
        favoritesToast.show();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
//        adapter.notifyDataSetChanged();
        Log.d("onRestart", "***************");
        for(int i = 0; i < listData.size();++i){
            String event_id = listData.get(i).getEvent_id();
            JSONArray curr_favorites;
            try {
                curr_favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
            } catch (JSONException e) {
                e.printStackTrace();
                curr_favorites = new JSONArray();
            }
            Utils.FavoriteCheckResult curr_favCheckResult = Utils.isFavorite(event_id, curr_favorites);
            listData.get(i).setFavorite(curr_favCheckResult.isFavorite());
            adapter.notifyItemChanged(i);
        }
    }

}
