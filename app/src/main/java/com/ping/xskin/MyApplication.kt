package com.ping.xskin

import android.app.Application
import com.ping.skin.SkinManager

/**
 * @ClassName: MyApplication
 * @Description: java类作用描述
 * @Author: Ping
 * @CreateDate: 2022/6/14 16:56
 */
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this,null)
    }
}