package com.ping.skin.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

/**
 * @ClassName: SkinResources
 * @Description: 皮肤资源管理
 * @Author: Ping
 * @CreateDate: 2022/6/14 16:03
 */
object SkinResources {
    //皮肤包的包名
    private var mSkinPkgName: String? = null

    //当前是否默认皮肤
    private var isDefaultSkin = true

    // app原始的resource
    private var mAppResources: Resources? = null

    // 皮肤包的resource
    private var mSkinResources: Resources? = null

    /**
     * 初始化方法
     */
    fun init(context: Context) {
        mAppResources = context.resources
    }

    /**
     * 重置为默认
     */
    fun reset() {
        mSkinResources = null
        mSkinPkgName = ""
        isDefaultSkin = true
    }

    /**
     * 换肤
     * 设置当前皮肤的属性
     */
    fun applySkin(resources: Resources?, pkgName: String?) {
        mSkinResources = resources
        mSkinPkgName = pkgName
        //是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null
    }

    /**
     * 1.通过原始app中的resId(R.color.XX)获取到自己的 名字
     * 2.根据名字和类型获取皮肤包中的ID
     */
    fun getIdentifier(resId: Int): Int {
        if (isDefaultSkin) {
            return resId
        }
        val resName = mAppResources!!.getResourceEntryName(resId)
        val resType = mAppResources!!.getResourceTypeName(resId)
        return mSkinResources!!.getIdentifier(resName, resType, mSkinPkgName)
    }

    /**
     * 输入主APP的ID，到皮肤APK文件中去找到对应ID的颜色值
     * @param resId
     * @return
     */
    fun getColor(resId: Int): Int {
        if (isDefaultSkin) {
            return mAppResources!!.getColor(resId)
        }
        val skinId = getIdentifier(resId)
        return if (skinId == 0) {
            mAppResources!!.getColor(resId)
        } else mSkinResources!!.getColor(skinId)
    }

    fun getColorStateList(resId: Int): ColorStateList {
        if (isDefaultSkin) {
            return mAppResources!!.getColorStateList(resId)
        }
        val skinId = getIdentifier(resId)
        return if (skinId == 0) {
            mAppResources!!.getColorStateList(resId)
        } else mSkinResources!!.getColorStateList(skinId)
    }

    fun getDrawable(resId: Int): Drawable? {
        if (isDefaultSkin) {
            return mAppResources!!.getDrawable(resId)
        }
        //通过 app的resource 获取id 对应的 资源名 与 资源类型
        //找到 皮肤包 匹配 的 资源名资源类型 的 皮肤包的 资源 ID
        val skinId = getIdentifier(resId)
        return if (skinId == 0) {
            mAppResources!!.getDrawable(resId)
        } else mSkinResources!!.getDrawable(skinId)
    }


    /**
     * 可能是Color 也可能是drawable
     *
     * @return
     */
    fun getBackground(resId: Int): Any? {
        val resourceTypeName = mAppResources!!.getResourceTypeName(resId)
        return if ("color" == resourceTypeName) {
            getColor(resId)
        } else {
            // drawable
            getDrawable(resId)
        }
    }
}