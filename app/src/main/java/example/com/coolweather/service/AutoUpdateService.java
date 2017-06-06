package example.com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.bumptech.glide.Glide;

import java.io.IOException;

import example.com.coolweather.WeatherActivity;
import example.com.coolweather.gson.Weather;
import example.com.coolweather.util.HttpUtil;
import example.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        updateWeather();
        updatePic();
        Intent i = new Intent(this, AutoUpdateService.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long TriggerAtTime = SystemClock.elapsedRealtime()+8*60*60*1000;//8小时

        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, TriggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);

    }

    //更新图片
    private void updatePic() {
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String pic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("pic", pic);//将更新的内容存储到缓存中
                editor.apply();
            }
        });


    }

    //service是发生在界面显示出来以后，所以会有缓存,通过缓存获得weatherId来访问服务器
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
        final String weatherText = preferences.getString("weather", null);

        if (weatherText != null) {
            Weather weather = Utility.handleWeatherResponse(weatherText);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "https://api.heweather.com/x3/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

//            String weatherUrl = "https://api.heweather.com/x3/weather?cityid=" + weatherId + "&key=25b66c67c448456b88bd0f006f9adad9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weathterText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(weatherText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", weatherText);
                        editor.apply();
                    }
                }
            });
        }

    }
}
