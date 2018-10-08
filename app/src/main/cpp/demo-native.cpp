//
// Created by AdamChen on 2018/10/8.
//
#define LOG_TAG "JNI Demo"

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <assert.h>
#include "jni.h"
#include <android/log.h>

#define __DEBUG__

#ifdef __DEBUG__
#define LOGV(...) __android_log_print( ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__ )
#define LOGD(...) __android_log_print( ANDROID_LOG_DEBUG,  LOG_TAG, __VA_ARGS__ )
#define LOGI(...) __android_log_print( ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__ )
#define LOGW(...) __android_log_print( ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__ )
#define LOGE(...) __android_log_print( ANDROID_LOG_ERROR,  LOG_TAG, __VA_ARGS__ )
#else
#define LOGV(...)
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#endif

static const char* const classPath = "com/adam/app/demoset/jnidemo/NativeUtils";

static
jstring _sayHello(JNIEnv* env, jobject clazz) {
    return env->NewStringUTF("This string is from JNI");
}


/**
 * Jni native method
 */
static const JNINativeMethod gMethods[] = {
        {"sayHello", "()Ljava/lang/String;", (void*)_sayHello},
};

static int registerNative(JNIEnv* env) {
    jclass clazz = env->FindClass(classPath);

    if (clazz == NULL) {
        LOGE("Can not find class");
        return -1;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods)/ sizeof(gMethods[0])) != JNI_OK) {
        LOGE("Can not register native method");
        return -1;
    }

    return 0;
}


JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Can not get jni env");
        return -1;
    }

    // Get jclass with env->FindClass.
    // Register methods with env->RegisterNatives.
    if (registerNative(env) == -1) {
        LOGE("Can not register native method in jni_onload");
        return -1;
    }

    return JNI_VERSION_1_6;
}
