package com.ping.skin

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.ping.skin.utils.SkinResources
import java.util.ArrayList

/**
 * @ClassName: SkinAttribute
 * @Description: 这里面放了所有要换肤的view所对应的属性
 * @Author: Ping
 * @CreateDate: 2022/6/14 15:54
 */
class SkinAttribute {
    //需要换肤的属性
    private val mAttributes: MutableList<String> = ArrayList()
    init {
        mAttributes.add("background")
        mAttributes.add("src")
        mAttributes.add("textColor")
        mAttributes.add("drawableLeft")
        mAttributes.add("drawableTop")
        mAttributes.add("drawableRight")
        mAttributes.add("drawableBottom")
    }

    //记录换肤需要操作的View与属性信息
    private val mSkinViews: MutableList<SkinView> = ArrayList()


    /**
     * 记录下一个VIEW身上哪几个属性需要换肤
     */
    fun look(view: View, attrs: AttributeSet) {
        val mSkinPars: MutableList<SkinPair> = ArrayList()
        for (i in 0 until attrs.attributeCount) {
            //获得属性名  textColor/background
            val attributeName = attrs.getAttributeName(i)
            if (mAttributes.contains(attributeName)) {
                // #
                // ?722727272
                // @722727272
                val attributeValue = attrs.getAttributeValue(i)
                // 比如color 以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue
                }
                var resId: Int
                // 以 ？开头的表示使用 属性
                resId = if (attributeValue.startsWith("?")) {
                    val attrId = attributeValue.substring(1).toInt()
                    getResId(view.context, intArrayOf(attrId)).get(0)
                } else {
                    // 正常以 @ 开头
                    attributeValue.substring(1).toInt()
                }
                val skinPair = SkinPair(attributeName, resId)
                mSkinPars.add(skinPair)
            }
        }
        if (!mSkinPars.isEmpty() || view is SkinViewSupport) {
            val skinView = SkinView(view, mSkinPars)
            // 如果选择过皮肤 ，调用 一次 applySkin 加载皮肤的资源
            skinView.applySkin()
            mSkinViews.add(skinView)
        }
    }


    /*
       对所有的view中的所有的属性进行皮肤修改
     */
    fun applySkin() {
        for (mSkinView in mSkinViews) {
            mSkinView.applySkin()
        }
    }


    /**
     * 获得theme中的属性中定义的 资源id
     * @param context
     * @param attrs
     * @return
     */
    fun getResId(context: Context, attrs: IntArray): IntArray {
        val resIds = IntArray(attrs.size)
        val a = context.obtainStyledAttributes(attrs)
        for (i in attrs.indices) {
            resIds[i] = a.getResourceId(i, 0)
        }
        a.recycle()
        return resIds
    }


}

internal class SkinView(
    var view: View,
    //这个View的能被 换肤的属性与它对应的id 集合
    var skinPairs: List<SkinPair>
                       ) {
    /**
     * 对一个View中的所有的属性进行修改
     * 最终换肤的方法
     */
    fun applySkin() {
        //对实现了SkinViewSupport接口的自定义View进行换肤
        applySkinSupport()
        //对当前View进行换肤
        for (skinPair in skinPairs) {
            var left: Drawable? = null
            var top: Drawable? = null
            var right: Drawable? = null
            var bottom: Drawable? = null
            when (skinPair.attributeName) {
                "background" -> {
                    val background: Any? = SkinResources.getBackground(skinPair.resId)
                    background?.let {
                        //背景可能是 @color 也可能是 @drawable
                        if (it is Int) {
                            view.setBackgroundColor(it)
                        } else {
                            ViewCompat.setBackground(view, it as Drawable)
                        }
                    }
                }
                "src" -> {
                    val background: Any? = SkinResources.getBackground(skinPair.resId)
                    background?.let {
                        if (it is Int) {
                            (view as ImageView).setImageDrawable(
                                ColorDrawable((it as Int?)!!))
                        } else {
                            (view as ImageView).setImageDrawable(it as Drawable?)
                        }
                    }
                }
                "textColor" -> (view as TextView).setTextColor(SkinResources.getColorStateList(skinPair.resId))
                "drawableLeft" -> left = SkinResources.getDrawable(skinPair.resId)
                "drawableTop" -> top = SkinResources.getDrawable(skinPair.resId)
                "drawableRight" -> right = SkinResources.getDrawable(skinPair.resId)
                "drawableBottom" -> bottom = SkinResources.getDrawable(skinPair.resId)
                else -> {
                }
            }
            if (null != left || null != right || null != top || null != bottom) {
                (view as TextView).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
            }
        }
    }

    /**
     * 对实现了SkinViewSupport接口的自定义View进行换肤
     */
    private fun applySkinSupport() {
        if (view is SkinViewSupport) {
            (view as SkinViewSupport).applySkin()
        }
    }
}

/**
 * 需要换肤的属性名和资源id
 */
internal class SkinPair(
    //属性名
    var attributeName: String,
    //对应的资源id
    var resId: Int
                       )