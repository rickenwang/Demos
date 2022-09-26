package com.demo.memory;

import java.util.Random;

/**
 * Created by jeremywang on 2022/9/6.
 */
public class Person {

    private byte[] data = new byte[1024 * 1024];

    public Person() {
        this(false);
    }

    public Person(boolean virtual) {
        if (virtual) {
            return;
        }

        // 非常重要 ！！！！
        // 如果这里不给 data 随机赋值，那么系统可能优化为只消耗
        // 虚拟内存，而实际的 PSS 基本无变化，所以在 Memory Profiler
        // 下将看不到内存变化
        Random random = new Random(System.currentTimeMillis());
        random.nextBytes(data);
    }

    public byte dump() {

        byte ans = 0;
        for (byte b: data) {
            ans += b;
        }
        return ans;
    }
}
