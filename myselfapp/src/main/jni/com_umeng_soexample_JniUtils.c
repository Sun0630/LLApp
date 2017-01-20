#include "com_umeng_soexample_JniUtils.h"
/*
 * Class:     icom_example_myselfapp_JniUtils
 * Method:    getMyName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_umeng_soexample_JniUtils_getMyName
  (JNIEnv *env, jobject obj){
     return (*env)->NewStringUTF(env,"This just a test for Android Studio NDK JNI developer!");
  }
