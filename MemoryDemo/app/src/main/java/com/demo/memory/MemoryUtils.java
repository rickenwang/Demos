package com.demo.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.support.annotation.RequiresApi;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MemoryUtils {

    private static List<byte[]> memoryHolder = new LinkedList<>();

    // 最大的 Java 虚拟内存
    public static long currentMaxJavaVirtualMemory() {
        return Runtime.getRuntime().maxMemory();//根据是否largeHeap，等于limitMemory或largeMemory
    }

    public static long maxJavaVirtualMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getLargeMemoryClass();
    }

    // java 堆虚拟内存
    public static void applyJavaHeapVirtualMemory(int size) {
        byte[] memory = new byte[size];

        // 防止垃圾回收
        memoryHolder.add(memory);
    }

    // java 堆虚拟内存 + 映射等量的物理内存
    public static void applyJavaHeapPhysicalMemory(int size) {
        byte[] memory = new byte[size];
        new Random().nextBytes(memory);

        // 防止垃圾回收
        memoryHolder.add(memory);
    }

    public native static void applyNativeHeapVirtualMemory(long size);

    public native static void applyNativeHeapPhysicalMemory(long size);

    public static void get() {
        //
        Debug.getNativeHeapSize();
        Debug.getNativeHeapFreeSize();
        Debug.getNativeHeapAllocatedSize();

        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        // 当前进程的内存信息
        Debug.getMemoryInfo(memoryInfo);
        // memoryInfo.dalvikPss
        // memoryInfo.nativePss
        // memoryInfo.getMemoryStat("summary.java-heap");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getMemoryInfo(Context context) {

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        // 设置 largeHeap=true 时的 Java 堆内存限制
        long largeJavaHeapMemory = activityManager.getLargeMemoryClass();
        // 未设置 largeHeap=true 时的 Java 堆内存限制
        long normalJavaHeapMemory = activityManager.getMemoryClass();

        // ActivityManager.MemoryInfo 是整个系统的内存分配情况，
        // Debug.MemoryInfo
        ActivityManager.MemoryInfo amMemoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(amMemoryInfo);
        boolean lowMemory = amMemoryInfo.lowMemory; // 当前系统是否处于低内存状态
        long availMem = amMemoryInfo.availMem; // 当前系统可用内存，但不绝对，部分可能系统正在使用
        long totalMem = amMemoryInfo.totalMem; // 系统的全部内存
        long threshold = amMemoryInfo.threshold; // 系统开始杀后台服务和进程的低内存阈值

        // Debug.MemoryInfo 可以通过 pid 来获取，也尅直接通过 Debug.getMemoryInfo 来获取
        int pid = Process.myPid();
        Debug.MemoryInfo[] memoryInfoArr = activityManager.getProcessMemoryInfo(new int[]{pid});
        Debug.MemoryInfo memoryInfo = memoryInfoArr[0];
        // 当前进程的内存信息
        // Debug.getMemoryInfo(memoryInfo);
        long dalvikPss = memoryInfo.dalvikPss; // java 堆的 Pss
        long nativePss = memoryInfo.nativePss; // native 堆的 Pss
        // 对应于 dumpsys meminfo 输出的 App Summary 部分中的 Java Heap 字段
        String javaHeap = memoryInfo.getMemoryStat("summary.java-heap");
        String nativeHeap = memoryInfo.getMemoryStat("summary.native-heap");

        return 0;

    }

    public static void get3() {
        // Java 虚拟机当前总内存
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().freeMemory();
    }
}
