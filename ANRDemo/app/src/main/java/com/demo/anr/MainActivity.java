package com.demo.anr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class MainActivity extends AppCompatActivity {

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final List<Activity> staticActivityHolder = new LinkedList<>();

    static {
        System.loadLibrary("memory-lib");
    }

    private boolean interceptJavaWorkerThreadException = true;

    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleUnCatchException();

        // 造成 activity 泄漏
        staticActivityHolder.add(this);

        findViewById(R.id.btn_click_anr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickANR();
            }
        });

        // tvContent = findViewById(R.id.tv_content);

        Log.e("MemoryDemo", "maxMemory is " + Runtime.getRuntime().maxMemory() / 1024.0 + "KB");
        Log.e("MemoryDemo", "freeMemory is " + Runtime.getRuntime().freeMemory() / 1024.0 + "KB");
        Log.e("MemoryDemo", "totalMemory is " + Runtime.getRuntime().totalMemory() / 1024.0 + "KB");

        Log.e("MemoryDemo", "maxMemory is " + MemoryUtils.maxJavaVirtualMemory(this) + "MB");

    }

    public void toastMessage(final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showContent(final String content) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                tvContent.setText(content);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void startNewActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void handleUnCatchException() {

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if ("main".equals(t.getName())) {

                }

                /// Log.e("ExceptionHandler", "person size is " + staticPersonHolder.size());

                if (!"main".equals(t.getName()) && interceptJavaWorkerThreadException) {
                    toastMessage("UncaughtException: " + e.getMessage());
                    return;
                }

                defaultHandler.uncaughtException(t, e);
            }
        });
    }

    // 内存泄漏
    private void clickANR() {

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
