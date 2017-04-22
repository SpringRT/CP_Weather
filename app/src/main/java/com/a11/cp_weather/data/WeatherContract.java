package com.a11.cp_weather.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.a11.cp_weather";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = WeatherEntry.TABLE_NAME;

    public static final class WeatherEntry implements BaseColumns {
        public static final String TABLE_NAME = "cities";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WEATHER);

        public static final String CONTENT_LIST_TYPE = "list";
        public static final String CONTENT_ITEM_TYPE = "item";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_TEMP = "temp";
        public static final String COLUMN_WEATHER_TYPE = "weather_type";
    }
}
