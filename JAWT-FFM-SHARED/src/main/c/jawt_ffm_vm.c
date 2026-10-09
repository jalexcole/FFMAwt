/*
 * Copyright (c) 2026, OpenJDK contributors.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 */

#include "jni.h"

static JavaVM *jawt_ffm_vm = NULL;

/*
 * Called by the AWT initialization code.
 */
void JAWT_FFM_SetJavaVM(JavaVM *vm)
{
    jawt_ffm_vm = vm;
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved)
{
    (void)reserved;
    JAWT_FFM_SetJavaVM(vm);
    return JNI_VERSION_1_8;
}

/*
 * Used by jawt_ffm_env.c.
 */
JavaVM *
JAWT_FFM_GetJavaVM(void)
{
    return jawt_ffm_vm;
}

JNIEnv *
JAWT_FFM_GetJNIEnv(void)
{
    JNIEnv *env = NULL;

    if (jawt_ffm_vm == NULL)
    {
        return NULL;
    }

#ifdef __cplusplus
    if (jawt_ffm_vm->GetEnv((void **)&env, JNI_VERSION_1_8) != JNI_OK)
#else
    if ((*jawt_ffm_vm)->GetEnv(jawt_ffm_vm, (void **)&env, JNI_VERSION_1_8) != JNI_OK)
#endif
    {
        return NULL;
    }

    return env;
}