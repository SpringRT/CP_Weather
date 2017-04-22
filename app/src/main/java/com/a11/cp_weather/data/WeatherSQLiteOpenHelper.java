package com.a11.cp_weather.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

public class WeatherSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weather.db";
    private static final int CURRENT_VERSION = 1;

    public WeatherSQLiteOpenHelper(@NonNull Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableRequest());
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_CITY, "Moscow");
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP, 4.2);
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE, "Sunny");
        db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);

        contentValues = new ContentValues();
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_CITY, "London");
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_TEMP, 1.5);
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE, "Cloudy");
        db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);
    }

    private String createTableRequest(){
        String request = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherContract.WeatherEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_TEMP + " REAL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_TYPE + " TEXT);";
        Log.i("", request);
        return request;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
