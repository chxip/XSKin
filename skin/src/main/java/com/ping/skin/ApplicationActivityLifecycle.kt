package com.ping.skin

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import androidx.core.view.LayoutInflaterCompat
import java.lang.Exception
import java.util.*

/**
 * @ClassName: ApplicationActivityLifecycle
 * @Description: Activity 生命周期回调
 * @Author: Ping
 * @CreateDate: 2022/6/14 16:48
 */
class ApplicationActivityLifecycle(val mObserable: Observable) : ActivityLifecycleCallbacks {
    //保存当前Activity 和 LayoutInflaterFactory 的对应关系
    private val mLayoutInflaterFactories = ArrayMap<Activity, SkinLayoutInflaterFactory>()

    /**
     * Activity 创建完成回调
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //使用factory2 设置布局加载工程
        val skinLayoutInflaterFactory = SkinLayoutInflaterFactory(activity)

        /**
         * 更新布局视图
         */
        //获得Activity的布局加载器
        val layoutInflater = activity.layoutInflater
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            try {
                //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
                //如设置过抛出一次
                //设置 mFactorySet 标签为false
                val field = LayoutInflater::class.java.getDeclaredField("mFactorySet")
                field.isAccessible = true
                field.setBoolean(layoutInflater, false)
                LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //安卓9以上版本，mFactorySet 不存在了，所有直接mFactory2设置反射mFactory2
            try {
                val field = LayoutInflater::class.java.getDeclaredField("mFactory2")
                field.isAccessible = true
                field[layoutInflater] = skinLayoutInflaterFactory
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //保存Activity 和 skinLayoutInflaterFactory的对应关系
        mLayoutInflaterFactories[activity] = skinLayoutInflaterFactory
        //添加观察者
        mObserable.addObserver(skinLayoutInflaterFactory)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    /**
     * 页面推出时，清除数据
     */
    override fun onActivityDestroyed(activity: Activity) {
        val observer = mLayoutInflaterFactories.remove(activity)  as SkinLayoutInflaterFactory
        observer.clear()
        SkinManager.deleteObserver(observer)
    }

}