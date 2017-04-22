package com.a11.cp_weather.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.a11.cp_weather.R;
import com.a11.cp_weather.RecyclerAdapter;
import com.a11.cp_weather.data.WeatherContract;
import com.a11.cp_weather.data.WeatherSQLiteOpenHelper;
import com.a11.cp_weather.service.WeatherRequestService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> cities;
    RecyclerAdapter adapter;

    WeatherSQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cities = new ArrayList<>();
        loadCityList();

        dbHelper = new WeatherSQLiteOpenHelper(this);
        displayDatabaseInfo();

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new RecyclerAdapter(cities, this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);

        Intent intent = new Intent(this, WeatherRequestService.class);
        startService(intent);
    }

    public void loadCityList(){
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

    public void addCity(String city) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_CITY, city);
        getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
        cities.add(city);
        adapter.notifyDataSetChanged();

        displayDatabaseInfo();
        Intent intent = new Intent(this, WeatherRequestService.class);
        startService(intent);
    }

    public void onClickAdd(View view) {
        Log.i("Activity", "Click Add");
        EditText editText = (EditText) findViewById(R.id.editText);
        String city = editText.getText().toString();
        if (!city.equals("") && !cities.contains(city)) {
            addCity(city);
        }
        editText.setText("");
    }

    public void onClickCity(String city){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    private void displayDatabaseInfo() {
        String[] projection = {
                WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_CITY,
                WeatherContract.WeatherEntry.COLUMN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE};

        Log.i("Activity", WeatherContract.WeatherEntry.CONTENT_URI.toString());
        Cursor cursor = getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, projection, null, null, null);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Таблица содержит " + cursor.getCount() + " городов.\n\n");
        stringBuilder.append(
                WeatherContract.WeatherEntry._ID + " - " +
                        WeatherContract.WeatherEntry.COLUMN_CITY + " - " +
                        WeatherContract.WeatherEntry.COLUMN_TEMP + " - " +
                        WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE + "\n" );

        try {
            int idColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry._ID);
            int cityColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_CITY);
            int tempColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_TEMP);
            int weatherTypeColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentCity = cursor.getString(cityColumnIndex);
                double currentTemp = cursor.getDouble(tempColumnIndex);
                String currentWeatherType = cursor.getString(weatherTypeColumnIndex);

                stringBuilder.append(("\n" + currentID + " - " +
                        currentCity + " - " +
                        currentTemp + " - " +
                        currentWeatherType));
            }
        }catch (Exception e){
            Log.e("", e.getLocalizedMessage());
        }
        finally {
            cursor.close();
        }

        Log.i("WEATHER TABLE", " \n" + stringBuilder.toString());
    }
}
