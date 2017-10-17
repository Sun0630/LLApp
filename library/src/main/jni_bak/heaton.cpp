//
// Created by DDL on 2016/5/23.
//

#include "heaton.h"/*
#include <jni.h>
#include <signal.h>
#include <stdlib.h>

static struct sigaction old_sa[NSIG];


void android_sigaction(int signal, siginfo_t *info, void *reserved)

{

    JniMethodInfo methodInfo;

    bool isHave = JniHelper::getStaticMethodInfo(methodInfo,

        "com.cdbwsoft.library",

        "onNativeCrashed",

        "()V");

    if(isHave){

        methodInfo.env->CallStaticVoidMethod(methodInfo.classID, methodInfo.methodID);

    }else{

        return;

    }

    old_sa[signal].sa_handler(signal);

}



void InitCrashReport()

{

    struct sigaction handler;

    memset(&handler, 0, sizeof(struct sigaction));



    handler.sa_sigaction = android_sigaction;

    handler.sa_flags = SA_RESETHAND;



#define CATCHSIG(X) sigaction(X, &handler, &old_sa[X])

    CATCHSIG(SIGILL);

    CATCHSIG(SIGABRT);

    CATCHSIG(SIGBUS);

    CATCHSIG(SIGFPE);

    CATCHSIG(SIGSEGV);

    CATCHSIG(SIGSTKFLT);

    CATCHSIG(SIGPIPE);

}*/