package com.a11.cp_weather.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.a11.cp_weather.R;
import com.a11.cp_weather.data.WeatherContract;

public class DetailsActivity extends AppCompatActivity {

    String city;
    double currentTemp;
    String currentWeatherType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        city = (String) getIntent().getExtras().get("city");

        String[] projection = {
                WeatherContract.WeatherEntry.COLUMN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE};
        Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI,
                projection, WeatherContract.WeatherEntry.COLUMN_CITY + "= ?", new String[]{city}, null);

        if (cursor != null) {
            try {
                cursor.moveToNext();
                int tempColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP);
                int weatherTypeColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE);

                currentTemp = cursor.getDouble(tempColumnIndex);
                currentTemp = Math.round(currentTemp);
                currentWeatherType = cursor.getString(weatherTypeColumnIndex);

            } catch (Exception e) {
                Log.e("", e.getLocalizedMessage());
            } finally {
                cursor.close();
            }
        }
        setScreen();
    }

    public void setScreen(){
        ((TextView)findViewById(R.id.city_tv)).setText(city);
        ((TextView)findViewById(R.id.weather_tv)).setText((int)currentTemp + "°C \n" + currentWeatherType);
    }
}