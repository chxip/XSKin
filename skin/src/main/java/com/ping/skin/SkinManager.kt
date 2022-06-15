package com.ping.skin

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.text.TextUtils
import com.ping.skin.utils.SkinResources
import java.lang.Exception
import java.util.*

/**
 * @ClassName: SkinManager
 * @Description: 皮肤管理类
 * @Author: Ping
 * @CreateDate: 2022/6/14 16:47
 */
object SkinManager: Observable() {
    /**
     * Activity生命周期回调
     */
    private var skinActivityLifecycle: ApplicationActivityLifecycle? = null
    private var mContext: Application? = null

    /**
     * 初始化 必须在Application中先进行初始化
     * @param application
     * @param skinPath 默认的皮肤路径
     */
    fun init(application: Application,skinPath:String?) {
        mContext = application
        //资源管理类 用于从 app/皮肤 中加载资源
        SkinResources.init(application.applicationContext)
        //注册Activity生命周期,并设置被观察者
        skinActivityLifecycle = ApplicationActivityLifecycle(this)
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle)
        //加载上次使用保存的皮肤
        loadSkin(skinPath)
    }


    /**
     * 记载皮肤并应用
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    fun loadSkin(skinPath: String?) {
        if (TextUtils.isEmpty(skinPath)) {
            //还原默认皮肤
            SkinResources.reset()
        } else {
            try {
                //宿主app的 resources;
                val appResource = mContext!!.resources
                //
                //反射创建AssetManager 与 Resource
                val assetManager = AssetManager::class.java.newInstance()
                //资源路径设置 目录或压缩包
                val addAssetPath =
                        assetManager.javaClass.getMethod("addAssetPath", String::class.java)
                addAssetPath.invoke(assetManager, skinPath)

                //根据当前的设备显示器信息 与 配置(横竖屏、语言等) 创建Resources
                val skinResource = Resources(assetManager, appResource.displayMetrics,
                                             appResource.configuration)
                //获取外部Apk(皮肤包) 包名
                val mPm = mContext!!.packageManager
                val info = mPm.getPackageArchiveInfo(skinPath!!, PackageManager.GET_ACTIVITIES)
                val packageName = info!!.packageName
                SkinResources.applySkin(skinResource, packageName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //通知采集的View 更新皮肤
        //被观察者改变 通知所有观察者
        setChanged()
        notifyObservers(null)
    }
}