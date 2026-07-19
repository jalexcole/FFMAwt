#include "org_java_desktop_ffmawt_FFMCanvas.h"

#include <stdint.h>

#import <Cocoa/Cocoa.h>
#import <OpenGL/OpenGL.h>
#import <OpenGL/gl.h>

#include "jawt.h"
#include "jawt_md.h"

typedef struct {
    NSOpenGLContext *context;
    NSView *view;
} FFMNativeGlContext;

static jlong memory_segment_address(JNIEnv *env, jobject segment);

static void *segment_pointer(JNIEnv *env, jobject segment)
{
    return (void *)(intptr_t)memory_segment_address(env, segment);
}

static NSView *surface_to_view(void *surface_ptr)
{
    id surface_obj;
    id maybe_view;

    if (surface_ptr == NULL)
    {
        return nil;
    }

    surface_obj = (__bridge id)surface_ptr;
    if ([surface_obj isKindOfClass:[NSView class]])
    {
        return (NSView *)surface_obj;
    }

    if ([surface_obj respondsToSelector:@selector(view)])
    {
        @try
        {
            maybe_view = [surface_obj valueForKey:@"view"];
            if ([maybe_view isKindOfClass:[NSView class]])
            {
                return (NSView *)maybe_view;
            }
        }
        @catch (NSException *exception)
        {
            (void)exception;
        }
    }

    return nil;
}

static FFMNativeGlContext *as_gl_context(jlong context_handle)
{
    return (FFMNativeGlContext *)(intptr_t)context_handle;
}

static jlong memory_segment_address(JNIEnv *env, jobject segment)
{
    jclass segment_class;
    jmethodID address_method;

    if (segment == NULL)
    {
        return 0;
    }

    segment_class = (*env)->GetObjectClass(env, segment);
    if (segment_class == NULL)
    {
        return 0;
    }

    address_method = (*env)->GetMethodID(env, segment_class, "address", "()J");
    if (address_method == NULL)
    {
        return 0;
    }

    return (*env)->CallLongMethod(env, segment, address_method);
}

inline static void write_pointer_out(JNIEnv *env, jobject out_segment, void *value)
{
    jlong out_address;
    void **out_location;

    out_address = memory_segment_address(env, out_segment);
    if (out_address == 0)
    {
        return;
    }

    out_location = (void **)(intptr_t)out_address;
    *out_location = value;
}

inline static void draw_debug_text(JNIEnv *env, jobject graphics, const char *text)
{
    jclass graphics_class;
    jmethodID draw_string_method;
    jstring jtext;

    if (graphics == NULL || text == NULL)
    {
        return;
    }

    graphics_class = (*env)->FindClass(env, "java/awt/Graphics");
    if (graphics_class == NULL)
    {
        return;
    }

    draw_string_method = (*env)->GetMethodID(env, graphics_class, "drawString", "(Ljava/lang/String;II)V");
    if (draw_string_method == NULL)
    {
        return;
    }

    jtext = (*env)->NewStringUTF(env, text);
    if (jtext == NULL)
    {
        return;
    }

    (*env)->CallVoidMethod(env, graphics, draw_string_method, jtext, 12, 22);
}

JNIEXPORT void JNICALL Java_org_java_desktop_ffmawt_FFMCanvas_nativePaint(
    JNIEnv *env,
    jobject thisObj,
    jobject graphics,
    jobject display,
    jobject surface)
{
    JAWT awt;
    JAWT_DrawingSurface *drawing_surface;
    JAWT_DrawingSurfaceInfo *dsi;
    jint lock;
    void *native_surface;
    void *native_display;

    native_surface = NULL;
    native_display = NULL;

    awt.version = JAWT_VERSION_1_7;
    if (JAWT_GetAWT(env, &awt) == JNI_FALSE)
    {
        draw_debug_text(env, graphics, "JAWT init failed");
        return;
    }

    drawing_surface = awt.GetDrawingSurface(env, thisObj);
    if (drawing_surface == NULL)
    {
        draw_debug_text(env, graphics, "Canvas surface unavailable");
        return;
    }

    lock = drawing_surface->Lock(drawing_surface);
    if ((lock & JAWT_LOCK_ERROR) != 0)
    {
        awt.FreeDrawingSurface(drawing_surface);
        draw_debug_text(env, graphics, "Canvas lock failed");
        return;
    }

    dsi = drawing_surface->GetDrawingSurfaceInfo(drawing_surface);
    if (dsi != NULL)
    {
#if defined(__APPLE__)
        native_surface = dsi->platformInfo;
#elif defined(_WIN32)
        JAWT_Win32DrawingSurfaceInfo *win_info;
        win_info = (JAWT_Win32DrawingSurfaceInfo *)dsi->platformInfo;
        if (win_info != NULL)
        {
            native_display = win_info->hdc;
            native_surface = win_info->hwnd;
        }
#else
        JAWT_X11DrawingSurfaceInfo *x11_info;
        x11_info = (JAWT_X11DrawingSurfaceInfo *)dsi->platformInfo;
        if (x11_info != NULL)
        {
            native_display = x11_info->display;
            native_surface = (void *)(intptr_t)x11_info->drawable;
        }
#endif
        drawing_surface->FreeDrawingSurfaceInfo(dsi);
    }

    drawing_surface->Unlock(drawing_surface);
    awt.FreeDrawingSurface(drawing_surface);

    write_pointer_out(env, display, native_display);
    write_pointer_out(env, surface, native_surface);

    if (native_surface != NULL)
    {
        draw_debug_text(env, graphics, "WebGPU native surface ready");
    }
    else
    {
        draw_debug_text(env, graphics, "Native surface not resolved");
    }
}

