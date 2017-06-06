package example.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Scroller;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //如果选定了城市的天气，一打开就会默认显示该城市的天气

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weather = preferences.getString("weather", null);//获取缓存信息
        if (weather != null) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
