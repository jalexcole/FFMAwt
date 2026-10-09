package io.desktop.ffm.api;

public interface DrawingSurface {

    int lock() throws Throwable;

    DrawingSurfaceInfo drawingSurfaceInfo() throws Throwable;

    void freeDrawingSurfaceInfo(DrawingSurfaceInfo info) throws Throwable;

    void unlock() throws Throwable;
}
