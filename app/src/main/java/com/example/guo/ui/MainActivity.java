package com.example.guo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guo.MyApplication;
import com.example.guo.R;
import com.nineoldandroids.view.ViewHelper;

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
        drawer.setDrawerShadow(R.drawable.drawer_background, GravityCompat.START);
        drawer.setScrimColor(Color.TRANSPARENT);
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
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final View mContent = drawer.getChildAt(0);
                View mMenu = drawerView;
                float translation = slideOffset*mMenu.getMeasuredWidth();
                //if (drawerView.getTag().equals("LEFT")) {
                    ViewHelper.setTranslationX(mContent, translation);
               // Log.e("GG","slideOffset:"+slideOffset+"\n" +"mMenu:"+mMenu.getMeasuredWidth()+"\n"+"translation:"+translation);
                    //ViewHelper.setTranslationX(mContent,slideOffset*windowWidth);
               // }
                ValueAnimator animator = ValueAnimator.ofFloat(0f,translation);
                animator.start();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                      //  mContent.setTranslationX();
                    }
                });
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
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
