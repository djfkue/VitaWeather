package com.vita.weather;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.transition.Transition;
import android.util.Pair;
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
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

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

                    String bgName = newTransitionName + getString(R.string.transition_shot_background);
                    View background = recyclerView.findViewWithTag(bgName);
                    names.add(bgName);
                    sharedElements.put(bgName, background);
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
    private WeatherCardAdapter adapter;

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

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        adapter = new WeatherCardAdapter(this);
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
        final int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_ALBUM_POSITION);
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

        getWindow().getSharedElementReenterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getSharedElementReenterTransition().removeListener(this);
                CardHolder cardHolder
                        = (CardHolder) recyclerView.findViewHolderForAdapterPosition(currentPosition);
                cardHolder.restoreVisiblity();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

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

        View background;

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

            background = v.findViewById(R.id.background);

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

            String bgTag = data.city + getString(R.string.transition_shot_background);
            background.setTransitionName(bgTag);
            background.setTag(bgTag);

        }

        @Override
        public void onClick(View v) {

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(50);

//            ViewPropertyAnimator iconAnimator = weatherIcon.animate();
//            iconAnimator.alpha(0.0f);

            ObjectAnimator cityAnimator = ObjectAnimator.ofFloat(city, "alpha", 1, 0.5f);

            ObjectAnimator tempCurAnimator = ObjectAnimator.ofFloat(tempCurrent, "alpha", 1, 0.5f);
            ObjectAnimator highAnimator = ObjectAnimator.ofFloat(tempMax, "alpha", 1, 0.5f);
            ObjectAnimator lowAnimator = ObjectAnimator.ofFloat(tempMin, "alpha", 1, 0.5f);

//            ViewPropertyAnimator cityAnimator = city.animate().alpha(0.0f);
//            ViewPropertyAnimator tempCurAnimator = tempCurrent.animate().alpha(0.0f);
//            ViewPropertyAnimator highAnimator = tempMax.animate().alpha(0.0f);
//            ViewPropertyAnimator lowAnimator = tempMin.animate().alpha(0.0f);

            animatorSet.playTogether(
                    cityAnimator,
                    tempCurAnimator,
                    highAnimator,
                    lowAnimator);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra(EXTRA_STARTING_ALBUM_POSITION, position);

                    if (!mIsDetailsActivityStarted) {
                        mIsDetailsActivityStarted = true;
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation(
                                        MainActivity.this,
                                        Pair.create((View)weatherBg, weatherBg.getTransitionName()),
                                        Pair.create(background, background.getTransitionName()));
                        startActivity(intent,
                                options.toBundle());
                        getWindow().setExitTransition(null);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorSet.start();

        }

        public void restoreVisiblity() {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(250);

            ObjectAnimator iconAnimator = ObjectAnimator.ofFloat(weatherIcon, "alpha", 0, 1);
            ObjectAnimator cityAnimator = ObjectAnimator.ofFloat(city, "alpha", 0, 1);

            ObjectAnimator tempCurAnimator = ObjectAnimator.ofFloat(tempCurrent, "alpha", 0, 1);
            ObjectAnimator highAnimator = ObjectAnimator.ofFloat(tempMax, "alpha", 0, 1);
            ObjectAnimator lowAnimator = ObjectAnimator.ofFloat(tempMin, "alpha", 0, 1);

//            ViewPropertyAnimator cityAnimator = city.animate().alpha(0.0f);
//            ViewPropertyAnimator tempCurAnimator = tempCurrent.animate().alpha(0.0f);
//            ViewPropertyAnimator highAnimator = tempMax.animate().alpha(0.0f);
//            ViewPropertyAnimator lowAnimator = tempMin.animate().alpha(0.0f);
            animatorSet.setInterpolator(new FastOutSlowInInterpolator());
            animatorSet.playTogether(
                    iconAnimator,
                    cityAnimator,
                    tempCurAnimator,
                    highAnimator,
                    lowAnimator);
            animatorSet.start();
        }
    }
}
