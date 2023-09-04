package com.example.label.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.children
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ObjectUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils

/**
 * 创建时间：2023/2/12
 * 编写人： 陈陈陈
 * 功能描述：
 */
class PizhuContainView :FrameLayout {
    companion object{
        val minPizhuWidth = SizeUtils.dp2px(100f).toFloat()
    }
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initViews(context,attrs)
    }


    private var mViewDownX:Float = 0f
    private var mViewDownY:Float = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("MyMovingDraggable", "onTouch0")
        if(null != event){
            mViewDownX = event.x
            mViewDownY = event.y
        }
        return super.onTouchEvent(event)
    }

    private fun initViews(context: Context, attrs: AttributeSet?) {
        setOnClickListener {
            onMyClick()
        }
    }

    private fun onMyClick(){
        Log.d("MyMovingDraggable", "onMyClick")

        if(null != currentPizhu){
            setCurrentPizhuType()
        }else {
            if(childCount>0){
                for(item in children){
                    if(item is PizhuView){

                        val rect = Rect(item.x.toInt(), item.y.toInt(),item.x.toInt()+item.width,item.y.toInt()+item.height)
                        if(rect.contains(mViewDownX.toInt(), mViewDownY.toInt())){
                            currentPizhu = item
                            setCurrentPizhuType()
                            return
                        }
                    }
                }
            }
            addPizhu(mViewDownX- minPizhuWidth /2,mViewDownY - SizeUtils.dp2px(25f).toFloat())
        }
    }

    private fun setCurrentPizhuType() {
        when (currentPizhu?.type) {
            0 -> {
                currentPizhu?.setCurrentType(1)
                KeyboardUtils.hideSoftInput(currentPizhu!!)
            }
            1 -> {
                currentPizhu?.setCurrentType(2)
                if(ObjectUtils.isEmpty(currentPizhu!!.et_text.text)){
                    removeView(currentPizhu)
                }
                currentPizhu = null
            }
            2 -> {
                currentPizhu?.setCurrentType(0)
                post{
                    KeyboardUtils.showSoftInput(currentPizhu!!)
                }
            }
            -1 -> {
                //无效批注，删除重新创建
                currentPizhu = null
                addPizhu(mViewDownX- minPizhuWidth /2,mViewDownY - SizeUtils.dp2px(25f).toFloat())
            }
        }
    }

    var currentPizhu : PizhuView? = null
    fun addPizhu(transX: Float, transY:Float){
        val pizhu = PizhuView(context)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        pizhu.x = transX
        pizhu.y = transY
//        layoutParams.setMargins(transX.toInt(), transY.toInt(),0,0)
        pizhu.layoutParams = layoutParams
        addView(pizhu)

        post{
            pizhu.setCurrentType(0)
            KeyboardUtils.showSoftInput(pizhu)
        }
        currentPizhu = pizhu
    }

    fun setCurrentTextColor(i: Int) {
        currentPizhu?.setCurrentTextColor(i)
    }

    /**
     * 点击确认
     */
    fun onConfirmed() {
        if(null != currentPizhu){
            currentPizhu?.setCurrentType(2)
            currentPizhu = null
        }
    }

    fun isPerformed():Boolean{
        if(childCount>0){
            children.forEach {
                if(it is PizhuView && ObjectUtils.isNotEmpty(it.et_text.text)){
                    return true
                }
            }
        }
        return false
    }

    fun getCoveredHeightByKeyboard(keyboardHeight:Int):Int {
        if(null != currentPizhu){
            val location = IntArray(2)
            currentPizhu!!.getLocationOnScreen(location)
            val i = ScreenUtils.getScreenHeight() - currentPizhu!!.height - location[1]
            return keyboardHeight - i
        }
        return 0
    }
}