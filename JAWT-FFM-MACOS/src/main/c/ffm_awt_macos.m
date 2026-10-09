#import "ffm_awt_macos.h"
#import <Cocoa/Cocoa.h>
#import <jawt_md.h>

void *awt_ffm_component_layer(void *platformInfo)
{
    if (platformInfo == NULL) {
        return NULL;
    }

    id<JAWT_SurfaceLayers> surfaceLayers =
        (id<JAWT_SurfaceLayers>)platformInfo;
    return (void *)surfaceLayers.layer;
}

void *awt_ffm_window_layer(void *platformInfo)
{
    if (platformInfo == NULL) {
        return NULL;
    }

    id<JAWT_SurfaceLayers> surfaceLayers =
        (id<JAWT_SurfaceLayers>)platformInfo;
    return (void *)surfaceLayers.windowLayer;
}