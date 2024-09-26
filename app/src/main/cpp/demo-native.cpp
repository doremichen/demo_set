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
#include <signal.h>
#include <sys/time.h>

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
 * java data context
 */
struct javadata_t {
    jclass class_demo;
    jfieldID fid_Objdata;
    jmethodID mid_Objmethod;
    jfieldID fid_Clazzdata;
    jmethodID mid_Clazzmethod;
} javadata;

// The java class path
static const char* const classPath = "com/adam/app/demoset/jnidemo/NativeUtils";

/**==============================================================================
 *
 * As the following method are the native method implementation.
 *
 */
static
jstring _sayHello(JNIEnv* env, jobject thiz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    return env->NewStringUTF("This string is from JNI");
}

static
void _objectCallBack(JNIEnv* env, jobject thiz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    LOGI("CallBack to java layer");
    // CallBack to java layer
    jstring strInfo_data = env->NewStringUTF("data changed");
    env->SetObjectField(thiz, javadata.fid_Objdata, strInfo_data);
    env->DeleteLocalRef(strInfo_data);

    jstring strInfo_method = env->NewStringUTF("Changed by native layer");
    env->CallVoidMethod(thiz, javadata.mid_Objmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

static
void _classCallBack(JNIEnv* env, jobject thiz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    LOGI("CallBack to java layer");
    env->SetStaticBooleanField(javadata.class_demo, javadata.fid_Clazzdata, true);
    jstring strInfo_method = env->NewStringUTF("Changed by native layer");
    env->CallStaticVoidMethod(javadata.class_demo, javadata.mid_Clazzmethod, strInfo_method);
    env->DeleteLocalRef(strInfo_method);
}

static
void _clearObjData(JNIEnv* env, jobject thiz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    jstring strInfo_data = env->NewStringUTF("unChange");
    env->SetObjectField(thiz, javadata.fid_Objdata, strInfo_data);
    env->DeleteLocalRef(strInfo_data);
}

static
void _clearClazzData(JNIEnv* env, jobject thiz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    env->SetStaticBooleanField(javadata.class_demo, javadata.fid_Clazzdata, false);
}
/**
 * Jni native method
 */
static const JNINativeMethod gMethods[] = {
        {"sayHello", "()Ljava/lang/String;", (void*)_sayHello},
        {"objectCallBack", "()V", (void*)_objectCallBack},
        {"classCallBack", "()V", (void*)_classCallBack},
        {"clearObjData", "()V", (void*)_clearObjData},
        {"clearClazzData", "()V", (void*)_clearClazzData},
};

static
int registerNative(JNIEnv* env)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    jclass clazz = env->FindClass(classPath);

    if (clazz == NULL) {
        LOGE("Can not find class");
        return -1;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods)/ sizeof(gMethods[0])) != JNI_OK) {
        LOGE("Can not register native method");
        return -1;
    }

    // Get global reference
    javadata.class_demo = (jclass) env->NewGlobalRef(clazz);

    // Get java object member id
    javadata.fid_Objdata = env->GetFieldID(clazz, "mDataFromNative", "Ljava/lang/String;");
    javadata.mid_Objmethod = env->GetMethodID(clazz, "notifyObj", "(Ljava/lang/String;)V");

    // Get java class member id
    javadata.fid_Clazzdata = env->GetStaticFieldID(clazz, "sDataFromNative", "Z");
    javadata.mid_Clazzmethod = env->GetStaticMethodID(clazz, "notifyClazz", "(Ljava/lang/String;)V");

    LOGI("[%s] exit\n", __FUNCTION__);
    return 0;
}

/**
 * The load function of jni
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
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
