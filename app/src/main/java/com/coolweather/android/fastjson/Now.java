package com.coolweather.android.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 当前天气的详细信息
 */
public class Now {
    @JSONField(name = "tmp")
    public String temperature;  //当前温度
    @JSONField(name = "cond")
    public More more;

    public class More {
        @JSONField(name = "txt")
        public String info; //当前天气
    }


    @Override
    public String toString() {
        return "Now{" +
                "temperature='" + temperature + '\'' +
                ", more=" + more +
                '}';
    }
}
