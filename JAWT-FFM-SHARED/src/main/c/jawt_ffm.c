/*
 * Copyright (c) 2026, OpenJDK contributors.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 */

#include <jni.h>
#include <jawt.h>
#include <jawt_md.h>
#include <string.h>

#include "jawt_ffm.h"
#include "jawt_ffm_vm.h"

/*
 * FFM-facing JAWT initialization.
 */
int32_t
JAWT_GetAWT_FFM(JAWT_FFM *ffm)
{
    JNIEnv *env;
    JAWT jawt;

    if (ffm == NULL)
    {
        return 0;
    }

    memset(&jawt, 0, sizeof(jawt));

    env = JAWT_FFM_GetJNIEnv();

    if (env == NULL)
    {
        return 0;
    }

    /*
     * Request the same JAWT version requested by the FFM caller.
     */
    jawt.version = (jint)ffm->version;

    if (!JAWT_GetAWT(env, &jawt))
    {
        return 0;
    }

    ffm->GetDrawingSurface = (JAWT_FFM_GetDrawingSurface)jawt.GetDrawingSurface;
    ffm->FreeDrawingSurface = (JAWT_FFM_FreeDrawingSurface)jawt.FreeDrawingSurface;
    ffm->Lock = (JAWT_FFM_LockAWT)jawt.Lock;
    ffm->Unlock = (JAWT_FFM_LockAWT)jawt.Unlock;
    ffm->GetComponent = (JAWT_FFM_GetComponent)jawt.GetComponent;
    ffm->CreateEmbeddedFrame = (JAWT_FFM_CreateEmbeddedFrame)jawt.CreateEmbeddedFrame;
    ffm->SetBounds = (JAWT_FFM_SetBounds)jawt.SetBounds;
    ffm->SynthesizeWindowActivation =
        (JAWT_FFM_SynthesizeWindowActivation)jawt.SynthesizeWindowActivation;

    return 1;
}

