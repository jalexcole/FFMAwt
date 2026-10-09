#ifndef JAWT_FFM_H
#define JAWT_FFM_H

#include <stdint.h>

#ifdef __cplusplus
extern "C"
{
#endif

    typedef void *JAWT_FFM_Env;
    typedef void *JAWT_FFM_Object;

    typedef struct JAWT_FFM_Rectangle
    {
        int32_t x;
        int32_t y;
        int32_t width;
        int32_t height;
    } JAWT_FFM_Rectangle;

    struct JAWT_FFM_DrawingSurface;

    /**
     * # Drawing Surface Information
     *
     * Describes a platform drawing surface obtained from
     * `JAWT_FFM_DrawingSurface::GetDrawingSurfaceInfo` while the associated
     * drawing surface is locked.
     *
     * ## Lifetime
     *
     * The information is valid only until it is released through
     * `JAWT_FFM_DrawingSurface::FreeDrawingSurfaceInfo`. Callers must release
     * it before unlocking the associated drawing surface and must not retain
     * pointers obtained from this structure beyond that lifetime.
     */
    typedef struct JAWT_FFM_DrawingSurfaceInfo
    {
        /**
         * Platform-specific JAWT drawing-surface data.
         *
         * On macOS, points to an `NSObject` that conforms to the
         * `JAWT_SurfaceLayers` protocol. This lets Objective-C callers access
         * the component and window `CALayer` objects.
         *
         * ```objective-c
         * id<JAWT_SurfaceLayers> surfaceLayers =
         *     (id<JAWT_SurfaceLayers>)surfaceInfo->platformInfo;
         * CALayer *windowLayer = surfaceLayers.windowLayer;
         * ```
         *
         * On Windows, points to `JAWT_Win32DrawingSurfaceInfo`, which exposes
         * the native window through `hwnd` and its device context through
         * `hdc`.
         *
         * ```c
         * JAWT_Win32DrawingSurfaceInfo *winInfo =
         *     (JAWT_Win32DrawingSurfaceInfo *)surfaceInfo->platformInfo;
         * HWND window = winInfo->hwnd;
         * HDC deviceContext = winInfo->hdc;
         * ```
         *
         * On Unix systems using X11, points to `JAWT_X11DrawingSurfaceInfo`,
         * which exposes the X11 `Display *` through `display` and the native
         * drawable identifier through `drawable`.
         *
         * ```c
         * JAWT_X11DrawingSurfaceInfo *x11Info =
         *     (JAWT_X11DrawingSurfaceInfo *)surfaceInfo->platformInfo;
         * Display *display = x11Info->display;
         * Drawable drawable = x11Info->drawable;
         * ```
         */
        void *platformInfo;
        /** The drawing surface that owns this information. */
        struct JAWT_FFM_DrawingSurface *ds;
        /** Bounds of the drawing surface in component coordinates. */
        JAWT_FFM_Rectangle bounds;
        /** Number of rectangles in `clip`. */
        int32_t clipSize;
        /** Clipping rectangles; contains `clipSize` entries. */
        JAWT_FFM_Rectangle *clip;
    } JAWT_FFM_DrawingSurfaceInfo;

    typedef int32_t (*JAWT_FFM_Lock)(struct JAWT_FFM_DrawingSurface *ds);
    typedef JAWT_FFM_DrawingSurfaceInfo *(*JAWT_FFM_GetDrawingSurfaceInfo)(
        struct JAWT_FFM_DrawingSurface *ds);
    typedef void (*JAWT_FFM_FreeDrawingSurfaceInfo)(JAWT_FFM_DrawingSurfaceInfo *dsi);
    typedef void (*JAWT_FFM_Unlock)(struct JAWT_FFM_DrawingSurface *ds);

    typedef struct JAWT_FFM_DrawingSurface
    {
        JAWT_FFM_Env env;
        JAWT_FFM_Object target;
        JAWT_FFM_Lock Lock;
        JAWT_FFM_GetDrawingSurfaceInfo GetDrawingSurfaceInfo;
        JAWT_FFM_FreeDrawingSurfaceInfo FreeDrawingSurfaceInfo;
        JAWT_FFM_Unlock Unlock;
    } JAWT_FFM_DrawingSurface;

    typedef JAWT_FFM_DrawingSurface *(*JAWT_FFM_GetDrawingSurface)(
        JAWT_FFM_Env env, JAWT_FFM_Object target);
    typedef void (*JAWT_FFM_FreeDrawingSurface)(JAWT_FFM_DrawingSurface *ds);
    typedef void (*JAWT_FFM_LockAWT)(JAWT_FFM_Env env);
    typedef JAWT_FFM_Object (*JAWT_FFM_GetComponent)(
        JAWT_FFM_Env env, void *platformInfo);
    typedef JAWT_FFM_Object (*JAWT_FFM_CreateEmbeddedFrame)(
        JAWT_FFM_Env env, void *platformInfo);
    typedef void (*JAWT_FFM_SetBounds)(JAWT_FFM_Env env,
                                       JAWT_FFM_Object embeddedFrame, int32_t x, int32_t y,
                                       int32_t width, int32_t height);
    typedef void (*JAWT_FFM_SynthesizeWindowActivation)(JAWT_FFM_Env env,
                                                        JAWT_FFM_Object embeddedFrame, uint8_t doActivate);

    typedef struct JAWT_FFM
    {
        int32_t version;
        JAWT_FFM_GetDrawingSurface GetDrawingSurface;
        JAWT_FFM_FreeDrawingSurface FreeDrawingSurface;
        JAWT_FFM_LockAWT Lock;
        JAWT_FFM_LockAWT Unlock;
        JAWT_FFM_GetComponent GetComponent;
        JAWT_FFM_CreateEmbeddedFrame CreateEmbeddedFrame;
        JAWT_FFM_SetBounds SetBounds;
        JAWT_FFM_SynthesizeWindowActivation SynthesizeWindowActivation;
    } JAWT_FFM;

#define JAWT_FFM_LOCK_ERROR 0x00000001
#define JAWT_FFM_LOCK_CLIP_CHANGED 0x00000002
#define JAWT_FFM_LOCK_BOUNDS_CHANGED 0x00000004
#define JAWT_FFM_LOCK_SURFACE_CHANGED 0x00000008

#define JAWT_FFM_VERSION_1_3 0x00010003
#define JAWT_FFM_VERSION_1_4 0x00010004
#define JAWT_FFM_VERSION_1_7 0x00010007
#define JAWT_FFM_VERSION_9 0x00090000

    /*
     * Initialize the FFM-safe copy of the JAWT function table.
     */
    int32_t JAWT_GetAWT_FFM(JAWT_FFM *awt);

#ifdef __cplusplus
}
#endif

#endif