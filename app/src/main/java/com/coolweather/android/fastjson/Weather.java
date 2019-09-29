package com.coolweather.android.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 一个总的实例类，用于引用这个包中其它实体类
 */
public class Weather {

    public String status;   //表示请求的状态，ok表示成功，失败则返回具体的失败原因
    public AQI aqi;
    public Basic basic;
    public Now now;
    public Suggestion suggestion;
    @JSONField(name = "daily_forecast")
    public List<Forecast> forecastList;


    @Override
    public String toString() {
        return "Weather{" +
                "status='" + status + '\'' +
                ", aqi=" + aqi +
                ", basic=" + basic +
                ", now=" + now +
                ", suggestion=" + suggestion +
                ", forecastList=" + forecastList +
                '}';
    }
}
