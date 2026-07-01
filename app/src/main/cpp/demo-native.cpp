/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#define LOG_TAG "JNI_Demo_Native"

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <assert.h>
#include "jni.h"
#include <android/log.h>
#include <signal.h>
#include <sys/time.h>
#include <sys/system_properties.h>
#include <cstdio>

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

/**
 * Structure to cache Java class and member IDs for performance.
 */
struct javadata_t {
    jclass class_demo;
    jfieldID fid_Objdata;
    jmethodID mid_Objmethod;
    jfieldID fid_Clazzdata;
    jmethodID mid_Clazzmethod;
} javadata;

// Target Java class path
static const char *const classPath = "com/adam/app/demoset/jnidemo/NativeUtils";

/**
 * Returns a greeting string to the Java layer.
 */
static jstring _sayHello(JNIEnv *env, jobject thiz) {
    LOGI("[%s] Execution started", __FUNCTION__);
    return env->NewStringUTF("Hello from the C++ Native Layer!");
}

/**
 * Triggers an instance-level callback by updating a field and calling a method.
 */
static void _objectCallBack(JNIEnv *env, jobject thiz) {
    LOGI("[%s] Triggering instance callback", __FUNCTION__);

    // Update Java instance field 'mDataFromNative'
    jstring strInfo_data = env->NewStringUTF("data changed");
    env->SetObjectField(thiz, javadata.fid_Objdata, strInfo_data);
    env->DeleteLocalRef(strInfo_data);

    // Invoke Java instance method 'notifyObj'
    jstring strInfo_method = env->NewStringUTF("Instance data updated by C++");
    env->CallVoidMethod(thiz, javadata.mid_Objmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

/**
 * Triggers a class-level (static) callback.
 */
static void _classCallBack(JNIEnv *env, jclass thiz) {
    LOGI("[%s] Triggering static callback", __FUNCTION__);

    // Update Java static field 'sDataFromNative'
    env->SetStaticBooleanField(javadata.class_demo, javadata.fid_Clazzdata, true);

    // Invoke Java static method 'notifyClazz'
    jstring strInfo_method = env->NewStringUTF("Static data updated by C++");
    env->CallStaticVoidMethod(javadata.class_demo, javadata.mid_Clazzmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

/**
 * Resets instance-level data in the Java layer.
 */
static void _clearObjData(JNIEnv *env, jobject thiz) {
    LOGI("[%s] Clearing instance data", __FUNCTION__);
    jstring strInfo_data = env->NewStringUTF("unChange");
    env->SetObjectField(thiz, javadata.fid_Objdata, strInfo_data);
    env->DeleteLocalRef(strInfo_data);

    jstring strInfo_method = env->NewStringUTF("Instance data reset by C++");
    env->CallVoidMethod(thiz, javadata.mid_Objmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

/**
 * Resets static-level data in the Java layer.
 */
static void _clearClazzData(JNIEnv *env, jclass thiz) {
    LOGI("[%s] Clearing static data", __FUNCTION__);
    env->SetStaticBooleanField(javadata.class_demo, javadata.fid_Clazzdata, false);

    jstring strInfo_method = env->NewStringUTF("Static data reset by C++");
    env->CallStaticVoidMethod(javadata.class_demo, javadata.mid_Clazzmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

/**
 * Performs a simple addition in native code.
 */
static jint _calculate(JNIEnv *env, jobject thiz, jint a, jint b) {
    LOGI("[%s] Calculating: %d + %d", __FUNCTION__, a, b);
    return a + b;
}

/**
 * Retrieves CPU ABI information from the system properties.
 */
static jstring _getSystemInfo(JNIEnv *env, jobject thiz) {
    char abi[PROP_VALUE_MAX];
    __system_property_get("ro.product.cpu.abi", abi);
    char info[128];
    sprintf(info, "Native ABI: %s", abi);
    return env->NewStringUTF(info);
}

/**
 * JNI Native Method Mapping
 */
static const JNINativeMethod gMethods[] = {
        {"sayHello", "()Ljava/lang/String;", (void *) _sayHello},
        {"objectCallBack", "()V", (void *) _objectCallBack},
        {"classCallBack", "()V", (void *) _classCallBack},
        {"clearObjData", "()V", (void *) _clearObjData},
        {"clearClazzData", "()V", (void *) _clearClazzData},
        {"calculate", "(II)I", (void *) _calculate},
        {"getSystemInfo", "()Ljava/lang/String;", (void *) _getSystemInfo},
};

/**
 * Registers native methods and initializes field/method IDs.
 */
static int registerNative(JNIEnv *env) {
    LOGI("[%s] Starting registration", __FUNCTION__);
    jclass clazz = env->FindClass(classPath);

    if (clazz == NULL) {
        LOGE("Could not find class: %s", classPath);
        return -1;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) != JNI_OK) {
        LOGE("Failed to register native methods");
        return -1;
    }

    // Cache Global Reference and Member IDs
    javadata.class_demo = (jclass) env->NewGlobalRef(clazz);
    javadata.fid_Objdata = env->GetFieldID(clazz, "mDataFromNative", "Ljava/lang/String;");
    javadata.mid_Objmethod = env->GetMethodID(clazz, "notifyObj", "(Ljava/lang/String;)V");
    javadata.fid_Clazzdata = env->GetStaticFieldID(clazz, "sDataFromNative", "Z");
    javadata.mid_Clazzmethod = env->GetStaticMethodID(clazz, "notifyClazz", "(Ljava/lang/String;)V");

    LOGI("[%s] Registration complete", __FUNCTION__);
    return 0;
}

/**
 * JNI Library entry point.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Failed to get JNI environment");
        return -1;
    }

    if (registerNative(env) == -1) {
        LOGE("Native registration failed");
        return -1;
    }

    return JNI_VERSION_1_6;
}
