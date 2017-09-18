package com.jyc.jycdatepicker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017/9/14.
 */

public class DatePickerPanel extends LinearLayout {

    private int mType = DatePicker.TYPE_MONTH;

    private Calendar mCurrent;

    private ArrayList<DatePickerCell> mCellList = new ArrayList<>();

    private int mRowHeight = 0;
    private int mRowCount = 0;

    public int getRowHeight() {
        return mRowHeight;
    }

    public int getRowCount() {
        return mRowCount;
    }

    public int getMaxHeight(){
        return getRowHeight() * getRowCount();
    }

    public DatePickerPanel(Context context, Calendar calendar, int type) {
        super(context);
        this.mType = type;
        this.mCurrent = calendar;
        init(context);
    }

    public void setCurrentDay(Calendar calendar){
        this.mCurrent = calendar;
        for (int i = 0; i < mCellList.size(); i ++){
            mCellList.get(i).setCurrentDay(calendar);
        }
    }

    private DatePicker.DatePickerClickListener mPickerListener;

    private OnClickListener mCellClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPickerListener != null)
                mPickerListener.cellClick((Calendar) v.getTag());
        }
    };

    public void setPickerListener(DatePicker.DatePickerClickListener listener){
        this.mPickerListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() != 0)
            this.mRowHeight = getChildAt(0).getMeasuredHeight();
    }

    private void init(Context context){
        setOrientation(VERTICAL);

        Calendar startDay = getStartDay();
        Calendar endDay = getEndDay();

        Calendar cursorDay = Calendar.getInstance();
        cursorDay.setTime(startDay.getTime());

        endDay.add(Calendar.DATE, 1);

        LinearLayout root = getRowLinearLayout(context);

        LinearLayout.LayoutParams cellLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        mCellList.clear();

        while (cursorDay.before(endDay)){
            Calendar displayDate = Calendar.getInstance();
            displayDate.setTime(cursorDay.getTime());
            DatePickerCell cell = DatePickerCell.getCell(15, 12, cellLayoutParams, displayDate, mCurrent, context);
            cell.setOnClickListener(mCellClickListener);

            Calendar tagCalendar = Calendar.getInstance();
            tagCalendar.setTime(cursorDay.getTime());
            cell.setTag(tagCalendar);

            root.addView(cell);
            mCellList.add(cell);

            cursorDay.add(Calendar.DATE, 1);

            if (root.getChildCount() == 7){
                addView(root);
                mRowCount ++;
                root = getRowLinearLayout(context);
            }

        }
    }

    private LinearLayout getRowLinearLayout(Context context){
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(HORIZONTAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return root;
    }

    private Calendar getStartDay(){
        Calendar result = Calendar.getInstance();
        result.setTime(mCurrent.getTime());

        if (mType == DatePicker.TYPE_MONTH)
            result.set(Calendar.DAY_OF_MONTH, 1);

        if (result.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            result.add(Calendar.DATE, -(result.get(Calendar.DAY_OF_WEEK) - 1));

        return result;
    }

    private Calendar getEndDay(){
        Calendar result = Calendar.getInstance();
        result.setTime(mCurrent.getTime());

        if (mType == DatePicker.TYPE_MONTH)
            result.set(Calendar.DAY_OF_MONTH, result.getActualMaximum(Calendar.DAY_OF_MONTH));

        if (result.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
            result.add(Calendar.DATE, 7 - result.get(Calendar.DAY_OF_WEEK));

        return result;
    }

    public static DatePickerPanel getPanel(Context context, Calendar calendar, int type){
        DatePickerPanel datePicker = new DatePickerPanel(context, calendar, type);
        datePicker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return datePicker;
    }
}
