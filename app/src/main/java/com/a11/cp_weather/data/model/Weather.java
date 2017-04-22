package com.a11.cp_weather.data.model;


import android.net.Uri;

import java.util.Locale;
import java.util.Map;

public class Weather {
    private String city;
    private String state;
    private Uri img;
    private double temp;

    public static Weather parse(APIWeather weather) {
        String city = weather.getName();
        Map<String, Object> map = weather.getWeather().get(0);
        String state = map.get("main").toString() + " - " + map.get("description").toString();
        String img = map.get("icon").toString();
        double temp = Double.parseDouble(weather.getMain().get("temp").toString());

        return new Weather(city, state, img, temp);
    }

    private Weather(String city, String state, String img, double temp) {
        this.city = city;
        this.state = state;
        this.img = Uri.parse(String.format(Locale.getDefault(), "http://openweathermap.org/img/w/%s.png", img));
        this.temp = temp - 273;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public Uri getImg() {
        return img;
    }

    public double getTemp() {
        return temp;
    }
}

