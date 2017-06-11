package example.com.coolweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.coolweather.gson.Forecast;
import example.com.coolweather.gson.Weather;
import example.com.coolweather.service.AutoUpdateService;
import example.com.coolweather.util.HttpUtil;
import example.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by rhm on 2017/6/5.
 */

public class WeatherActivity extends AppCompatActivity {
    @BindView(R.id.btn_nav)
    public Button btn_nav;

    @BindView(R.id.weather_info)
    ScrollView weatherLayout;

    @BindView(R.id.title_city)
    TextView titleCity;

    @BindView(R.id.titile_time)
    TextView titleTime;

    @BindView(R.id.degree_text)
    TextView degreeText;

    @BindView(R.id.info_text)
    TextView infoText;

    @BindView(R.id.forecast_layout)
    LinearLayout forcastLayout;

    @BindView(R.id.aqi_text)
    TextView aqiText;

    @BindView(R.id.pm25_text)
    TextView pm25Text;

    @BindView(R.id.comfort_text)
    TextView comfortText;

    @BindView(R.id.wash_text)
    TextView carWashText;

    @BindView(R.id.sport_text)
    TextView sportText;

    @BindView(R.id.back_img)
    ImageView back_img;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refrshLayout;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);//初始化控件

        //刷新的颜色
        refrshLayout.setColorSchemeResources(R.color.colorPrimary);



        //设置透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {//5.0以上
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        final String weatherId;

        //查看是否有缓存，没有则到服务器中查询
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherText = preferences.getString("weather", null);
        if (weatherText != null) {
            Weather weather = Utility.handleWeatherResponse(weatherText);//解析缓存数据
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //到服务器中查找
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.GONE);//当没有数据的时候，不显示内容
            requestWeather(weatherId);
        }


        //设置监听事件
        refrshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                loadPic();
            }
        });


        //获取缓存中的图片，如果没有就去下载
        String pic = preferences.getString("pic", null);
        if (pic != null) {
            Glide.with(this).load(pic).into(back_img);
        } else {
            loadPic();
        }


        //导航
        btn_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    //返回的是背景图链接
    private void loadPic() {
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String pic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("pic", pic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(pic).into(back_img);
                    }
                });


            }
        });
    }


    public void requestWeather(final String weatherId) {
        String weatherUrl = "https://api.heweather.com/x3/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";


        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        refrshLayout.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        refrshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    //显示天气数据
    private void showWeatherInfo(Weather weather) {
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        titleCity.setText(weather.basic.cityName);
        titleTime.setText(weather.basic.update.updateTime.split(" ")[1]);//返回的时间：2017-06-06 21:28，为了只显示时间，通过split分割，取后面的时间信息
        degreeText.setText(weather.now.tmp+"℃");
        infoText.setText(weather.now.cond.txt);
        forcastLayout.removeAllViews();


        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forcast_item, forcastLayout, false);
            TextView date = (TextView) view.findViewById(R.id.date_text);
            TextView info = (TextView) view.findViewById(R.id.info_text);
            TextView max = (TextView) view.findViewById(R.id.max_text);
            TextView min = (TextView) view.findViewById(R.id.min_text);

            date.setText(forecast.date);
            info.setText(forecast.cond.txt);
            max.setText(forecast.tmp.max);
            min.setText(forecast.tmp.min);

            forcastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm);
        }

        String comfort = "舒适度：" + weather.suggestion.comf.txt;
        String carWash = "洗车指数：" + weather.suggestion.cw.txt;
        String sport = "运动建议：" + weather.suggestion.sport.txt;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);



    }


}
