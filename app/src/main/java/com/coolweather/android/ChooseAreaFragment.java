package com.coolweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.coolweather.android.adapter.PlaceAdapter;
import com.coolweather.android.db.greendao.CityDao;
import com.coolweather.android.db.greendao.CountyDao;
import com.coolweather.android.db.greendao.DaoSession;
import com.coolweather.android.db.greendao.ProvinceDao;
import com.coolweather.android.db.pojo.City;
import com.coolweather.android.db.pojo.County;
import com.coolweather.android.db.pojo.Province;
import com.coolweather.android.my_interface.MyCallback;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用于遍历省市县数据的碎片
 */
//public class ChooseAreaFragment extends Fragment implements View.OnClickListener {
public class ChooseAreaFragment extends Fragment implements MyCallback {

    private static final String TAG = "ChooseAreaFragment";
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private List<String> dataList = new ArrayList<>();
    private ProvinceDao provinceDao;
    private CityDao cityDao;
    private CountyDao countyDao;
    private List<Province> provinceList;  //省列表
    private List<City> cityList = new ArrayList<>();     //市列表
    private List<County> countyList = new ArrayList<>(); //县列表
    private Province selectedProvince;    //选中的省
    private City selectedCity;            //选中的市
    private int currentLevel;             //当前选中的级别


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //将布局文件动态加载进碎片
        View view = inflater.inflate(R.layout.choose_area_fragment, container, false);
        //获取引用
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        recyclerView = view.findViewById(R.id.recycler_view);
        DaoSession daoSession = MyApplication.getDaoSession();
        provinceDao = daoSession.getProvinceDao();
        cityDao = daoSession.getCityDao();
        countyDao = daoSession.getCountyDao();
        //初始化recycleView
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //给backButton绑定单击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    //查询选中省内所有的市
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //查询全国所有的省
                    queryProvinces();
                }
            }
        });
        //查询全国所有的省
        queryProvinces();

    }

    /*
    查询全国所有的省,并显示
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE); //隐藏backButton按钮
        //从SQLite查询数据，如果没有则从服务器中查询
        provinceList = provinceDao.loadAll();
        if (provinceList.size() > 0) {
            //SQLite中有数据，将数据存入dataList中，显示到recyclerView中
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            placeAdapter.notifyDataSetChanged();    //更新数据
            currentLevel = LEVEL_PROVINCE;
        } else {
            //SQLite数据库中没有数据，从服务器中查询数据
            //String address ="http://192.168.0.119:8080/queryPlace";   //自己写的后端
            String address = "http://guolin.tech/api/china";
            //根据传入的地址和类型从服务器中查询对应数据，解析其数据并存入SQLite中
            queryFromServer(address, "province");
        }
    }


    /*
    查询选中省份内所有城市，并显示
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //从SQLite中查询数据，如果没有则从服务器中查询
        CityDao cityDao = MyApplication.getDaoSession().getCityDao();
        cityList = cityDao.queryBuilder().where(CityDao.Properties.ProvinceId.eq(selectedProvince.getId())).list();
        if (cityList.size() > 0) {
            //SQLite数据库中有对应数据,将数据存入到dataList中，显示到recyclerView中
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            placeAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
        } else {
            //SQLite中没有对应数据，从服务器中查询数据
            //String address ="http://192.168.0.119:8080/queryPlace/"+selectedProvince.getId();
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(address, "city");
        }
    }

    /*
    查询选中市内所有县，并显示
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //从SQLite中查询数据，如果没有则从服务器中查询
        CountyDao countyDao = MyApplication.getDaoSession().getCountyDao();
        countyList = countyDao.queryBuilder().where(CountyDao.Properties.CityId.eq(selectedCity.getId())).list();
        if (countyList.size() > 0) {
            //SQLite数据库中有对应数据，将数据存入到dataList中，显示到recyclerView
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            placeAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
        } else {
            //SQL中没有对应数据,从服务器中查询数据
            //String address ="http://192.168.0.119:8080/queryPlace/"+selectedProvince.getId()+"/"+selectedCity.getId();
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            queryFromServer(address, "county");
        }
    }

    /*
    根据传入的请求地址和数据种类从服务器中查询对应数据
     */
    private void queryFromServer(String address,final String type) {
        showProgressDialog();//展示ProgressDialog,实现加载数据的效果
        Log.d(TAG, "请求地址："+address);
        //发送网络请求并在回调函数中处理结果
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //响应失败
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        //提示加载失败
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG, "响应失败错误提示："+e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //响应成功
                closeProgressDialog();
                String responseText = response.body().string(); //以String形式获取响应消息
                Boolean result = false; //用于记录解析数据是否成功
                //根据数据种类解析响应消息，并存将其存入SQLite数据库中
                System.out.println(responseText);
                if ("province".equals(type)) {
                    Log.d(TAG, "province");
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    Log.d(TAG, "city");
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    Log.d(TAG, "county");
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    //数据存储成功，调用对应的查询方法并显示
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }else {
                    Log.d(TAG, "数据解析失败！");
                }
            }
        });
    }


    //回调接口的具体逻辑，根据不同的item数据响应不同的操作
    @Override
    public void positionCallback(int position) {
        Intent intent ;
        if (currentLevel == LEVEL_PROVINCE) {
            selectedProvince = provinceList.get(position);
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            selectedCity = cityList.get(position);
            queryCounties();
        }else if (currentLevel == LEVEL_COUNTY){
            County selectedCounty = countyList.get(position);   //获取当前选中的county
            //跳转到WeatherActivity中，并将当前区县城市的countName和weatherId传递到WeatherActivity中
            intent = new Intent(getContext(),WeatherActivity.class);
            intent.putExtra("countName",selectedCounty.getCountyName());
            intent.putExtra("weather_id",selectedCounty.getWeatherId());
            startActivity(intent);
        }
    }

    /*
    初始化recycleView
     */
    private void initRecyclerView() {
        //绑定布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //绑定适配器
        placeAdapter = new PlaceAdapter(dataList, this);
        recyclerView.setAdapter(placeAdapter);
    }

    /*
    显示加载对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    /*
    关闭加载对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /*@Override
    public void onClick(View v) {
        //View.OnClickListener回调中的逻辑
    }*/
}

