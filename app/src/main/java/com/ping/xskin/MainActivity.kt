package com.ping.xskin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ping.skin.SkinManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    var isDark = false

    fun change(view: View?) {
        //换肤，皮肤包是独立的apk包，可以来自网络下载
        if(isDark){
            SkinManager.loadSkin(null)
        }else{
            SkinManager.loadSkin("/data/data/com.ping.xskin/darktheme-debug.apk")
        }
        isDark = !isDark

    }
}