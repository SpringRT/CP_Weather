package com.a11.cp_weather.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.a11.cp_weather.data.WeatherContract;
import com.a11.cp_weather.data.WeatherSQLiteOpenHelper;

public class WeatherProvider extends ContentProvider {

    public static final String TAG = WeatherProvider.class.getSimpleName();

    public static final int WEATHER = 100;
    public static final int CITY_ID = 200;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/#", CITY_ID);
    }

    private WeatherSQLiteOpenHelper dbOpenHelper;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new WeatherSQLiteOpenHelper(getContext());
        return true;
    }

    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        Log.i("WeatherProvider", " queri URI: " + uri.toString());
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case CITY_ID:
                selection = WeatherContract.WeatherEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(WeatherContract.WeatherEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return insertCity(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
            case CITY_ID:
                selection = WeatherContract.WeatherEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);

        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return updateCity(uri, values, selection, selectionArgs);
            case CITY_ID:
                selection = WeatherContract.WeatherEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCity(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private Uri insertCity(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateCity(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        return database.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return WeatherContract.WeatherEntry.CONTENT_LIST_TYPE;
            case CITY_ID:
                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}