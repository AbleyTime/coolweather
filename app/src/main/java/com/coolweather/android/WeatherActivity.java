package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.ViewTarget;
import com.coolweather.android.fastjson.Forecast;
import com.coolweather.android.fastjson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 显示天气信息的活动
 */
public class WeatherActivity extends AppCompatActivity {

    private static final String TAG = "WeatherActivity";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;  //用于记录城市的天气id
    private Button navButton;
    public DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //将背景图和状态栏融合到一起
        if (Build.VERSION.SDK_INT >= 21) {
            //是android5.0及以上系统版本
            Window window = getWindow();    //获取当前活动的窗口
            View decorView = window.getDecorView();     //获取当前窗口的DecorView
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); //将活动的布局填充到状态栏上
            window.setStatusBarColor(Color.TRANSPARENT);    //将当前窗口的状态栏设置为透明色
        }

        //初始化控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        navButton = findViewById(R.id.nav_button);
        drawerLayout = findViewById(R.id.draw_layout);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(204,204,204); //设置下拉刷新进度条颜色


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //加载weather数据
        String weatherString = sharedPreferences.getString("weather", null);    //获取储存在本地的weather数据的json字符串
        if (weatherString != null) {
            //有缓存，直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);  //处理并展示weather对象中的数据
        } else {
            //无缓存，去服务端查询天气数据
            weatherLayout.setVisibility(View.INVISIBLE);    //scrollView设置为不可见
            mWeatherId = getIntent().getStringExtra("weather_id");  //获取上个页面传入的当前查询城市的weather_id
            requestWeather(mWeatherId);
        }


        //加载背景图片
        String bingPic = sharedPreferences.getString("bing_pic", null);
        if (bingPic != null) {
            //有缓存背景图片，使用Glide加载
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            //没有缓存背景图片，向服务器端获取
            loadBingPic();
        }

        //给下拉刷新绑定下拉刷新的监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                loadBingPic();
            }
        });

        //给导航按键设置点击事件
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将导航菜单显示出来，该方法中需要传入一个gravity，为保持和前面的一致所以选择使用GravityCompat.START
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



    }


    /*
     根据weather_id请求城市天气信息
      */
    public void requestWeather(String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=05ef561127c243af9cc4e9b9e95e97b4";
        Log.d(TAG, "requestWeather: 请求URL：" + url);
        //发送网络请求
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //响应失败,打印异常日志，弹出提示信息
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);  //关闭下拉刷新进度条
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //响应成功，解析天气数据
                final String responseText = response.body().string();
                Log.d(TAG, "onResponse: 响应回来的weather数据：" + responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                Log.d(TAG, "onResponse: 解析后的weather对象：" + weather);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            //响应数据正确,将数据存入SharedPreferences中
                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", responseText);
                            edit.apply();
                            mWeatherId = weather.basic.weatherId;   //使不同情景下请求的天气数据都能在再次请求相同的天气数据时有效
                            showWeatherInfo(weather);   //展示weather实体类中的数据
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息错误！", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);  //关闭下拉刷新进度条
                    }
                });
            }
        });
    }


    /*
    处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();    //清除该布局中的所有子布局视图
        //遍历forecastList中的数据将其展示到forecastLayout布局文件中
        for (Forecast forecast : weather.forecastList) {
            //将forecast_item布局文件序列化为View对象
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.tmp.max);
            minText.setText(forecast.tmp.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        //启动后台自动更新天气数据和背景图片的的服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /*
    加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "loadBingPic: 响应失败！");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "loadBingPic: 响应成功！");
                final String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //使用Glide加载网络图片资源
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });
    }

}
