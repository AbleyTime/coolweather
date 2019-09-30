package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.fastjson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    private  int anHour = 8 * 60 * 60 * 1000;    //8小时

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    服务启动的时候回调该方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();    //更新天气信息。即根据当前城市的weatherId从服务器端查询天气数据并存入到sharedPreference中
        updateBingPic();    //更新天气的背景图。方法同上
        //获取AlarmManager来设置定时任务
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;    //从系统开机开始，每隔8小时的时间，包括系统深度休眠的时间
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0); //从系统获取一个用于启动Service的PendingIntent的对象
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }


    /*
    更新天气信息
     */
    private void updateWeather() {
        //在sharedPreferences中获取当前显示的城市的weatherId
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //直接解析缓存数据,从中拿到当前城市的weatherId
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;  //获取当前城市的weatherId
            String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=05ef561127c243af9cc4e9b9e95e97b4";
            //发送网络请求
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //响应失败,打印异常日志
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //响应成功，解析天气数据
                    final String responseText = response.body().string();
                    Log.d(TAG, "updateWeather: 响应回来的weather数据：" + responseText);
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    Log.d(TAG, "updateWeather: 解析后的weather对象：" + weather);
                    if (weather != null && "ok".equals(weather.status)) {
                        //响应数据正确,将数据存入SharedPreferences中
                        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        edit.putString("weather", responseText);
                        edit.apply();
                    }
                }

            });
        }

    }

    /*
    更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "updateBingPic: 响应失败！");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "updateBingPic: 响应成功！");
                final String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();
            }
        });
    }
}
