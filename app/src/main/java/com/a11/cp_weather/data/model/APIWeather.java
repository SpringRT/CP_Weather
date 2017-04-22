package com.a11.cp_weather.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class APIWeather {

    @SerializedName("weather")
    private List<Map<String, Object>> weather;

    @SerializedName("main")
    private Map<String, Object> main;

    @SerializedName("name")
    private String name;

    public List<Map<String, Object>> getWeather() {
        return weather;
    }

    public void setWeather(List<Map<String, Object>> weather) {
        this.weather = weather;
    }

    public Map<String, Object> getMain() {
        return main;
    }

    public void setMain(Map<String, Object> main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
