package com.coolweather.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coolweather.android.ChooseAreaFragment;
import com.coolweather.android.R;
import com.coolweather.android.my_interface.MyCallback;
import java.util.List;

/**
 * 适配器的作用是将每个数据与item绑定
 */
public class PlaceAdapter extends RecyclerView.Adapter {
    private List<String> dataList;  //需要在recyclerView中展示的数据
    private View itemView;
    private MyCallback myCallback;
    //private View.OnClickListener callback;

    /*
      自定义ViewHolder类
        作用：一个临时的储存器，将每个item对象缓存起来，避免每一次都重新定义一个item对象载入布局，再加载数据
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_place);
        }
    }

    //带参的构造函数
    public PlaceAdapter(List<String> dataList,MyCallback myCallback) {
        this.dataList = dataList;
        this.myCallback = myCallback;
    }

    /*public PlaceAdapter(List<String> dataList , View.OnClickListener callback){
        this.dataList = dataList;
        this.callback = callback;
    }*/

    //用于创建列表项组件，使用该方法创建的组件会被自动缓存
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        final ViewHolder holder = new ViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallback.positionCallback(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    //负责为列表项组件绑定数据，每次组件重新显示出来时都会重新执行该方法
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            String placeName = dataList.get(position);
            viewHolder.textView.setText(placeName);
        }
    }

    //返回值决定包含多少个列表项
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
