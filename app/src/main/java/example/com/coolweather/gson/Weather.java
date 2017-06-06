package example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rhm on 2017/6/5.
 */

//总的Weather类，用来管理"HeWeather'
public class Weather {
    public String status;
    public Basic  basic;
    public Aqi aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
