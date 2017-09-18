package com.jyc.jycdatepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/9/15.
 */

public class DatePickerContentContainer extends RelativeLayout {

    public boolean isScrolling = false;

    public GestureDetector mGestureDetector;

    public OnUpActionListener mUpListener;

    public DatePickerContentContainer(Context context) {
        super(context);
    }

    public DatePickerContentContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePickerContentContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((ev.getAction() == MotionEvent.ACTION_CANCEL
            || ev.getAction() == MotionEvent.ACTION_UP) && mUpListener != null)
            mUpListener.actionUp(ev);

        if (mGestureDetector != null)
            mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public interface OnUpActionListener{
        void actionUp(MotionEvent event);
    }
}
