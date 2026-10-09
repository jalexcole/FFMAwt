#ifndef _JAVASOFT_JAWT_FFM_VM_H_
#define _JAVASOFT_JAWT_FFM_VM_H_

#include "jni.h"

#ifdef __cplusplus
extern "C"
{
#endif

    void JAWT_FFM_SetJavaVM(JavaVM *vm);

    JavaVM *JAWT_FFM_GetJavaVM(void);

    JNIEnv *JAWT_FFM_GetJNIEnv(void);

#ifdef __cplusplus
}
#endif

#endif /* !_JAVASOFT_JAWT_FFM_VM_H_ */