package com.a11.cp_weather.data;


import com.a11.cp_weather.data.model.APIWeather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface API {

    @GET("weather")
    Call<APIWeather> getWeather(@QueryMap Map<String, String> query);
}
