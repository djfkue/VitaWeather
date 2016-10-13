package com.vita.weather;

import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.vita.weather.data.WeatherData;
import com.vita.weather.data.WeatherDataModel;
import com.vita.weather.util.Utils;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String EXTRA_STARTING_ALBUM_POSITION = "extra_starting_item_position";
    static final String EXTRA_CURRENT_ALBUM_POSITION = "extra_current_item_position";

    private RecyclerView recyclerView;
    private Bundle mTmpReenterState;
    private boolean mIsDetailsActivityStarted;

    private final SharedElementCallback mCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mTmpReenterState != null) {
                int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
                int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
                if (startingPosition != currentPosition) {
                    // If startingPosition != currentPosition the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    String newTransitionName = Utils.CITY_NAMES[currentPosition];
                    View newSharedElement = recyclerView.findViewWithTag(newTransitionName);
                    if (newSharedElement != null) {
                        names.clear();
                        names.add(newTransitionName);
                        sharedElements.clear();
                        sharedElements.put(newTransitionName, newSharedElement);
                    }
                }

                mTmpReenterState = null;
            } else {
                // If mTmpReenterState is null, then the activity is exiting.
                View navigationBar = findViewById(android.R.id.navigationBarBackground);
                View statusBar = findViewById(android.R.id.statusBarBackground);
                if (navigationBar != null) {
                    names.add(navigationBar.getTransitionName());
                    sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                }
                if (statusBar != null) {
                    names.add(statusBar.getTransitionName());
                    sharedElements.put(statusBar.getTransitionName(), statusBar);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        WeatherCardAdapter adapter = new WeatherCardAdapter(this);
        recyclerView.setAdapter(adapter);

        setExitSharedElementCallback(mCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsDetailsActivityStarted = false;
    }

    @Override
    public void onActivityReenter(int requestCode, Intent data) {
        super.onActivityReenter(requestCode, data);
        mTmpReenterState = new Bundle(data.getExtras());
        int startingPosition = mTmpReenterState.getInt(EXTRA_STARTING_ALBUM_POSITION);
        int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
        if (startingPosition != currentPosition) {
            recyclerView.scrollToPosition(currentPosition);
        }
        postponeEnterTransition();
        recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                recyclerView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class WeatherCardAdapter extends RecyclerView.Adapter<CardHolder> {

        private Context context;

        public WeatherCardAdapter(Context context) {
            this.context = context;
        }

        @Override
        public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_weather, parent, false);
            CardHolder holder = new CardHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(CardHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return WeatherDataModel.getInstance(context).getDataArrayList().size();
        }


    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private class CardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView weatherBg;
        ImageView weatherIcon;
        TextView city;
        TextView tempCurrent;
        TextView tempMax;
        TextView tempMin;

        int position;

        public CardHolder(View v) {
            super(v);

            weatherBg = (ImageView) v.findViewById(R.id.weather_bg);

            city = (TextView) v.findViewById(R.id.weather_city);
            city.setTypeface(Utils.getRobotoLightTypeface(v.getContext()));

            tempCurrent = (TextView) v.findViewById(R.id.current_temp);
            tempCurrent.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));

            tempMax = (TextView) v.findViewById(R.id.current_high_temp);
            tempMax.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));

            tempMin = (TextView) v.findViewById(R.id.current_low_temp);
            tempMin.setTypeface(Utils.getRobotoThinTypeface(v.getContext()));

            weatherIcon = (ImageView) v.findViewById(R.id.weather_icon);

            itemView.setOnClickListener(this);

        }

        public void bind(int position) {
            bindWithWeather(
                    WeatherDataModel.getInstance(
                            itemView.getContext()).getDataArrayList().get(position));
            this.position = position;
        }

        private void bindWithWeather(WeatherData data) {
            city.setText(data.city);
            tempCurrent.setText(
                    Utils.formatTemperature(
                            tempCurrent.getContext(),
                            R.string.format_temperature,
                            data.temperature));
            tempMax.setText(
                    Utils.formatTemperature(
                            tempCurrent.getContext(),
                            R.string.format_temperature_max,
                            data.temperature_max));
            tempMin.setText(
                    Utils.formatTemperature(
                            tempCurrent.getContext(),
                            R.string.format_temperature_min,
                            data.temperature_min));

            weatherIcon.setImageResource(Utils.getIconResourceForWeatherCondition(data.weather_id));

            weatherBg.setTransitionName(data.city);
            weatherBg.setTag(data.city);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(EXTRA_STARTING_ALBUM_POSITION, position);

            if (!mIsDetailsActivityStarted) {
                mIsDetailsActivityStarted = true;
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                        weatherBg, weatherBg.getTransitionName()).toBundle());
            }
        }
    }
}
