package com.example.hw9_eventsearch_android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_results extends RecyclerView.Adapter<Adapter_results.ResultHolder> {

    private ArrayList<EventResult> data;
    private ListItemClickListener clickListener;
    private Context myContext;

    public Adapter_results(ArrayList<EventResult> data, ListItemClickListener listener) {
        this.data = data;
        clickListener = listener;
    }

    public interface ListItemClickListener {
        void onListItemDetailsSurfaceClicked(int clickedItemIndex);
        void onListItemFavoriteToggleClicked(int clickedItemIndex);
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_result_list_item, parent, false);
        myContext = v.getContext();
        ResultHolder holder = new ResultHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {

        EventResult currentEventResult = data.get(position);
        Log.d("ResultsActivity", "currentEventResult: " + "Icon: "+currentEventResult.getIcon()+" Name: "+currentEventResult.getName() + " Venue: "+currentEventResult.getVenue() + " Date: "+currentEventResult.getDate() + " Event_id: "+ currentEventResult.getEvent_id());
        holder.iconImageView.setImageDrawable(getDrawable(myContext, currentEventResult.getIcon()));
        holder.nameTextView.setText(currentEventResult.getName());
        holder.venueTextView.setText(currentEventResult.getVenue());
        holder.dateTextView.setText(currentEventResult.getDate());

        if (currentEventResult.isFavorite()) {
            holder.favoriteImageView.setImageResource(R.drawable.heart_fill_red);
        } else {
            holder.favoriteImageView.setImageResource(R.drawable.heart_outline_black);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static Drawable getDrawable(@NonNull Context context, String name) {
        @NonNull Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return ResourcesCompat.getDrawable(resources, resourceId, null);
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView nameTextView;
        private TextView venueTextView;
        private TextView dateTextView;
        private ImageView favoriteImageView;
        private LinearLayout detailsClickableLL;

        public ResultHolder(View itemView) {
            super(itemView);

            iconImageView = itemView.findViewById(R.id.iv_event_result_icon);
            nameTextView = itemView.findViewById(R.id.tv_event_result_name);
            venueTextView = itemView.findViewById(R.id.tv_event_result_venue);
            dateTextView = itemView.findViewById(R.id.tv_event_result_date);
            favoriteImageView = itemView.findViewById(R.id.ra_iv_favorite_toggle);
            detailsClickableLL = itemView.findViewById(R.id.ar_ll_details_clickable_surface);

            detailsClickableLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    clickListener.onListItemDetailsSurfaceClicked(clickedPosition);
                }
            });

            favoriteImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    clickListener.onListItemFavoriteToggleClicked(clickedPosition);
                }
            });
        }

    }
}