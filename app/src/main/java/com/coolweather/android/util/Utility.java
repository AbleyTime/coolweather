package com.coolweather.android.util;


import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.MyApplication;
import com.coolweather.android.db.greendao.CityDao;
import com.coolweather.android.db.greendao.CountyDao;
import com.coolweather.android.db.greendao.ProvinceDao;
import com.coolweather.android.db.pojo.City;
import com.coolweather.android.db.pojo.County;
import com.coolweather.android.db.pojo.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 解析和处理服务器传回的数据
 */
public class Utility {
    private static final String TAG = "Utility";

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {

        ProvinceDao provinceDao = MyApplication.getDaoSession().getProvinceDao();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceJSON = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceJSON.getString("name"));
                    province.setProvinceCode(provinceJSON.getInt("id"));
                    provinceDao.insert(province);   //将数据存入SQLite数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, long provinceId) {
        CityDao cityDao = MyApplication.getDaoSession().getCityDao();

        try {
            JSONArray allCitys = new JSONArray(response);
            for (int i = 0; i < allCitys.length(); i++) {
                JSONObject cityJSON = allCitys.getJSONObject(i);
                City city = new City();
                city.setCityName(cityJSON.getString("name"));
                city.setCityCode(cityJSON.getInt("id"));
                city.setProvinceId(provinceId);
                cityDao.insert(city);
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的区县级数据
     */
    public static boolean handleCountyResponse(String response, long cityId) {
        CountyDao countyDao = MyApplication.getDaoSession().getCountyDao();

        try {
            JSONArray allCountys = new JSONArray(response);
            if (allCountys.length() ==0){
                Log.d(TAG, "handleCountyResponse: 后端数据为空！");
                return false;
            }
            for (int i = 0; i < allCountys.length(); i++) {
                County county = new County();
                JSONObject countyJSON = allCountys.getJSONObject(i);
                county.setCountyName(countyJSON.getString("name"));
                county.setWeatherId(countyJSON.getString("weather_id"));
                county.setCityId(cityId);
                countyDao.insert(county);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
