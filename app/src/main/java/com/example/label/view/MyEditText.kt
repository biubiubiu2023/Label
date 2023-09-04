package com.example.label.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * 创建时间：2023/2/13
 * 编写人： 陈陈陈
 * 功能描述：
 */
class MyEditText:androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initViews(context,attrs)
    }

    private fun initViews(context: Context, attrs: AttributeSet?) {

    }

    private var downY: Int = 0
    private var downX: Int = 0
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d("MyMovingDraggable", "dispatchTouchEvent")

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val upX: Int = event.rawX.toInt() - downX
                val upY: Int = event.rawY.toInt() - downY
                if (abs(upX) <= ViewConfiguration.get(context).scaledTouchSlop && abs(upY) <= ViewConfiguration.get(context).scaledTouchSlop) {
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (!TextUtils.isEmpty(text) && selStart == selEnd) {
            setSelection(text!!.length)
        }
    }
}