package com.example.guo.util;

import android.os.Handler;
import android.util.Log;

import com.example.guo.MyApplication;

/**
 * Created by G on 2015/12/23.
 */
public class CommonUtil {
    /**
     * 主线程执行Runnable
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable){
        Handler handler = MyApplication.getMainHandler();
        //Log.e("ff","--------------------"+handler+"---"+runnable);
        handler.post(runnable);
    }


}
