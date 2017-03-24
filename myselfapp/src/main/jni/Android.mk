LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := myselfapp
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
    H:\heaton\Git\LLApp\myselfapp\src\main\jni\com_umeng_soexample_Jni.c \
	H:\heaton\Git\LLApp\myselfapp\src\main\jni\com_umeng_soexample_JniUtils.c \
	H:\heaton\Git\LLApp\myselfapp\src\main\jni\empty.c \

LOCAL_C_INCLUDES += H:\heaton\Git\LLApp\myselfapp\src\main\jni
LOCAL_C_INCLUDES += H:\heaton\Git\LLApp\myselfapp\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
