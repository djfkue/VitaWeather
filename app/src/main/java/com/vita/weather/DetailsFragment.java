package com.vita.weather;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.vita.weather.data.WeatherDataModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private static final String ARG_ALBUM_IMAGE_POSITION = "arg_album_image_position";
    private static final String ARG_STARTING_ALBUM_IMAGE_POSITION = "arg_starting_album_image_position";
    private View background;

    public static DetailsFragment newInstance(int position, int startingPosition) {
        Bundle args = new Bundle();
        args.putInt(ARG_ALBUM_IMAGE_POSITION, position);
        args.putInt(ARG_STARTING_ALBUM_IMAGE_POSITION, startingPosition);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ImageView weatherBackground;
    private int mStartingPosition;
    private int mAlbumPosition;
    private boolean mIsTransitioning;
    private long mBackgroundImageFadeMillis;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartingPosition = getArguments().getInt(ARG_STARTING_ALBUM_IMAGE_POSITION);
        mAlbumPosition = getArguments().getInt(ARG_ALBUM_IMAGE_POSITION);
        mIsTransitioning = savedInstanceState == null && mStartingPosition == mAlbumPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        weatherBackground = (ImageView) rootView.findViewById(R.id.weather_bg);
        String city = WeatherDataModel.getInstance(getContext()).getDataArrayList().get(mAlbumPosition).city;
        weatherBackground.setTransitionName(city);
        weatherBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                weatherBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                getActivity().startPostponedEnterTransition();
                return true;
            }
        });

        background = rootView.findViewById(R.id.background);
        background.setTransitionName(city + getString(R.string.transition_shot_background));

        if (mIsTransitioning) {
            getActivity().getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {

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

        return rootView;
    }

    /**
     * Returns the shared element that should be transitioned back to the previous Activity,
     * or null if the view is not visible on the screen.
     */
    @Nullable
    ImageView getWeatherBackground() {
        if (isViewInBounds(getActivity().getWindow().getDecorView(), weatherBackground)) {
            return weatherBackground;
        }
        return null;
    }

    View getViewBackground() {
        return background;
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    private static boolean isViewInBounds(@NonNull View container, @NonNull View view) {
        Rect containerBounds = new Rect();
        container.getHitRect(containerBounds);
        return view.getLocalVisibleRect(containerBounds);
    }

}
