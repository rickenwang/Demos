package com.demo.ipc;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


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
                startNewActivity();
            }
        });
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

    private void printAppInfos() {
        Log.i("MainActivity", this.getHideBasePackageName());
        Log.i("MainActivity", this.getPackageName());
        Log.i("MainActivity", "" + this.getEmbeddedId());

        Log.i("MainActivity", "myUid: " + android.os.Process.myUid());
        Log.i("MainActivity", "UserHandle: " + android.os.Process.myUserHandle().toString());
    }

    private String getHideBasePackageName() {

        Class clazz = getClass();
        try {
            Method m1 = clazz.getMethod("getBasePackageName");
            return (String) m1.invoke(this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private String getEmbeddedId() {

        Class clazz = Activity.class;
        try {
            Field m1 = clazz.getDeclaredField("mEmbeddedID");
            m1.setAccessible(true);
            return (String) m1.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "Unknown";
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
