package com.example.guo.ui;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guo.R;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private Button btn;
    DrawerLayout drawer;
    private int currentFragment = 0;

    @Override
    public void initView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        btn = (Button) findViewById(R.id.btn);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        switchFragment(tv1);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switchFragment(v);
    }

    private void switchFragment(View v) {
        FragmentManager manager = getSupportFragmentManager();
        switch (v.getId()){
            case R.id.tv1:
                manager.beginTransaction().replace(R.id.mainlayout,new FirstFragment(),"first").commit();
                break;
            case R.id.tv2:
                manager.beginTransaction().replace(R.id.mainlayout,new SecondFragment(),"second").commit();
                break;
            case R.id.tv3:
                break;
        }
        drawer.closeDrawer(Gravity.LEFT);
    }


}
