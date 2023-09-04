package com.example.label.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 创建时间：2023/2/13
 * 编写人： 陈陈陈
 * 功能描述：
 */
public abstract class MyBaseDraggable{

    protected View mView;
    public void start(View view) {
        mView = view;
    }

    protected void updateLocation(float endX, float endY){
//        Log.d("MyMovingDraggable", "endX =="+endX+"/endY=="+endY);

        mView.animate()
                .x(endX)
                .y(endY)
                .setDuration(0)
                .start();
    }
    public abstract boolean onTouchEvent(MotionEvent event);

    /**
     * 判断用户是否移动了，判断标准以下：
     * 根据手指按下和抬起时的坐标进行判断，不能根据有没有 move 事件来判断
     * 因为在有些机型上面，就算用户没有手指没有移动也会产生 move 事件
     *
     * @param downX         手指按下时的 x 坐标
     * @param upX           手指抬起时的 x 坐标
     * @param downY         手指按下时的 y 坐标
     * @param upY           手指抬起时的 y 坐标
     */
    protected boolean isTouchMove(float downX, float upX, float downY, float upY) {
        float minTouchSlop = getMinTouchDistance();
        return Math.abs(downX - upX) >= minTouchSlop || Math.abs(downY - upY) >= minTouchSlop;
    }

    /**
     * 获取最小触摸距离
     */
    protected float getMinTouchDistance() {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                Resources.getSystem().getDisplayMetrics());
    }

}
