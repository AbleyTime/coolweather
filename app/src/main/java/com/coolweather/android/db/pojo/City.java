package com.coolweather.android.db.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class City {
    @Id
    private Long id;
    @NotNull
    private String cityName;    //记录市的名字
    @NotNull
    private int cityCode;       //记录市的代号
    @NotNull
    private Long provinceId;      //记录当前市所属省的id

    @Generated(hash = 277886640)
    public City(Long id, @NotNull String cityName, int cityCode,
                @NotNull Long provinceId) {
        this.id = id;
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.provinceId = provinceId;
    }

    @Generated(hash = 750791287)
    public City() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return this.cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

}
