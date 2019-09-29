package com.coolweather.android.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 天气预报建议类
 */
public class Suggestion {
    @JSONField(name = "comf")
    public Comfort comfort;
    @JSONField(name = "cw")
    public CarWash carWash;
    public Sport sport;

    public class Comfort{
        @JSONField(name = "txt")
        public String info;
    }

    public class CarWash{
        @JSONField(name = "txt")
        public String info;
    }

    public class Sport{
        @JSONField(name = "txt")
        public String info;
    }


    @Override
    public String toString() {
        return "Suggestion{" +
                "comfort=" + comfort +
                ", carWash=" + carWash +
                ", sport=" + sport +
                '}';
    }
}
