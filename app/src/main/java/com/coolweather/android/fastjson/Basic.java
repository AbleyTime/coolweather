package com.coolweather.android.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 城市的基本信息类
 */
public class Basic {

    @JSONField(name = "city")
    public String cityName;     //城市名称
    @JSONField(name = "id")
    public String weatherId;    //当前城市对应的天气id
    public Update update;       //

    public class Update{
        @JSONField(name = "loc")
        public String updateTime;   //天气更新时间
    }


    @Override
    public String toString() {
        return "Basic{" +
                "cityName='" + cityName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", update=" + update +
                '}';
    }
}
