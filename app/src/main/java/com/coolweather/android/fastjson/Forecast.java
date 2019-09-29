package com.coolweather.android.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 预报天气类
 */
public class Forecast {
    public String date;
    @JSONField(name = "cond")
    public More more;
    public Temperature tmp;

    public class More{
        @JSONField(name = "txt_d")
        public String info;
    }

    public class Temperature{
        public String max;
        public String min;
    }


    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", more=" + more +
                ", temp=" + tmp +
                '}';
    }
}
