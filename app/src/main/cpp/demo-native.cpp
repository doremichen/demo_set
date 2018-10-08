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

struct javadata_t {
    jclass class_demo;
    JNIEnv* env_demo;
    jfieldID fid_data;
    jmethodID mid_method;
} javadata;


static const char* const classPath = "com/adam/app/demoset/jnidemo/NativeUtils";

static
void sigImp(int sigId)
{
    LOGI("[%s] enter\n", __FUNCTION__);
//    LOGI("CallBack to java layer");
//    // CallBack to java layer
//    jstring strInfo_data = javadata.env_demo->NewStringUTF("data changed");
//    javadata.env_demo->SetObjectField(javadata.class_demo, javadata.fid_data, strInfo_data);
//
//    jstring strInfo_method = javadata.env_demo->NewStringUTF("Changed by native layer");
//    javadata.env_demo->CallVoidMethod(javadata.class_demo, javadata.mid_method, strInfo_method);
    LOGI("[%s] exit\n", __FUNCTION__);
}

static
void configTimer()
{
    LOGI("[%s] enter\n", __FUNCTION__);
    struct itimerval value;

    signal(SIGALRM, sigImp);

    value.it_value.tv_sec = 2; // After 3 sec it start alarm
    value.it_value.tv_usec = 100000;
    value.it_interval.tv_sec = 0; // Every 1 sec it execute Alarm body
    value.it_interval.tv_usec = 0;

    setitimer(ITIMER_REAL, &value, NULL);
}

static
jstring _sayHello(JNIEnv* env, jobject clazz)
{
    LOGI("[%s] enter\n", __FUNCTION__);
    // Start alarm to call back
    configTimer();

    LOGI("CallBack to java layer");
    // CallBack to java layer
    jstring strInfo_data = env->NewStringUTF("data changed");
    env->SetObjectField(clazz, javadata.fid_data, strInfo_data);
    env->DeleteLocalRef(strInfo_data);

    jstring strInfo_method = env->NewStringUTF("Changed by native layer");
    env->CallVoidMethod(clazz, javadata.mid_method, strInfo_method);
    env->DeleteLocalRef(strInfo_method);

    return env->NewStringUTF("This string is from JNI");
}


/**
 * Jni native method
 */
static const JNINativeMethod gMethods[] = {
        {"sayHello", "()Ljava/lang/String;", (void*)_sayHello},
};

static
int registerNative(JNIEnv* env)
{
    jclass clazz = env->FindClass(classPath);

    if (clazz == NULL) {
        LOGE("Can not find class");
        return -1;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods)/ sizeof(gMethods[0])) != JNI_OK) {
        LOGE("Can not register native method");
        return -1;
    }

//    javadata.class_demo = clazz;
//    javadata.env_demo = env;

    // Get java member id
    javadata.fid_data = env->GetFieldID(clazz, "dataFromNative", "Ljava/lang/String;");
    javadata.mid_method = env->GetMethodID(clazz, "notify", "(Ljava/lang/String;)V");


    return 0;
}


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
