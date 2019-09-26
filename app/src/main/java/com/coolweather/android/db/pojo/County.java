package com.coolweather.android.db.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class County {
    @Id
    private Long id;
    @NotNull
    private String countyName;      //记录区县名称
    @NotNull
    private String weatherId;       //记录区县所对应的天气id
    @NotNull
    private Long cityId;     //记录当前县所属市
    @Generated(hash = 526466147)
    public County(Long id, @NotNull String countyName, @NotNull String weatherId,
            @NotNull Long cityId) {
        this.id = id;
        this.countyName = countyName;
        this.weatherId = weatherId;
        this.cityId = cityId;
    }
    @Generated(hash = 1991272252)
    public County() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCountyName() {
        return this.countyName;
    }
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
    public String getWeatherId() {
        return this.weatherId;
    }
    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
    public Long getCityId() {
        return this.cityId;
    }
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }


}
