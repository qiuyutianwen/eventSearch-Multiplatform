package com.example.hw9_eventsearch_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

public class fragment_favourites extends Fragment implements Adapter_results.ListItemClickListener {

    private SharedPreferences favoritesSharedPreferences;
    private SharedPreferences.Editor favoritesEditor;
    private JSONArray favorites;
    private ArrayList<EventResult> favoritesList;

    private Adapter_results adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private Toast favoritesToast;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);

        favoritesSharedPreferences = v.getContext().getSharedPreferences(getString(R.string.fav_shared_pref_file), v.getContext().MODE_PRIVATE);
        favoritesEditor = favoritesSharedPreferences.edit();

        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
            favoritesList = convertToArrayList(favorites);
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
            favoritesList = new ArrayList<>();
        }


        // new adapter
        adapter = new Adapter_results(favoritesList, this);

        recyclerView = v.findViewById(R.id.rv_favourites);
        recyclerView.setAdapter(adapter);

        emptyView = v.findViewById(R.id.main_tv_no_favorites);
        progressBar = v.findViewById(R.id.progressbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (favoritesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        return v;
    }

    public ArrayList<EventResult> convertToArrayList(JSONArray array) {
        ArrayList<EventResult> arrayList = new ArrayList<>();

        for (int i = 0; i < favorites.length(); i++) {
            JSONObject jObj;
            try {
                jObj = favorites.getJSONObject(i);
                String eventId = jObj.getString("event_id");
                String name = jObj.getString("name");
                String venue = jObj.getString("venue");
                String date = jObj.getString("date");
                String icon = jObj.getString("icon");
                arrayList.add(new EventResult(icon, name, venue, date, eventId, true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    @Override
    public void onListItemDetailsSurfaceClicked(int clickedItemIndex) {
        // show progress dialog
        progressBar.setVisibility(View.VISIBLE);

        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        String detailsUrl = NetworkUtils.buildDetailsUrl(favoritesList.get(clickedItemIndex).getEvent_id());
        final Intent intent = new Intent(getActivity(), Activity_details.class);


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
                Toast.makeText(getActivity(), "A network error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onListItemFavoriteToggleClicked(int clickedItemIndex) {
        EventResult currentResult = favoritesList.get(clickedItemIndex);
        String favoritesToastMsg = null;

        // try to get shared preferences JSON file if it exists.
        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
        }


        // we know that all items in the favorites list are favorites, so we don't need to check like we do in Activity_results
        currentResult.setFavorite(false);

        // remove item from sharedpreferences file as well as recyclerview list
        favorites.remove(clickedItemIndex);
        favoritesList.remove(clickedItemIndex);

        favoritesToastMsg = currentResult.getName() + " was removed from favorites.";


        // put JSONArray back into shared preferences file
        favoritesEditor.putString(getString(R.string.fav_json), favorites.toString());

        // save changes
        favoritesEditor.apply();

        // tell adapter to update view
        adapter.notifyDataSetChanged();

        if (favoritesToast != null) {
            favoritesToast.cancel();
        }

        favoritesToast = Toast.makeText(getActivity(), favoritesToastMsg, Toast.LENGTH_SHORT);
        favoritesToast.show();

        if (favoritesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}

