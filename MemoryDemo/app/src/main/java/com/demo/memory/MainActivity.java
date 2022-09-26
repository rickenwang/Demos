package com.demo.memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    // 静态变量是 GC root，可能会导致内存泄漏
    private static final List<Person> staticPersonHolder = new LinkedList<>();

    private static final List<Activity> staticActivityHolder = new LinkedList<>();

    public Person person = new Person();

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

        Log.e("MemoryDemo", stringFromJNI());

        // 造成 activity 泄漏
        staticActivityHolder.add(this);

        // 多次启动 MainActivity，返回后，所有的 MainActivity 都不会消失
        findViewById(R.id.btn_activity_leak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity();
            }
        });

        findViewById(R.id.btn_memory_leak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryLeak();
            }
        });

        findViewById(R.id.btn_memory_shack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryShack();
            }
        });

        findViewById(R.id.btn_java_heap_virtual_memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testJavaHeapVirtualMemory();
            }
        });

        findViewById(R.id.btn_java_heap_physical_memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testJavaHeapPhysicalMemory();
            }
        });

        findViewById(R.id.btn_native_heap_virtual_memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNativeHeapVirtualMemory();
            }
        });

        findViewById(R.id.btn_native_heap_physical_memory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testNativeHeapPhysicalMemory();
            }
        });

        tvContent = findViewById(R.id.tv_content);

        Log.e("MemoryDemo", "maxMemory is " + Runtime.getRuntime().maxMemory() / 1024.0 + "KB");
        Log.e("MemoryDemo", "freeMemory is " + Runtime.getRuntime().freeMemory() / 1024.0 + "KB");
        Log.e("MemoryDemo", "totalMemory is " + Runtime.getRuntime().totalMemory() / 1024.0 + "KB");

        Log.e("MemoryDemo", "maxMemory is " + MemoryUtils.maxJavaVirtualMemory(this) + "MB");

    }

    public void toastMessage(final String message) {
        mainHandler.post(
                // 这里理论上也可能造成内存泄漏，因此需要移除所有的 pending 消息
                // 移除所有的 pending 消息
                // mainHandler.removeCallbacksAndMessages(null);
                new Runnable() {
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
        Thread.setDefaultUncaughtExceptionHandler(
                // 这个类持有 MainActivity 对象，因此会造成 MainActivity 泄漏
                new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if ("main".equals(t.getName())) {

                }

                Log.e("ExceptionHandler", "person size is " + staticPersonHolder.size());

                if (!"main".equals(t.getName()) && interceptJavaWorkerThreadException) {
                    toastMessage("UncaughtException: " + e.getMessage());
                    return;
                }


                defaultHandler.uncaughtException(t, e);
            }
        });
    }

    // 内存泄漏
    private void memoryLeak() {

        final int size = 10; // MB
        final int interval = 1000; // ms

        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < size; i++) {
                    staticPersonHolder.add(new Person());
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "MemoryLeak").start();
    }

    // 内存抖动
    private void memoryShack() {

        final int size = 200; // MB
        final int interval = 500; // ms

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < size; i++) {
                    System.out.println(new Person().dump());
                    try {
                        Thread.sleep(interval);
                        // System.gc();
                        // Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "MemoryShack").start();
    }


    private void testJavaHeapPhysicalMemory() {
        final AtomicInteger counter = new AtomicInteger(0);
        testIncreaseMemoryStepByStep("JavaHeapPhysicalMemory",
                50,
                Integer.MAX_VALUE,
                new MemoryCallback() {
            @Override
            public void callback() {
                MemoryUtils.applyJavaHeapPhysicalMemory(1024 * 1024);
                showContent("JavaHeapPhysicalMemory: " + counter.addAndGet(1) + "M");

            }
        });
    }

    private void testJavaHeapVirtualMemory() {
        final AtomicInteger counter = new AtomicInteger(0);
        testIncreaseMemoryStepByStep("JavaHeapVirtualMemory",
                50,
                Integer.MAX_VALUE,
                new MemoryCallback() {
            @Override
            public void callback() {
                MemoryUtils.applyJavaHeapVirtualMemory(1024 * 1024);
                showContent("JavaHeapVirtualMemory: " + counter.addAndGet(1) + "M");

            }
        });
    }

    private void testNativeHeapVirtualMemory() {
        final AtomicInteger counter = new AtomicInteger(0);
        testIncreaseMemoryStepByStep("NativeHeapVirtualMemory",
                50,
                500,
                new MemoryCallback() {
            @Override
            public void callback() {
                int size = 1024 * 1024 * 1024;
                MemoryUtils.applyNativeHeapVirtualMemory(size);
                showContent("NativeHeapVirtualMemory: " + counter.addAndGet(1) + "G");
            }
        });
    }

    private void testNativeHeapPhysicalMemory() {
        final AtomicInteger counter = new AtomicInteger(0);
        testIncreaseMemoryStepByStep("NativeHeapPhysicalMemory",
                50,
                1200,
                new MemoryCallback() {
            @Override
            public void callback() {
                MemoryUtils.applyNativeHeapPhysicalMemory(1024 * 1024);
                showContent("NativeHeapPhysicalMemory: " + counter.addAndGet(1) + "M");
            }
        });
    }

    private void testIncreaseMemoryStepByStep(String threadName,
                                              final int interval,
                                              final int step,
                                              final MemoryCallback apply) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < step; i++) {
                    // MemoryUtils.applyJavaHeapPhysicalMemory(size);
                    apply.callback();;
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, threadName).start();
    }

    /**
     * A native method that is implemented by the 'ndk' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
