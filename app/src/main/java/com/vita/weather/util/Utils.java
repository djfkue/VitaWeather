package com.vita.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.vita.weather.R;
import com.vita.weather.data.WeatherData;

/**
 * Created by talos on 10/11/16.
 */
public class Utils {

    public static final String[] CITY_NAMES = {
            "nanjing",
            "berlin",
            "beijing",
            "guangzhou"
    };

    private static Typeface robotoThin = null;
    private static Typeface robotoLight = null;

    public static Typeface getRobotoLightTypeface(Context context) {
        if (robotoLight == null) {
            robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/robotolight.ttf");
        }
        return robotoLight;
    }

    public static Typeface getRobotoThinTypeface(Context context) {
        if (robotoThin == null) {
            robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/robotothin.ttf");
        }
        return robotoThin;
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, int resId, double temperature) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";
        if (!isMetric(context)) {
            temperature = (temperature * 1.8) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        return String.format(context.getString(resId), temperature);
    }


    public static int getIconResourceForWeatherCondition(WeatherData data) {
        return getIconResourceForWeatherCondition(data.weather_id);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.mipmap.storm_weather;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.mipmap.rainy_weather;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.mipmap.rainy_weather;
        } else if (weatherId == 511) {
            return R.mipmap.snow_weather;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.mipmap.rainy_weather;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.mipmap.snow_weather;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.mipmap.cloudy_weather;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.mipmap.storm_weather;
        } else if (weatherId == 800) {
            return R.mipmap.clear_night;
        } else if (weatherId == 801) {
            return R.mipmap.haze_weather;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.mipmap.cloudy_weather;
        }
        return -1;
    }

}
