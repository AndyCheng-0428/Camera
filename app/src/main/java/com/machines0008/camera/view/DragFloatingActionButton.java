package com.machines0008.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Project Name: Camera
 * Created By: user
 * Created On: 2022/5/18
 * Usage:
 **/
public class DragFloatingActionButton extends FloatingActionButton {
    private float downRawX, downRawY;
    private float dX, dY;
    private static final int CLICK_DRAG_TOLERANCE = 10 ;

    public DragFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downRawX = event.getRawX();
            downRawY = event.getRawY();
            dX = getX() - downRawX;
            dY = getY() - downRawY;
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            View viewParent = (View) getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();
            float newX = event.getRawX() + dX;
            newX = Math.max(0, newX);
            newX = Math.min(parentWidth - viewWidth, newX);
            float newY = event.getRawY() + dY;
            newY = Math.max(0, newY);
            newY = Math.min(parentHeight - viewHeight, newY);
            animate().x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            float upRawX = event.getRawX();
            float upRawY = event.getRawY();
            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;
            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) {
                return performClick();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}