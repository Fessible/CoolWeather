package example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rhm on 2017/6/5.
 */

//是daily_forecast的一个部分内容
public class Forecast {
    public String date;
    public Cond cond;
    public Tmp tmp;

    public class Cond{
        @SerializedName("txt_d")
        public String txt;
    }

    public class Tmp{
        public String max;
        public String min;
    }
}
