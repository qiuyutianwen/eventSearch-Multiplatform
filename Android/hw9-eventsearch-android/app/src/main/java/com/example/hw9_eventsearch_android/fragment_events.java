package com.example.hw9_eventsearch_android;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class fragment_events extends Fragment {

    private LinearLayout artistRow;
    private LinearLayout venueRow;
    private LinearLayout dateRow;
    private LinearLayout categoryRow;
    private LinearLayout priceRangeRow;
    private LinearLayout ticketStatusRow;
    private LinearLayout buyTicketAtRow;
    private LinearLayout seatMapRow;

    private TextView artistTextView;
    private TextView venueTextView;
    private TextView dateTextView;
    private TextView categoryTextView;
    private TextView priceRangeTextView;
    private TextView ticketStatusTextView;
    private TextView buyTicketAtTextView;
    private TextView seatMapTextView;

    public fragment_events() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_events, container, false);

        String artist = getArguments().getString("ArtistsTeams");
        String venue = getArguments().getString("Venue");
        String date = getArguments().getString("Date");
        String category = getArguments().getString("Category");
        String priceRange = getArguments().getString("PriceRange");
        String ticketStatus = getArguments().getString("TicketStatus");
        String buyTicketAt = getArguments().getString("BuyTicketAt");
        String seatMap = getArguments().getString("SeatMap");
        Log.d("fragment_events", "ArtistsTeams: " + artist + " Venue: " + venue + " Date: " + date + " Category: " + category + " PriceRange: " + priceRange + " TicketStatus: " + ticketStatus + " BuyTicketAt: " + buyTicketAt + " SeatMap: " + seatMap);
        if (artist != null) {
            artistRow = v.findViewById(R.id.ad_fe_ll_artist);
            artistRow.setVisibility(View.VISIBLE);
            artistTextView = v.findViewById(R.id.ad_tv_event_artist);
            artistTextView.setText(artist);
        }
        if (venue != null) {
            venueRow = v.findViewById(R.id.ad_fe_ll_venue);
            venueRow.setVisibility(View.VISIBLE);
            venueTextView = v.findViewById(R.id.ad_tv_event_venue);
            venueTextView.setText(venue);
        }
        if (date != null) {
            dateRow = v.findViewById(R.id.ad_fe_ll_date);
            dateRow.setVisibility(View.VISIBLE);
            dateTextView = v.findViewById(R.id.ad_tv_event_date);
            dateTextView.setText(date);
        }
        if (category != null) {
            categoryRow = v.findViewById(R.id.ad_fe_ll_category);
            categoryRow.setVisibility(View.VISIBLE);
            categoryTextView = v.findViewById(R.id.ad_tv_event_category);
            categoryTextView.setText(category);
        }
        if (priceRange != null) {
            priceRangeRow = v.findViewById(R.id.ad_fe_ll_priceRange);
            priceRangeRow.setVisibility(View.VISIBLE);
            priceRangeTextView = v.findViewById(R.id.ad_tv_event_priceRange);
            priceRangeTextView.setText(priceRange);
        }
        if (ticketStatus != null) {
            ticketStatusRow = v.findViewById(R.id.ad_fe_ll_ticketStatus);
            ticketStatusRow.setVisibility(View.VISIBLE);
            ticketStatusTextView = v.findViewById(R.id.ad_tv_event_ticketStatus);
            ticketStatusTextView.setText(ticketStatus);
        }
        if (buyTicketAt != null) {
            buyTicketAtRow = v.findViewById(R.id.ad_fe_ll_buyTicketAt);
            buyTicketAtRow.setVisibility(View.VISIBLE);
            buyTicketAtTextView = v.findViewById(R.id.ad_tv_event_buyTicketAt);
            String linkedText = String.format("<a href=\"%s\">View Seat Map Here</a> ", buyTicketAt);
            buyTicketAtTextView.setText(Html.fromHtml(linkedText,Html.FROM_HTML_MODE_LEGACY));
            buyTicketAtTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (seatMap != null) {
            seatMapRow = v.findViewById(R.id.ad_fe_ll_seatMap);
            seatMapRow.setVisibility(View.VISIBLE);
            seatMapTextView = v.findViewById(R.id.ad_tv_event_seatMap);
            String linkedText = String.format("<a href=\"%s\">View Seat Map Here</a> ", seatMap);
            seatMapTextView.setText(Html.fromHtml(linkedText,Html.FROM_HTML_MODE_LEGACY));
            seatMapTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return v;

    }

}
