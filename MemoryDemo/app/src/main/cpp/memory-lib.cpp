//
// Created by 王康 on 2022/8/14.
//
#include <jni.h>
#include <string>
#include <unistd.h>
#include <sys/mman.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_demo_memory_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C"
JNIEXPORT void JNICALL
Java_com_demo_memory_MemoryUtils_applyNativeHeapVirtualMemory(JNIEnv* env,
                       jclass clazz,
                       jlong size) {
    // 私有、匿名虚拟内存
    void* block = mmap(
            NULL,
            size,
            PROT_READ | PROT_WRITE,
            MAP_PRIVATE | MAP_ANONYMOUS,
            0,
            0);
    memset(block, 1, 1);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_demo_memory_MemoryUtils_applyNativeHeapPhysicalMemory(JNIEnv *env, jclass clazz, jlong size) {
     void *block = malloc(size); // 在 native heap 中分配内存

    // 私有、匿名虚拟内存
//    void* block = mmap(
//            NULL,
//            size,
//            PROT_READ | PROT_WRITE,
//            MAP_PRIVATE | MAP_ANONYMOUS,
//            0,
//            0);
    memset(block, 1, size);
}