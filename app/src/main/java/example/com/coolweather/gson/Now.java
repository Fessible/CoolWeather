package example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rhm on 2017/6/5.
 */

public class Now {
    @SerializedName("tmp")
    public String tmp;

    public Cond cond;

    public class Cond {
        @SerializedName("txt")
        public String txt;
    }
}
