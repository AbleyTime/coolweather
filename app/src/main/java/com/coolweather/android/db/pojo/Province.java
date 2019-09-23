package com.coolweather.android.db.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Province {

    @Id
    private Long id;
    @NotNull
    private String provinceName;    //记录省份名称
    @NotNull
    private int provinceCode;       //记录省份代号
    @Generated(hash = 352555051)
    public Province(Long id, @NotNull String provinceName, int provinceCode) {
        this.id = id;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
    }
    @Generated(hash = 1309009906)
    public Province() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getProvinceName() {
        return this.provinceName;
    }
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
    public int getProvinceCode() {
        return this.provinceCode;
    }
    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

/*
    @Entity：将我们的java普通类变为一个能够被greenDAO识别的数据库类型的实体类,默认是使用实体类的类名转化为大写作为表名，如需要另外设置，@Entity(nameInDb = "表名")
    @Id：对象的Id，使用Long类型作为EntityId，否则会报错。(autoincrement = true)表示主键会自增，如果false就会使用旧值 。
    @Property：可以自定义字段名，注意外键不能使用该属性
    @NotNull：属性不能为空
    @Transient：使用该注释的属性不会被存入数据库的字段中
    @Unique：该属性值必须在数据库中是唯一值
    @Generated：编译后自动生成的构造函数、方法等的注释，提示构造函数、方法等不能被修改
        */