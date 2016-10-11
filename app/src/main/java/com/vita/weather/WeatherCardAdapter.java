package com.vita.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vita.weather.util.Utils;

/**
 * Created by talos on 16-9-30.
 */

public class WeatherCardAdapter extends RecyclerView.Adapter<WeatherCardAdapter.ViewHolder> {
    @Override
    public WeatherCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_weather, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView weatherIcon;
        TextView city;
        TextView tempCurrent;
        TextView tempUp;
        TextView tempDown;

        public ViewHolder(View v) {
            super(v);

            city = (TextView) v.findViewById(R.id.weather_city);
            city.setTypeface(Utils.getRobotoLightTypeface(v.getContext()));

            tempCurrent = (TextView) v.findViewById(R.id.current_temp);
            tempCurrent.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));

            tempUp = (TextView) v.findViewById(R.id.current_high_temp);
            tempUp.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));

            tempDown = (TextView) v.findViewById(R.id.current_low_temp);
            tempDown.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));
        }
    }

}
