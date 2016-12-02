package com.example.guo.ui;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initView();
        initListener();
    }
    /**
     * 初始化view
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 初始化监听事件
     */
    public  abstract void initListener();

    /**
     * 获取布局文件
     * @return
     */
    public abstract int getLayoutId();
}
