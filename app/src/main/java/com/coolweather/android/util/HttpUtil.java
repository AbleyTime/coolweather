package com.coolweather.android.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 做网络请求的工具类
 */
public class HttpUtil {

    //使用OkHttp发送Http请求
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {

        OkHttpClient client = new OkHttpClient();   //构造一个HttpClient。相当于设置个人邮箱
        Request request = new Request.Builder().url(address).build();   //创建一个Request对象。相当于写信
        Call call = client.newCall(request);    //将Request封住为Call对象。相当于把信放进邮箱，成为设置后待发送的信件
        call.enqueue(callback);     //放置到请求队列，开始发送，并等待回复。相当于邮件开始发送，等待对方回信

    }
}



