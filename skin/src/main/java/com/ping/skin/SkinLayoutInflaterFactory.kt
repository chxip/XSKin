package com.ping.skin

import android.app.Activity
import android.content.Context
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.lang.Exception
import java.lang.reflect.Constructor
import java.util.*

/**
 * @ClassName: SkinLayoutInflaterFactory
 * @Description: 用来接管系统的view的生产过程
 * @Author: Ping
 * @CreateDate: 2022/6/14 15:04
 * activity 用于修改状态栏的颜色等
 */
class SkinLayoutInflaterFactory(val activity: Activity) :LayoutInflater.Factory2, Observer {
    //所有View的类包名
    private val mClassPrefixList =
            arrayOf("android.widget.", "android.webkit.", "android.app.", "android.view.")

    //View的两个参数的构造函数格式
    private val mConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
    //缓存View的构造函数
    private val mConstructorMap = HashMap<String, Constructor<out View?>>()

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private var skinAttribute: SkinAttribute? = SkinAttribute()

    /**
     * 创建View的方法
     */
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = createSDKView(name, context, attrs)
        if (null == view) {
            view = createView(name, context, attrs)
        }
        //这就是我们加入的逻辑
        if (null != view) {
            //判断这个view需不需要换肤(有没有需要换肤的属性)，如果有，则记录下来
            //使用skinAttribute 类来保存相关属性
            skinAttribute!!.look(view, attrs)
        }
        return view
    }

    private fun createSDKView(name: String, context: Context, attrs: AttributeSet): View? {
        //如果包含 . 则不是SDK中的view 可能是自定义view包括support库中的View
        if (-1 != name.indexOf('.')) {
            return null
        }
        //不包含就要在解析的 节点 name前，拼上： android.widget. 等尝试去反射
        for (i in mClassPrefixList.indices) {
            val view = createView(mClassPrefixList.get(i) + name,context, attrs)
            if (view != null) {
                return view
            }
        }
        return null
    }

    /**
     * 根据View的构造方法，反射创建对应的View
     */
    private fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        //反射获取View 两个参数的构造方法
        val constructor = findConstructor(context, name)
        constructor?.let {
            try {
                //反射创建对象
                return it.newInstance(context, attrs)
            } catch (e: Exception) {
            }
        }

        return null
    }


    /**
     * 反射获取View的两个参数的构造方法，并且缓存起来，仿照系统写法
     */
    private fun findConstructor(context: Context, name: String): Constructor<out View?>? {
        var constructor: Constructor<out View?>? = mConstructorMap.get(name)
        if (constructor == null) {
            try {
                val clazz = context.classLoader.loadClass(name).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*mConstructorSignature)
                mConstructorMap.put(name, constructor)
            } catch (e: Exception) {
            }
        }
        return constructor
    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return  null
    }

    /**
     * 接收到换肤事件，修改所有view的皮肤
     */
    override fun update(o: Observable?, arg: Any?) {
        //修改状态栏的颜色
        //SkinThemeUtils.updateStatusBarColor(activity)
        //换肤
        skinAttribute!!.applySkin()
    }

    /**
     * 清楚数据
     */
    fun clear(){
        skinAttribute = null
    }

}

