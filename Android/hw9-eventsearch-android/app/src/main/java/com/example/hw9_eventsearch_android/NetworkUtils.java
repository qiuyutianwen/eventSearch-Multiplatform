package com.example.hw9_eventsearch_android;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String API_BASE_URL =  "https://hw8-eventsearch-nodejs.uw.r.appspot.com";

    public static URL buildEventUrl(String keyword, String category, String distance, String distance_unit, String here, String location, String radio2Location) {
        URL url = null;
        if(here.equalsIgnoreCase("true")){
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath("event")
                    .appendQueryParameter("keyword", keyword)
                    .appendQueryParameter("category", category)
                    .appendQueryParameter("distance", distance)
                    .appendQueryParameter("distance_unit", distance_unit)
                    .appendQueryParameter("here", here)
                    .appendQueryParameter("loc", location)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                    .appendPath("event")
                    .appendQueryParameter("keyword", keyword)
                    .appendQueryParameter("category", category)
                    .appendQueryParameter("distance", distance)
                    .appendQueryParameter("distance_unit", distance_unit)
                    .appendQueryParameter("here", here)
                    .appendQueryParameter("address", radio2Location)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    public static String buildDetailsUrl(String eventId) {
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath("eventdetails")
                .appendQueryParameter("id", eventId)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url.toString();
    }

    public static String buildSpotifyArtistUrl(String keyword) {
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath("spotify")
                .appendQueryParameter("keyword", keyword)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url.toString();
    }

    public static String buildVenueDetailsUrl(String id) {
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath("venuedetails")
                .appendQueryParameter("id", id)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url.toString();
    }

}
