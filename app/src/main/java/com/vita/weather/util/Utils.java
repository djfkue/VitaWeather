package com.vita.weather.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by talos on 10/11/16.
 */
public class Utils {

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
}
