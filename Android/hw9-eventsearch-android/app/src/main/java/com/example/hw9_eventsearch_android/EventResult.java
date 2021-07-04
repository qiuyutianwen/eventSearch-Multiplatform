package com.example.hw9_eventsearch_android;

public class EventResult {
    private String icon;
    private String name;
    private String venue;
    private String date;
    private String event_id;
    private boolean isFavorite;

    public EventResult(String icon, String name, String venue, String date, String event_id, boolean isFavorite) {
        this.icon = icon;
        this.name = name;
        this.venue = venue;
        this.date = date;
        this.event_id = event_id;
        this.isFavorite = isFavorite;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public String getDate() {
        return date;
    }

    public String getEvent_id() {
        return event_id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
