

#if defined(__linux__) || (defined(__unix__) && !defined(__APPLE__))

#include "ffm_awt_unix.h"
#include "jawt_md.h"

Display *awt_ffm_display(void *surfaceInfo)
{
    // Implement the function to fetch the display from platformInfo
    JAWT_X11DrawingSurfaceInfo *x11Info =
        (JAWT_X11DrawingSurfaceInfo *)surfaceInfo->platformInfo;
    Display *display = x11Info->display;
    return display;
}

Drawable awt_ffm_drawable(void *surfaceInfo)
{
    // Implement the function to fetch the drawable from surfaceInfo
    JAWT_X11DrawingSurfaceInfo *x11Info =
        (JAWT_X11DrawingSurfaceInfo *)surfaceInfo->platformInfo;
    Drawable drawable = x11Info->drawable;
    return drawable;
}

#endif