JNIEXPORT jlong JNICALL Java_org_java_desktop_ffmawt_FFMCanvas_nativeCreateOpenGlContext(
    JNIEnv *env,
    jclass clazz,
    jobject surface)
{
    NSOpenGLPixelFormatAttribute attrs[] = {
        NSOpenGLPFAOpenGLProfile, NSOpenGLProfileVersionLegacy,
        NSOpenGLPFAColorSize, 24,
        NSOpenGLPFAAlphaSize, 8,
        NSOpenGLPFADepthSize, 16,
        NSOpenGLPFADoubleBuffer,
        NSOpenGLPFAAccelerated,
        0};
    FFMNativeGlContext *native_ctx;
    NSOpenGLPixelFormat *pixel_format;
    NSOpenGLContext *context;
    NSView *view;
    void *surface_ptr;

    (void)clazz;

    surface_ptr = segment_pointer(env, surface);
    view = surface_to_view(surface_ptr);
    if (view == nil)
    {
        return 0;
    }

    native_ctx = calloc(1, sizeof(FFMNativeGlContext));
    if (native_ctx == NULL)
    {
        return 0;
    }

    @autoreleasepool
    {
        pixel_format = [[NSOpenGLPixelFormat alloc] initWithAttributes:attrs];
        if (pixel_format == nil)
        {
            free(native_ctx);
            return 0;
        }

        context = [[NSOpenGLContext alloc] initWithFormat:pixel_format shareContext:nil];
        [pixel_format release];
        if (context == nil)
        {
            free(native_ctx);
            return 0;
        }

        [context setView:view];
        [context makeCurrentContext];
        [context update];

        native_ctx->context = context;
        native_ctx->view = view;
    }

    return (jlong)(intptr_t)native_ctx;
}

JNIEXPORT void JNICALL Java_org_java_desktop_ffmawt_FFMCanvas_nativeMakeOpenGlContextCurrent(
    JNIEnv *env,
    jclass clazz,
    jlong contextHandle)
{
    FFMNativeGlContext *native_ctx;

    (void)env;
    (void)clazz;

    native_ctx = as_gl_context(contextHandle);
    if (native_ctx == NULL || native_ctx->context == nil)
    {
        return;
    }

    [native_ctx->context makeCurrentContext];
    [native_ctx->context update];
}

JNIEXPORT void JNICALL Java_org_java_desktop_ffmawt_FFMCanvas_nativeFlushOpenGlContext(
    JNIEnv *env,
    jclass clazz,
    jlong contextHandle)
{
    FFMNativeGlContext *native_ctx;

    (void)env;
    (void)clazz;

    native_ctx = as_gl_context(contextHandle);
    if (native_ctx == NULL || native_ctx->context == nil)
    {
        return;
    }

    [native_ctx->context flushBuffer];
}

JNIEXPORT void JNICALL Java_org_java_desktop_ffmawt_FFMCanvas_nativeDestroyOpenGlContext(
    JNIEnv *env,
    jclass clazz,
    jlong contextHandle)
{
    FFMNativeGlContext *native_ctx;

    (void)env;
    (void)clazz;

    native_ctx = as_gl_context(contextHandle);
    if (native_ctx == NULL)
    {
        return;
    }

    if (native_ctx->context != nil)
    {
        [native_ctx->context clearDrawable];
        [NSOpenGLContext clearCurrentContext];
        [native_ctx->context release];
    }

    free(native_ctx);
}
