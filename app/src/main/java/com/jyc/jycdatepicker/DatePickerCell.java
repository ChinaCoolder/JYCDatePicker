package com.jyc.jycdatepicker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/9/14.
 */

public class DatePickerCell extends RelativeLayout {

    private Calendar mDisplayDate;

    private Calendar mCurrentDay;

    private float mDayTextSize;
    private float mTraditionalDayTextSize;

    private ImageView mIVSameDayBg;
    private TextView mTVDay;
    private TextView mTVTraditionalDay;

    public void setDayTextSize(float mDayTextSize) {
        this.mDayTextSize = mDayTextSize;
    }

    public void setTraditionalDayTextSize(float mTraditionalDayTextSize) {
        this.mTraditionalDayTextSize = mTraditionalDayTextSize;
    }

    public void setDisplayDate(Calendar date){
        this.mDisplayDate = date;
    }

    public void setCurrentDay(Calendar calendar){
        this.mCurrentDay = calendar;
        changeByCurrentCalendar();
    }

    public DatePickerCell(Context context, Calendar day, float dayTextSize, float traditionalDayTextSize, Calendar currentDay) {
        super(context);
        setDayTextSize(dayTextSize);
        setTraditionalDayTextSize(traditionalDayTextSize);
        setDisplayDate(day);
        this.mCurrentDay = currentDay;
        init(context);
    }

    public DatePickerCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DatePickerCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context){

        RelativeLayout.LayoutParams layoutParamsIV = new RelativeLayout.LayoutParams(0, 0);
        layoutParamsIV.addRule(CENTER_IN_PARENT);
        mIVSameDayBg = new ImageView(getContext());
        mIVSameDayBg.setImageResource(isToday() && !isSameDay() ? R.drawable.jyc_date_picker_today_bg : R.drawable.jyc_date_picker_bg);
        mIVSameDayBg.setLayoutParams(layoutParamsIV);
        addView(mIVSameDayBg);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout textParent = new LinearLayout(context);
        textParent.setPadding(0, 10, 0, 10);
        textParent.setLayoutParams(layoutParams);
        textParent.setGravity(Gravity.CENTER_HORIZONTAL);
        textParent.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams dayLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTVDay = new TextView(context);
        mTVDay.setLayoutParams(dayLayoutParams);
        mTVDay.setTextSize(mDayTextSize);
        mTVDay.setText(mDisplayDate.get(Calendar.DAY_OF_MONTH) + "");
        mTVDay.setTextColor(isSameMonth() ? (isSameDay() ? Color.parseColor("#4079f5") : (isToday() ? Color.WHITE : Color.BLACK)) : Color.parseColor("#cccccc"));
        textParent.addView(mTVDay);

        LinearLayout.LayoutParams traditionalDayLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mTVTraditionalDay = new TextView(context);
        mTVTraditionalDay.setLayoutParams(traditionalDayLayoutParams);
        mTVTraditionalDay.setTextSize(mTraditionalDayTextSize);
        mTVTraditionalDay.setText(Lunar.getChinaDayString((new Lunar(mDisplayDate)).day));
        mTVTraditionalDay.setTextColor(Color.parseColor(isSameMonth() ?  (isSameDay() ? "#3975F5" : (isToday() ? "#ffffff" : "#999999")) : "#d9d9d9"));

        textParent.addView(mTVTraditionalDay);

        addView(textParent);


        if (isSameDay() || isToday())
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setSelectedBG();
                }
            }, 100);
    }

    private void changeByCurrentCalendar(){
        mTVDay.setTextColor(isSameMonth() ? (isSameDay() ? Color.parseColor("#4079f5") : (isToday() ? Color.WHITE : Color.BLACK)) : Color.parseColor("#cccccc"));
        mTVTraditionalDay.setTextColor(Color.parseColor(isSameMonth() ?  (isSameDay() ? "#3975F5" : (isToday() ? "#ffffff" : "#999999")) : "#d9d9d9"));

        if (isToday() || isSameDay()){
            mIVSameDayBg.setVisibility(View.VISIBLE);
            setSelectedBG();
            mIVSameDayBg.setImageResource(isToday() && !isSameDay() ? R.drawable.jyc_date_picker_today_bg : R.drawable.jyc_date_picker_bg);
        }else{
            mIVSameDayBg.setVisibility(View.INVISIBLE);
        }
    }

    private void setSelectedBG(){
        int width = getMeasuredWidth() <= getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
        layoutParams.addRule(CENTER_IN_PARENT);
        mIVSameDayBg.setLayoutParams(layoutParams);
    }

    private boolean isToday(){
        return mDisplayDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
                && mDisplayDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
                && mDisplayDate.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    private boolean isSameDay(){
        return isSameMonth() && mDisplayDate.get(Calendar.DAY_OF_MONTH) == mCurrentDay.get(Calendar.DAY_OF_MONTH);
    }

    private boolean isSameMonth(){
        return mDisplayDate.get(Calendar.YEAR) == mCurrentDay.get(Calendar.YEAR)
                && mDisplayDate.get(Calendar.MONTH) == mCurrentDay.get(Calendar.MONTH);
    }

    public static DatePickerCell getCell(float daySize, float traditionalDaySize, LinearLayout.LayoutParams layoutParams, Calendar day, Calendar currentDay, Context context){
        DatePickerCell datePickerCell = new DatePickerCell(context, day, daySize, traditionalDaySize, currentDay);
        datePickerCell.setLayoutParams(layoutParams);
        return datePickerCell;
    }
}
