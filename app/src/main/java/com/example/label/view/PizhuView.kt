package com.example.label.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.label.R

/**
 * 创建时间：2023/2/9
 * 编写人： 陈陈陈
 * 功能描述：
 */
class PizhuView : FrameLayout {

    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initViews(context,attrs)
    }
    lateinit var et_text:EditText
    lateinit var iv_close: ImageView
    lateinit var iv_expend:ImageView
    private var bgView: View? = null
    var draggable:MyMovingDraggable? = null

    private fun initViews(context: Context, attrs: AttributeSet?) {
        val view = inflate(context, R.layout.item_pizhu, this)

        et_text = view.findViewById(R.id.et_text)
        iv_close = view.findViewById(R.id.iv_close)
        iv_expend = view.findViewById(R.id.iv_expend)
        bgView = findViewById(R.id.v_bg)
        draggable = MyMovingDraggable()
        draggable?.setOnMyToucnListener(object : MyMovingDraggable.OnMyToucnListener {
            override fun onClickContent() {

            }

            override fun onClickRemove() {
                type = -1
                if(null != parent && parent is ViewGroup){
                    (parent as ViewGroup).removeView(this@PizhuView)
                }
            }

            override fun onScale(event: MotionEvent?) {
                event?.let {
                    setPullIconView(it)
                }
            }

        })
        draggable?.start(this)

    }
    var lp:ViewGroup.LayoutParams? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("MyMovingDraggable", "onInterceptTouchEvent")
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if(type == 0){
                return false
            }
        }
        return true
    }
    @SuppressLint("ClickableViewAccessibility")
    override  fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("MyMovingDraggable", "onTouchEvent5 type=="+type)

        if(type == 1){
            if(null != draggable){
                return draggable!!.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        draggable?.onDraw(canvas)

    }
    fun setCurrentTextColor(color:Int){
        et_text.setTextColor(ContextCompat.getColor(context,color))
    }

    /**
     *  0: 获取焦点状态，可输入文字
     *  1：初始状态，没有获取焦点，两个按钮都显示
     *  2: 只显示文字的状态
     */
    var type = 0
    fun setCurrentType(t:Int){
        type = t
        post {
            if(type == 0){
                et_text.setBackgroundResource(R.drawable.stroke_yellow)
                et_text.isEnabled = true
                et_text.isFocusable = true
                et_text.isFocusableInTouchMode = true
                et_text.requestFocus()
                iv_close.visibility = GONE
                iv_expend.visibility = GONE
            }else if(type == 1){
                et_text.clearFocus()
                et_text.setBackgroundResource(R.drawable.stroke_yellow)
                et_text.isEnabled = false
                et_text.isFocusable = false
                et_text.isFocusableInTouchMode = false
                iv_close.visibility = VISIBLE
                iv_expend.visibility = VISIBLE
            }else{
                et_text.isEnabled = false
                et_text.isFocusable = false
                et_text.isFocusableInTouchMode = false
                et_text.background = null
                iv_close.visibility = GONE
                iv_expend.visibility = GONE
            }
        }
    }


    private var oldRawX = 0f
    private var oldRawY = 0f

    private var oldSize = 0f

    private var px = 0f
    private var py:Float = 0f
    private var pw = 0f
    private var ph:Float = 0f

    private var bgOX = 0f
    private var bgOY = 0f

    @SuppressLint("ClickableViewAccessibility")
    fun setPullIconView(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                oldRawX = event.rawX
                oldRawY = event.rawY
                oldSize = et_text.textSize
                px = x
                py = y
                pw = width.toFloat()
                ph = height.toFloat()
                defaultAngle = rotation
                oldDist = spacing(oldRawX - pivotX, oldRawY - y - height * 0.5f)
                val bgPosition = IntArray(2)
                bgView?.getLocationOnScreen(bgPosition)
                val closePosition = IntArray(2)
                iv_close.getLocationOnScreen(closePosition)
                val pullPosition = IntArray(2)
                iv_expend.getLocationOnScreen(pullPosition)
                val dx = Math.abs(closePosition[0] - pullPosition[0]) * 0.5f
                val dy = Math.abs(closePosition[1] - pullPosition[1]) * 0.5f
                bgOX = if (bgPosition[0] < pullPosition[0]) {
                    bgPosition[0] + dx
                } else {
                    bgPosition[0] - dx
                }
                bgOY = if (bgPosition[1] < pullPosition[1]) {
                    bgPosition[1] + dy
                } else {
                    bgPosition[1] - dy
                }
            }
            MotionEvent.ACTION_MOVE -> {

                // 旋转
                rotation = angleBetweenLines(
                    oldRawX,
                    oldRawY,
                    event.rawX,
                    event.rawY,
                    bgOX,
                    bgOY
                ) + defaultAngle

                // 拉伸
                x = px + (pw - width) * 0.5f
                y = py + (ph - height) * 0.5f
                val newDist = spacing(event.rawX - bgOX, event.rawY - bgOY)
                scale = newDist / oldDist
                if (newDist > oldDist + 1) {
                    zoom(scale)
                    oldDist = newDist
                }
                if (newDist < oldDist - 1) {
                    zoom(scale)
                    oldDist = newDist
                }
            }
            MotionEvent.ACTION_UP -> {}
        }
    }

    private fun zoom(f: Float) {
        et_text.textSize = f.let { oldSize *= it; oldSize }
    }

    var scale = 0f
    var oldDist = 0f
    var defaultAngle = 0f

    /**
     * 计算两点之间的距离
     *
     * @return 两点之间的距离
     */
    private fun spacing(xs: Float, ys: Float): Float {
        return Math.sqrt((xs * xs + ys * ys).toDouble()).toFloat()
    }

    /**
     * 计算刚开始触摸的两个点构成的直线和滑动过程中两个点构成直线的角度
     *
     * @param fX  初始点一号x坐标
     * @param fY  初始点一号y坐标
     * @param nfX 终点一号x坐标
     * @param nfY 终点一号y坐标
     * @return 构成的角度值
     */
    private fun angleBetweenLines(
        fX: Float,
        fY: Float,
        nfX: Float,
        nfY: Float,
        cfX: Float,
        cfY: Float
    ): Float {
        val angle1 =
            Math.atan2((fY - cfY).toDouble(), (fX - cfX).toDouble()).toFloat()
        val angle2 =
            Math.atan2((nfY - cfY).toDouble(), (nfX - cfX).toDouble()).toFloat()
        return Math.toDegrees((angle2 - angle1).toDouble()).toFloat() % 360
    }
}