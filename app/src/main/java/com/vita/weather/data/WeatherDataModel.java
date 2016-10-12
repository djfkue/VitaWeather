package com.vita.weather.data;

import android.content.Context;

import com.vita.weather.util.Utils;

import java.util.ArrayList;

/**
 * Created by talos on 10/12/16.
 */

public class WeatherDataModel {

    private static WeatherDataModel instance;

    private ArrayList<WeatherData> dataArrayList;

    public WeatherDataModel() {
        dataArrayList = new ArrayList<>();

        WeatherData data = new WeatherData();
        data.city = Utils.CITY_NAMES[0];
        data.temperature = 20;
        data.temperature_max = 25;
        data.temperature_min = 15;
        data.weather_id = 801;

        dataArrayList.add(data);

        data = new WeatherData();
        data.city = Utils.CITY_NAMES[1];
        data.temperature = 20;
        data.temperature_max = 25;
        data.temperature_min = 15;
        data.weather_id = 761;

        dataArrayList.add(data);

        data = new WeatherData();
        data.city = Utils.CITY_NAMES[2];
        data.temperature = 20;
        data.temperature_max = 25;
        data.temperature_min = 15;
        data.weather_id = 525;

        dataArrayList.add(data);
    }

    public static WeatherDataModel getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherDataModel();
        }
        return instance;
    }

    public ArrayList<WeatherData> getDataArrayList() {
        return dataArrayList;
    }

}
