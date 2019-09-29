package com.coolweather.android.fastjson;

/**
 * 空气质量类
 */
public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;  //城市空气质量指数
        public String pm25;
    }

    @Override
    public String toString() {
        return "AQI{" +
                "city=" + city +
                '}';
    }
}
