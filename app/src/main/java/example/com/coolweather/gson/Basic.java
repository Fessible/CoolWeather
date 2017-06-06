package example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rhm on 2017/6/5.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
