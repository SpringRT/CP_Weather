package com.a11.cp_weather.service;


import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.a11.cp_weather.R;
import com.a11.cp_weather.data.API;
import com.a11.cp_weather.data.WeatherContract;
import com.a11.cp_weather.data.model.APIWeather;
import com.a11.cp_weather.data.model.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRequestService extends IntentService {
    double updateInterval = 10;

    public static String baseUrl;
    public static String appId;

    ArrayList<String> cities;
    Handler h = new Handler();

    public WeatherRequestService() {
        super("WeatherRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadCityList();
        h.removeCallbacks(updateRunnable);
        h.post(updateRunnable);
    }

    public void loadCityList(){
        cities.clear();
        String[] projection = {WeatherContract.WeatherEntry.COLUMN_CITY};
        Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, projection, null, null, null);
        try {
            int cityColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CITY);
            while (cursor.moveToNext()){
                cities.add(cursor.getString(cityColumnIndex));
            }

        } catch (Exception e){
            Log.e("", e.getLocalizedMessage());
        } finally {
            cursor.close();
        }
    }

    public void update() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);

        for (String city : cities) {
            Map<String, String> query = new HashMap<>();
            query.put("APPID", appId);
            query.put("q", city);

            final String tempCity = city;
            api.getWeather(query).enqueue(new Callback<APIWeather>() {
                @Override
                public void onResponse(Call<APIWeather> call, Response<APIWeather> response) {
                    Log.e("MyTag", call.request().toString());
                    if (response.isSuccessful()) {
                        updateDatabase(Weather.parse(response.body()), tempCity);
                    } else {
                        Log.e("Service", "response ain't successful");
                    }
                }

                @Override
                public void onFailure(Call<APIWeather> call, Throwable t) {
                    Log.e("Service", "onFailure");
                }
            });
        }
    }

    public void updateDatabase(Weather weather, String city){
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP, weather.getTemp());
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE, weather.getState());

        getContentResolver().update(WeatherContract.WeatherEntry.CONTENT_URI,
                contentValues, WeatherContract.WeatherEntry.COLUMN_CITY + "= ?", new String[]{city});

        SharedPreferences sp = getSharedPreferences("time", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("lastUpdate", System.currentTimeMillis());
        editor.apply();
    }

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i("Service", "Update");
            update();
            h.postDelayed(updateRunnable, (int) (updateInterval * 60 * 1000));
        }
    };

    @Override
    public void onCreate() {
        baseUrl = getString(R.string.base_url);
        appId = getString(R.string.APP_ID);
        cities = new ArrayList<>();
        super.onCreate();
    }
}
