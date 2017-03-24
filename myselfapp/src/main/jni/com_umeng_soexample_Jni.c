#include <jni.h>
#include "com_umeng_soexample_JNI.h"
/**
 * jint:返回值
 * Java_全类名_方法名
 *
 */
jint Java_com_umeng_soexample_Jni_add
        (JNIEnv *env, jobject jobj, jint ji, jint jj){


    int result = ji + jj;
    return result;
};