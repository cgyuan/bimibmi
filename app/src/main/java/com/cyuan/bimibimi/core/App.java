package com.cyuan.bimibimi.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * 全局的API接口。
 *
 */
public class App {

    public static boolean isDebug = false;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static Handler handler;


    /**
     * 初始化接口。这里会进行应用程序的初始化操作，一定要在代码执行的最开始调用。
     *
     * @param c
     *          Context参数，注意这里要传入的是Application的Context，千万不能传入Activity或者Service的Context。
     */
    public static void initialize(Context c) {
        context = c;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取全局Context，在代码的任意位置都可以调用，随时都能获取到全局Context对象。
     *
     * @return 全局Context对象。
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 获取创建在主线程上的Handler对象。
     *
     * @return 创建在主线程上的Handler对象。
     */
    public static Handler getHandler() {
        return handler;
    }

    /**
     * 返回当前应用的包名。
     */
    public static String getPackageName() {
        return context.getPackageName();
    }

}