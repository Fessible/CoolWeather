package example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rhm on 2017/6/5.
 */

public class Aqi {

    public AqiCity city;

    public class AqiCity{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm;
    }

}

