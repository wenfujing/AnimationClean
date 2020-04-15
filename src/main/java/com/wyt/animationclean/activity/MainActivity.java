package com.wyt.animationclean.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.wyt.animationclean.R;
import com.wyt.animationclean.service.MyFloatService;

/**
 * @description 入口
 * @date: 2020/4/14
 * @author: a */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
    }

    public void startService(View view){
        Intent intent=new Intent(this, MyFloatService.class);
        startService(intent);
        finish();
    }
}
