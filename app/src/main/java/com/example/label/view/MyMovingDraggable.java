package com.example.label.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.label.R;

/**
 * 创建时间：2023/2/13
 * 编写人： 陈陈陈
 * 功能描述：
 */
public class MyMovingDraggable extends MyBaseDraggable{

    /** 触摸移动标记 */
    private boolean mMoveTouch;

    private float mDownX;
    private float mDownY;

    private float mViewX;
    private float mViewY;

    private int dis = 0;
    private Rect removeRect;
    private Rect scaleRect;
    float mDensity;
    private Paint mRectPaint;
    @Override
    public void start(View view) {
        super.start(view);
        mDensity = view.getContext().getResources().getDisplayMetrics().density;
        mRectPaint = new Paint();
        mRectPaint.setColor(ContextCompat.getColor(view.getContext(), R.color.color_f7d_33));
        mRectPaint.setStyle(Paint.Style.FILL);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setDither(true);
        initRect();
    }

    private void initRect() {
        mView.post(() -> {
            dis = (int) (mDensity * 30f);
            removeRect = new Rect(0,0, dis,dis);
            scaleRect = new Rect(mView.getWidth() - dis,mView.getHeight() - dis,mView.getWidth(),mView.getHeight());
        });
    }

    public void onDraw(Canvas canvas){
//        if(null != removeRect){
//            canvas.drawRect(removeRect,mRectPaint);
//        }
//        if(null != scaleRect){
//            canvas.drawRect(scaleRect,mRectPaint);
//        }
    }
    /**
     * 0：拖动
     * 1：删除
     * 2：缩小放大
     */
    private int touchType = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                mViewX = mView.getX();
                mViewY = mView.getY();

                if(removeRect.contains((int) event.getX(), (int) event.getY())){
                    touchType = 1;
                }else if(scaleRect.contains((int) event.getX(), (int) event.getY())){
                    touchType = 2;
                    if(null != onMyToucnListener){
                        onMyToucnListener.onScale(event);
                    }
                }else{
                    touchType = 0;
                }
                Log.d("MyMovingDraggable", "touchType =="+touchType);
                mMoveTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX() ;
                float moveY = event.getRawY() ;
                if (!mMoveTouch && isTouchMove(mDownX, moveX, mDownY, moveY)) {
                    // 如果用户移动了手指，那么就拦截本次触摸事件，从而不让点击事件生效
                    mMoveTouch = true;
                }

                // 更新移动的位置
                if(touchType == 0){
                    updateLocation(moveX - mDownX+mViewX, moveY - mDownY+mViewY);
                }else if(touchType == 2){
                    if(null != onMyToucnListener){
                        onMyToucnListener.onScale(event);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
                if(!mMoveTouch){
                    if(1 == touchType){
                        if(null != onMyToucnListener){
                            onMyToucnListener.onClickRemove();
                        }

                    }else if(0 == touchType){
                        if(null != onMyToucnListener){
                            onMyToucnListener.onClickContent();
                        }
                    }

                }
                if(touchType == 2){
                    if(null != onMyToucnListener){
                        onMyToucnListener.onScale(event);
                    }
                }
                touchType = -1;
                initRect();
                mView.postInvalidate();
            default:
                break;
        }
        return true;
    }

    private OnMyToucnListener onMyToucnListener;
    public void setOnMyToucnListener(OnMyToucnListener listener){
        onMyToucnListener = listener;
    }
    public interface OnMyToucnListener{
        void onClickContent();
        void onClickRemove();
        void onScale(MotionEvent event);
    }
}

