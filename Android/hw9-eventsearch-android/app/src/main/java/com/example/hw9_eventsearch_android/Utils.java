package com.example.hw9_eventsearch_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    static final class FavoriteCheckResult {
        private boolean isFavorite;
        private int favoritePosition;

        public FavoriteCheckResult(boolean isFavorite, int favoritePosition) {
            this.isFavorite = isFavorite;
            this.favoritePosition = favoritePosition;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public int getFavoritePosition() {
            return favoritePosition;
        }
    }

    public static FavoriteCheckResult isFavorite(EventResult eventResultToCheck, JSONArray favorites) {
        boolean isFavorite = false;
        int favoritePosition = -1;

        for (int i = 0; i < favorites.length(); i++) {
            JSONObject jObj;
            try {
                jObj = favorites.getJSONObject(i);
                if (jObj.getString("event_id").equals(eventResultToCheck.getEvent_id())) {
                    isFavorite = true;
                    favoritePosition = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new FavoriteCheckResult(isFavorite, favoritePosition);
    }

    public static FavoriteCheckResult isFavorite(String eventIdToCheck, JSONArray favorites) {
        boolean isFavorite = false;
        int favoritePosition = -1;

        for (int i = 0; i < favorites.length(); i++) {
            JSONObject jObj;
            try {
                jObj = favorites.getJSONObject(i);
                if (jObj.getString("event_id").equals(eventIdToCheck)) {
                    isFavorite = true;
                    favoritePosition = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new FavoriteCheckResult(isFavorite, favoritePosition);
    }
}
