package com.example.hw9_eventsearch_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_details extends AppCompatActivity {

    private DetailsActivityPagerAdapter mDetailsActivityPagerAdapter;
    private ViewPager mViewPager;

    // Events fragment data
    private String eventId;
    private String eventName;
    private String eventFragmentArtistsTeams;
    private String eventFragmentVenue;
    private String eventFragmentDate;
    private String eventFragmentSegment;
    private String eventFragmentCategory;
    private String eventFragmentPriceRange;
    private String eventFragmentTicketStatus;
    private String eventFragmentBuyTicketAt;
    private String eventFragmentSeatMap;

    // Artist(s) fragment data
    private String[] artistFragmentName;

    // Venue fragment data
    private String venueFragmentId;

    // twitter
    private final static String twitterHashtags = "CSCI571EventSearch";
    private String twitterText;

    private SharedPreferences favoritesSharedPreferences;
    private SharedPreferences.Editor favoritesEditor;
    private JSONArray favorites;
    private Toast favoritesToast;

    private Menu menu;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.details_menu, menu);

        if (Utils.isFavorite(eventId, favorites).isFavorite()) {
            menu.findItem(R.id.ad_action_favorite).setIcon(R.drawable.heart_fill_white);
        } else {
            menu.findItem(R.id.ad_action_favorite).setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ad_action_tweet:
                openTweetLink();
                break;
            case R.id.ad_action_favorite:
                toggleFavorite();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTwitterText() {
        twitterText = "Check out " + eventName + " located at " + eventFragmentVenue;
    }

    public void openTweetLink() {
        setTwitterText();
        Log.d("twitter", "open tweet link " + twitterText + " " + twitterHashtags);
        String twitterLink = "https://twitter.com/intent/tweet?text=" + twitterText + "&hashtags=" + twitterHashtags;
        Uri uri = Uri.parse(twitterLink);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void toggleFavorite() {
        String favoritesToastMsg = null;

        // try to get shared preferences JSON file if it exists.
        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
        }

        Utils.FavoriteCheckResult favCheckResult = Utils.isFavorite(eventId, favorites);

        // toggle favorites state
        // if selected item is currently a favorite item, un-favorite it (remove from sharedpreferences)
        if (favCheckResult.isFavorite()) {
            favorites.remove(favCheckResult.getFavoritePosition());
            favoritesToastMsg = eventName + " was removed from favorites.";
            menu.findItem(R.id.ad_action_favorite).setIcon(R.drawable.heart_outline_white);
        } else {    // else, favorite it (add to sharedpreferences)

            // create JSONObject containing data of new favorite
            JSONObject newFavorite = new JSONObject();
            try {
                newFavorite.put("event_id", eventId);
                newFavorite.put("name", eventName);
                newFavorite.put("date", eventFragmentDate);
                newFavorite.put("venue", eventFragmentVenue);
                switch (eventFragmentSegment){
                    case "Music":
                        newFavorite.put("icon", "music_icon");
                        break;
                    case "Sports":
                        newFavorite.put("icon", "ic_sport_icon");
                        break;
                    case "Arts & Theatre":
                        newFavorite.put("icon", "art_icon");
                        break;
                    case "Film":
                        newFavorite.put("icon", "film_icon");
                        break;
                    case "Miscellaneous":
                        newFavorite.put("icon", "miscellaneous_icon");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // insert new favorite into JSONArray
            favorites.put(newFavorite);

            // create toast message
            favoritesToastMsg = eventName + " was added to favorites.";
            menu.findItem(R.id.ad_action_favorite).setIcon(R.drawable.heart_fill_white);
        }

        // put JSONArray back into shared preferences file
        favoritesEditor.putString(getString(R.string.fav_json), favorites.toString());

        // save changes
        favoritesEditor.apply();

        if (favoritesToast != null) {
            favoritesToast.cancel();
        }

        favoritesToast = Toast.makeText(this, favoritesToastMsg, Toast.LENGTH_SHORT);
        favoritesToast.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        favoritesSharedPreferences = getSharedPreferences(getString(R.string.fav_shared_pref_file), MODE_PRIVATE);
        favoritesEditor = favoritesSharedPreferences.edit();

        try {
            favorites = new JSONArray(favoritesSharedPreferences.getString(getString(R.string.fav_json), ""));
        } catch (JSONException e) {
            e.printStackTrace();
            favorites = new JSONArray();
        }

        mDetailsActivityPagerAdapter = new DetailsActivityPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.ad_vp_container);
        mViewPager.setAdapter(mDetailsActivityPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String textFromResultsActivity = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

            try {
                JSONObject eventDetails = new JSONObject(textFromResultsActivity);

                eventId = eventDetails.optString("id", null);
                eventName = eventDetails.optString("name", null);
                eventFragmentArtistsTeams = null;
                JSONObject _embeded = eventDetails.getJSONObject("_embedded");
                if(!_embeded.isNull("attractions")){
                    JSONArray attractions = _embeded.getJSONArray("attractions");
                    if(attractions.length() == 1){
                        eventFragmentArtistsTeams = attractions.getJSONObject(0).getString("name");
                    }else if(attractions.length() >= 2){
                        eventFragmentArtistsTeams = attractions.getJSONObject(0).getString("name");
                        for(int i = 1; i < attractions.length(); ++i){
                            eventFragmentArtistsTeams += " | ";
                            eventFragmentArtistsTeams += attractions.getJSONObject(i).getString("name");
                        }
                    }
                }
                eventFragmentVenue = null;
                // Venue fragment data
                venueFragmentId = null;
                if(!_embeded.isNull("venues")){
                    JSONArray venues = _embeded.getJSONArray("venues");
                    eventFragmentVenue = venues.getJSONObject(0).getString("name");
                    venueFragmentId = venues.getJSONObject(0).getString("id");
                }
                eventFragmentDate = null;
                eventFragmentTicketStatus = null;
                if(!eventDetails.isNull("dates")){
                    JSONObject dates = eventDetails.getJSONObject("dates");
                    if(!dates.isNull("start")){
                        JSONObject start = dates.getJSONObject("start");
                        if(!start.isNull("localDate")){
                            eventFragmentDate = start.getString("localDate");
                        }
                    }
                    if(!dates.isNull("status")){
                        JSONObject status = dates.getJSONObject("status");
                        if(!status.isNull("code") ){
                            eventFragmentTicketStatus = status.getString("code");
                        }
                    }
                }
                eventFragmentSegment = null;
                eventFragmentCategory = null;
                if(!eventDetails.isNull("classifications")){
                    JSONObject classification = eventDetails.getJSONArray("classifications").getJSONObject(0);
                    eventFragmentSegment = classification.getJSONObject("segment").getString("name");
                    if(!classification.isNull("subGenre")){
                        eventFragmentCategory = classification.getJSONObject("subGenre").getString("name");
                    }
                    if(!classification.isNull("genre")){
                        eventFragmentCategory += " | ";
                        eventFragmentCategory += classification.getJSONObject("genre").getString("name");
                    }
                    if(!classification.isNull("segment")){
                        eventFragmentCategory += " | ";
                        eventFragmentCategory += classification.getJSONObject("segment").getString("name");
                    }
                    if(!classification.isNull("subType")){
                        String subType = classification.getJSONObject("subType").getString("name");
                        if(!subType.equalsIgnoreCase("Undefined")){
                            eventFragmentCategory += " | ";
                            eventFragmentCategory += subType;
                        }
                    }
                    if(!classification.isNull("type")){
                        String type = classification.getJSONObject("type").getString("name");
                        if(!type.equalsIgnoreCase("Undefined")){
                            eventFragmentCategory += " | ";
                            eventFragmentCategory += type;
                        }
                    }
                }
                eventFragmentPriceRange = null;
                if(!eventDetails.isNull("priceRanges")){
                    JSONObject priceRange = eventDetails.getJSONArray("priceRanges").getJSONObject(0);
                    eventFragmentPriceRange = priceRange.getString("min");
                    eventFragmentPriceRange += "-";
                    eventFragmentPriceRange += priceRange.getString("max");
                    eventFragmentPriceRange += " ";
                    eventFragmentPriceRange += priceRange.getString("currency");
                }
                eventFragmentBuyTicketAt = eventDetails.optString("url", null);
                eventFragmentSeatMap = null;
                if(!eventDetails.isNull("seatmap")){
                    eventFragmentSeatMap = eventDetails.getJSONObject("seatmap").getString("staticUrl");
                }

                // Artist(s) fragment data
                artistFragmentName = new String[2];
                if(!_embeded.isNull("attractions")) {
                    JSONArray attractions = _embeded.getJSONArray("attractions");
                    if(attractions.length() == 1){
                        artistFragmentName[0] = attractions.getJSONObject(0).getString("name");
                    }
                    else if(attractions.length() >= 2){
                        artistFragmentName[0] = attractions.getJSONObject(0).getString("name");
                        artistFragmentName[1] = attractions.getJSONObject(1).getString("name");
                    }
                }
                Log.d("Activity_details", artistFragmentName[0] + " " + artistFragmentName[1]);
                // set activity label to place name
                getSupportActionBar().setTitle(eventName);

                // set Twitter link text
                setTwitterText();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DetailsActivityPagerAdapter extends FragmentPagerAdapter {

        public DetailsActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                fragment_events eventFragment = new fragment_events(); // first tab
                Bundle args = eventFragment.getArguments();
                if (args == null) {
                    args = new Bundle();
                }
                args.putString("ArtistsTeams", eventFragmentArtistsTeams);
                args.putString("Venue", eventFragmentVenue);
                args.putString("Date", eventFragmentDate);
                args.putString("Category", eventFragmentCategory);
                args.putString("PriceRange", eventFragmentPriceRange);
                args.putString("TicketStatus", eventFragmentTicketStatus);
                args.putString("BuyTicketAt", eventFragmentBuyTicketAt);
                args.putString("SeatMap", eventFragmentSeatMap);
                Log.d("Activity_details", "ArtistsTeams: " + eventFragmentArtistsTeams + " Venue: " + eventFragmentVenue + " Date: " + eventFragmentDate + " Category: " + eventFragmentCategory + " PriceRange: " + eventFragmentPriceRange + " TicketStatus: " + eventFragmentTicketStatus + " BuyTicketAt: " + eventFragmentBuyTicketAt + " SeatMap: " + eventFragmentSeatMap);
                eventFragment.setArguments(args);
                return eventFragment;
            } else if (position == 1) {
                fragment_artist artistFragment = new fragment_artist(); // second tab
                Bundle args = artistFragment.getArguments();
                if (args == null) {
                    args = new Bundle();
                }
                args.putStringArray("ArtistsTeams", artistFragmentName);
                args.putString("Segment", eventFragmentSegment);
                artistFragment.setArguments(args);
                return artistFragment;
            } else {
                fragment_venue venueFragment = new fragment_venue(); // third tab
                Bundle args = venueFragment.getArguments();
                if (args == null) {
                    args = new Bundle();
                }
                args.putString("venueId", venueFragmentId);
                venueFragment.setArguments(args);
                return venueFragment;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
