package com.jyc.jycdatepicker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentLinearLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017/9/13.
 */

public class DatePicker extends LinearLayout {

    public static final int TYPE_WEEK = 0;
    public static final int TYPE_MONTH = 1;

    private ImageView mIVLeft;
    private ImageView mIVRight;
    private TextView mTVTitle;

    private DatePickerContentContainer mRLContent;

    private DatePickerPanel mCurrentMonthDatePanel;
    private DatePickerPanel mCurrentWeekDatePanel;

    private int mType = TYPE_MONTH;

    public int getType(){
        return mType;
    }

    public void changeType(int type){
        if (mType != type){
            mType = type;
            if (mCurrentListener != null)
                mCurrentListener.typeChange(mType);
            changeMonthWeekByType();
        }
    }

    private OnClickListener mLeftClickListener= new OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar target = Calendar.getInstance();
            target.setTime(mCurrentDay.getTime());
            if (mType == TYPE_MONTH){
                target.set(Calendar.DAY_OF_MONTH, 1);
                target.add(Calendar.MONTH, -1);
            } else if (mType == TYPE_WEEK){
                target.add(Calendar.DATE, -7);
            }

            if (mCurrentListener != null)
                mCurrentListener.clickPre(target);

            changeDate(target);
        }
    };

    private OnClickListener mRightClickListener= new OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar target = Calendar.getInstance();
            target.setTime(mCurrentDay.getTime());

            if (mType == TYPE_MONTH){
                target.set(Calendar.DAY_OF_MONTH, 1);
                target.add(Calendar.MONTH, 1);
            } else if (mType == TYPE_WEEK){
                target.add(Calendar.DATE, 7);
            }

            if (mCurrentListener != null)
                mCurrentListener.clickAfter(target);

            changeDate(target);
        }
    };

    private Calendar mCurrentDay = Calendar.getInstance();

    private DatePickerClickListener mCurrentListener;

    public Calendar getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDay.getTime());
        return calendar;
    }

    public void setDatePickerCellClickListener(DatePickerClickListener listener){
        mCurrentListener = listener;
        bindListener();
    }

    private void bindListener(){
        if (mCurrentMonthDatePanel != null)
            mCurrentMonthDatePanel.setPickerListener(mCurrentListener);
        if (mCurrentWeekDatePanel != null)
            mCurrentWeekDatePanel.setPickerListener(mCurrentListener);
    }

    public DatePicker(Context context) {
        super(context);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseValue(context, attrs);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseValue(context, attrs);
        init(context);
    }

    private void parseValue(Context context, AttributeSet attrs){
        if (context != null && attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JYCDatePicker);
            mType = typedArray.getInt(R.styleable.JYCDatePicker_jyc_date_picker_show_type, TYPE_MONTH);
            typedArray.recycle();
        }
    }

    private void init(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);

        PercentLinearLayout rootView = (PercentLinearLayout) inflater.inflate(R.layout.jyc_date_picker, null);

        mIVLeft = $(rootView, R.id.jyc_date_picker_left_button);
        mIVRight = $(rootView, R.id.jyc_date_picker_right_button);
        mTVTitle = $(rootView, R.id.jyc_date_picker_title);

        mIVLeft.setOnClickListener(mLeftClickListener);
        mIVRight.setOnClickListener(mRightClickListener);

        mRLContent = $(rootView, R.id.jyc_date_picker_content);

        addView(rootView);

        initPanel(context);
        update();

        mRLContent.mGestureDetector = new GestureDetector(context, new GestureDetectorListener());

        mRLContent.mUpListener = new DatePickerContentContainer.OnUpActionListener() {
            @Override
            public void actionUp(MotionEvent event) {
                if (mRLContent.isScrolling){
                    int currentHeight = mRLContent.getMeasuredHeight();
                    boolean needChangeToMonth = currentHeight >= mCurrentMonthDatePanel.getMaxHeight() / 2;
                    ValueAnimator animator = ValueAnimator.ofInt(currentHeight,
                            needChangeToMonth ? mCurrentMonthDatePanel.getMaxHeight() : mCurrentMonthDatePanel.getRowHeight());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mRLContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) animation.getAnimatedValue()));
                        }
                    });

                    animator.setDuration(200).start();
                    changeType(needChangeToMonth ? TYPE_MONTH : TYPE_WEEK);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentMonthDatePanel.updateBG();
                        }
                    }, 200);

                    mRLContent.isScrolling = false;
                    if (mCurrentListener != null)
                        mCurrentListener.stopScroll();
                }

                if (mCurrentListener != null)
                    mCurrentListener.actionUp();
            }
        };

        this.post(new Runnable() {
            @Override
            public void run() {
                changeHeightByContent();
            }
        });
    }

    private <T> T $(View view, @IdRes int id){
        return (T)view.findViewById(id);
    }

    public void changeDate(Calendar calendar){
        if (mCurrentDay.compareTo(calendar) == 0)
            return;

        if (isSameMonth(mCurrentDay, calendar)){
            mCurrentMonthDatePanel.setCurrentDay(calendar);
        }else{
            initMonthPanel(getContext(), calendar);
        }

        if (isSameWeek(mCurrentDay, calendar)){
            mCurrentWeekDatePanel.setCurrentDay(calendar);
        }else{
            initWeekPanel(getContext(), calendar);
        }

        mCurrentDay = calendar;

        bindListener();

        update();

        this.post(new Runnable() {
            @Override
            public void run() {
                changeHeightByContent();
            }
        });
    }

    private void initMonthPanel(Context context, Calendar date){
        mCurrentMonthDatePanel = DatePickerPanel.getPanel(context, date, TYPE_MONTH);
    }

    private void initMonthPanel(Context context){
        initMonthPanel(context, mCurrentDay);
    }

    private void initWeekPanel(Context context, Calendar date){
        mCurrentWeekDatePanel = DatePickerPanel.getPanel(context, date, TYPE_WEEK);
    }

    private void initWeekPanel(Context context){
        initWeekPanel(context, mCurrentDay);
    }

    private void initPanel(Context context){
        initMonthPanel(context);
        initWeekPanel(context);
        bindListener();
    }

    private void updateTitle(){
        mTVTitle.setText(new SimpleDateFormat("yyyy年MM月", Locale.getDefault()).format(mCurrentDay.getTime()));
    }

    private void update(){
        updateTitle();

        mRLContent.removeAllViews();
        mRLContent.addView(mCurrentMonthDatePanel);
        mRLContent.addView(mCurrentWeekDatePanel);

        changeMonthWeekByType();
    }

    private void changeHeightByContent(){
        if (getType() == TYPE_MONTH)
            mRLContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mCurrentMonthDatePanel.getMaxHeight()));
        else
            mRLContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mCurrentMonthDatePanel.getRowHeight()));
    }

    private void changeMonthWeekByType(){
        mCurrentMonthDatePanel.setVisibility(mType == TYPE_MONTH ? View.VISIBLE : View.INVISIBLE);
        mCurrentWeekDatePanel.setVisibility(mType == TYPE_WEEK ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isSameMonth(Calendar left, Calendar right){
        return left.get(Calendar.YEAR) == right.get(Calendar.YEAR)
                && left.get(Calendar.MONTH) == right.get(Calendar.MONTH);
    }

    private boolean isSameWeek(Calendar left, Calendar right){
        return isSameMonth(left, right)
                && left.get(Calendar.WEEK_OF_MONTH) == right.get(Calendar.WEEK_OF_MONTH);
    }

    private class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener{

        public GestureDetectorListener(){}

        @Override
        public boolean onDown(MotionEvent e) {
            if (mCurrentListener != null){
                mCurrentListener.startScroll();
                mCurrentListener.actionDown();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mRLContent.isScrolling = true;

            int minHeight = mCurrentMonthDatePanel.getRowHeight();
            int maxHeight = mCurrentMonthDatePanel.getMaxHeight();
            int currentHeight = mRLContent.getMeasuredHeight();
            if (minHeight <= currentHeight && currentHeight <= maxHeight){
                currentHeight -= distanceY;

                if (currentHeight <= minHeight)
                    currentHeight = minHeight;
                if (currentHeight >= maxHeight)
                    currentHeight = maxHeight;

                mRLContent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, currentHeight));
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void clickPre(){
        mIVLeft.performClick();
    }

    public void clickAfter(){
        mIVRight.performClick();
    }

    public interface DatePickerClickListener{
        void cellClick(Calendar calendar);
        void clickPre(Calendar calendar);
        void clickAfter(Calendar calendar);
        void typeChange(int type);
        void startScroll();
        void stopScroll();
        void actionDown();
        void actionUp();
    }
}
