package com.demo.crash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


/**
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class MainActivity extends AppCompatActivity {

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setDefaultUnCatchExceptionHandler();

        findViewById(R.id.javaCrashInUIThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                javaCrashInUIThread();
            }
        });

        findViewById(R.id.javaCrashInWorkerThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                javaCrashInWorkerThread();
            }
        });

        findViewById(R.id.nativeCrash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeCrash();
            }
        });
    }

    private void setDefaultUnCatchExceptionHandler() {
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.i("CrashDemo", "thread is " + t + ", throwable is " + t);
                Log.i("CrashDemo", "currentThread " + Thread.currentThread());

                // 主线程退出后，app 不再响应消息，必须杀死进程
                if ("main".equals(t.getName())) {
                    defaultHandler.uncaughtException(t, e);
                }
            }
        });
    }

    private void javaCrashInWorkerThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                javaCrash();
            }
        }, "JavaCrash").start();
    }

    private void javaCrashInUIThread() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                javaCrash();
            }
        });
    }

    private void javaCrash() {
        int i = 0;
        int j = 1;
        int k = j/i;
    }

    // 内存抖动
    private void nativeCrash() {

    }

}
