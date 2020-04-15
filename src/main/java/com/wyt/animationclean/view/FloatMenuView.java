package com.wyt.animationclean.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.wyt.animationclean.R;
import com.wyt.animationclean.activity.FloatViewManager;

/**
 * @description 悬浮窗口
 * @date: 2020/4/14
 * @author: a */
public class FloatMenuView extends LinearLayout {

    private TranslateAnimation translateAnimation;

    public FloatMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view =View.inflate(getContext(), R.layout.float_menuview,null);
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.ll);
        translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        linearLayout.setAnimation(translateAnimation);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatViewManager manager= FloatViewManager.getInstance(getContext());
                manager.hideFloatMenuView();  //隐藏悬浮窗
                manager.showFloatCircleView(); //打开 小火箭

                return false;
            }
        });
        addView(view);

    }

    public FloatMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMenuView(Context context) {
        this(context, null);
    }
    public void startAnimation(){
        translateAnimation.start();

    }
}
