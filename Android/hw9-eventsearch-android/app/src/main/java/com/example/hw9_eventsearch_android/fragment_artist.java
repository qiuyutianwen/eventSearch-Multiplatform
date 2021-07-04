package com.example.hw9_eventsearch_android;

import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fragment_artist extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout artist1_ll;
    private LinearLayout artist1_ll_details;
    private LinearLayout artist2_ll;
    private LinearLayout artist2_ll_details;

    private TextView artist1_Name_TextView;
    private TextView artist1_Followers_TextView;
    private TextView artist1_Popularity_TextView;
    private TextView artist1_CheckAt_TextView;
    private TextView artist1_no_details;

    private TextView artist2_Name_TextView;
    private TextView artist2_Followers_TextView;
    private TextView artist2_Popularity_TextView;
    private TextView artist2_CheckAt_TextView;
    private TextView artist2_no_details;

    private TextView no_records;
    public fragment_artist() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_artist, container, false);
        progressBar = v.findViewById(R.id.progressbar);
        assert getArguments() != null;
        String[] ArtistsTeams = getArguments().getStringArray("ArtistsTeams");
        String Segment = "None";
        if(getArguments() != null){
            if(getArguments().getString("Segment") != null){
                Segment = getArguments().getString("Segment");
            }
        }
        assert ArtistsTeams != null;
        Log.d("fragment_artist", ArtistsTeams[0] + "|||||" + ArtistsTeams[1]);
        no_records = v.findViewById(R.id.ad_fa_tv_no_records);
        if(Segment.equalsIgnoreCase("Music")){
            if(ArtistsTeams[0] != null){
                no_records.setVisibility(View.GONE);
                artist1_ll = v.findViewById(R.id.ad_fa_ll_artist1);
                artist1_ll.setVisibility(View.VISIBLE);
                artist1_ll_details = v.findViewById(R.id.ad_fa_ll_artist1_details);
                artist1_no_details = v.findViewById(R.id.ad_fa_tv_artist1_no_details);
                progressBar.setVisibility(View.VISIBLE);

                artist1_Name_TextView = v.findViewById(R.id.ad_tv_artist_artist1_Name);
                artist1_Followers_TextView = v.findViewById(R.id.ad_tv_artist_artist1_Followers);
                artist1_Popularity_TextView = v.findViewById(R.id.ad_tv_artist_artist1_Popularity);
                artist1_CheckAt_TextView = v.findViewById(R.id.ad_tv_artist_artist1_CheckAt);
                getSpotifyArtist(ArtistsTeams[0], artist1_ll_details, artist1_Name_TextView, artist1_Followers_TextView, artist1_Popularity_TextView, artist1_CheckAt_TextView, artist1_no_details);
            }
            if(ArtistsTeams[1] != null){
                no_records.setVisibility(View.GONE);
                artist2_ll = v.findViewById(R.id.ad_fa_ll_artist2);
                artist2_ll.setVisibility(View.VISIBLE);
                artist2_ll_details = v.findViewById(R.id.ad_fa_ll_artist2_details);
                artist2_no_details = v.findViewById(R.id.ad_fa_tv_artist2_no_details);
                progressBar.setVisibility(View.VISIBLE);

                artist2_Name_TextView = v.findViewById(R.id.ad_tv_artist_artist2_Name);
                artist2_Followers_TextView = v.findViewById(R.id.ad_tv_artist_artist2_Followers);
                artist2_Popularity_TextView = v.findViewById(R.id.ad_tv_artist_artist2_Popularity);
                artist2_CheckAt_TextView = v.findViewById(R.id.ad_tv_artist_artist2_CheckAt);
                getSpotifyArtist(ArtistsTeams[1], artist2_ll_details, artist2_Name_TextView, artist2_Followers_TextView, artist2_Popularity_TextView, artist2_CheckAt_TextView, artist2_no_details);
            }
            if(ArtistsTeams[0] == null && ArtistsTeams[1] == null){
                no_records.setVisibility(View.VISIBLE);
                artist1_ll = v.findViewById(R.id.ad_fa_ll_artist1);
                artist1_ll.setVisibility(View.GONE);
                artist2_ll = v.findViewById(R.id.ad_fa_ll_artist2);
                artist2_ll.setVisibility(View.GONE);
            }
        }else{
            if(ArtistsTeams[0] != null){
                no_records.setVisibility(View.GONE);
                artist1_ll = v.findViewById(R.id.ad_fa_ll_artist1);
                artist1_ll.setVisibility(View.VISIBLE);
                artist1_ll_details = v.findViewById(R.id.ad_fa_ll_artist1_details);
                artist1_ll_details.setVisibility(View.GONE);
                artist1_no_details = v.findViewById(R.id.ad_fa_tv_artist1_no_details);
                artist1_no_details.setVisibility(View.VISIBLE);
                artist1_no_details.setText(ArtistsTeams[0] + ": No details.");
            }
            if(ArtistsTeams[1] != null){
                no_records.setVisibility(View.GONE);
                artist2_ll = v.findViewById(R.id.ad_fa_ll_artist2);
                artist2_ll.setVisibility(View.VISIBLE);
                artist2_ll_details = v.findViewById(R.id.ad_fa_ll_artist2_details);
                artist2_ll_details.setVisibility(View.GONE);
                artist2_no_details = v.findViewById(R.id.ad_fa_tv_artist2_no_details);
                artist2_no_details.setVisibility(View.VISIBLE);
                artist2_no_details.setText(ArtistsTeams[1] + ": No details.");
            }
            if(ArtistsTeams[0] == null && ArtistsTeams[1] == null){
                no_records.setVisibility(View.VISIBLE);
                artist1_ll = v.findViewById(R.id.ad_fa_ll_artist1);
                artist1_ll.setVisibility(View.GONE);
                artist2_ll = v.findViewById(R.id.ad_fa_ll_artist2);
                artist2_ll.setVisibility(View.GONE);
            }
        }

        return v;

    }

    private void getSpotifyArtist(final String artistName, final LinearLayout artist_ll_details, final TextView Name_TextView, final TextView Followers_TextView, final TextView Popularity_TextView, final TextView CheckAt_TextView, final TextView artist_no_details){
        final RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String spotifyArtistUrl = NetworkUtils.buildSpotifyArtistUrl(artistName);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, spotifyArtistUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject details = new JSONObject(response);
                    JSONArray items = details.getJSONObject("artists").getJSONArray("items");
                    if(items.length() >= 1){
                        JSONObject artist = items.getJSONObject(0);
                        Name_TextView.setText(artist.getString("name"));
                        Log.d("followers", String.valueOf(artist.getJSONObject("followers").getInt("total")));
                        Followers_TextView.setText(String.valueOf(artist.getJSONObject("followers").getInt("total")));
                        Popularity_TextView.setText(String.valueOf(artist.getInt("popularity")));
                        String linkedText = String.format("<a href=\"%s\">Spotify</a> ", artist.getJSONObject("external_urls").getString("spotify"));
                        CheckAt_TextView.setText(Html.fromHtml(linkedText,Html.FROM_HTML_MODE_LEGACY));
                        CheckAt_TextView.setMovementMethod(LinkMovementMethod.getInstance());
                        artist_ll_details.setVisibility(View.VISIBLE);
                        artist_no_details.setVisibility(View.GONE);
                    }else{
                        artist_ll_details.setVisibility(View.GONE);
                        artist_no_details.setVisibility(View.VISIBLE);
                        artist_no_details.setText(artistName + ": No details.");
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