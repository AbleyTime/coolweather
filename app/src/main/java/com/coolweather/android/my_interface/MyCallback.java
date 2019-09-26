package com.coolweather.android.my_interface;

public interface MyCallback {
    //回调接口，传递position给fragment中，使其能够响应Item中不同内容的点击事件
    void positionCallback(int position);

}
