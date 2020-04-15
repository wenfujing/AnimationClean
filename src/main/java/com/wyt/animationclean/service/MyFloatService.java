package com.wyt.animationclean.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.wyt.animationclean.activity.FloatViewManager;


/**
 * @description 创建 service 方便管理悬浮小火箭 、悬浮窗口
 * @date: 2020/4/14
 * @author: a */
public class MyFloatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //用来开启FloatViewManager
        FloatViewManager manager= FloatViewManager.getInstance(this);
        manager.showFloatCircleView();
        super.onCreate();
    }

}
