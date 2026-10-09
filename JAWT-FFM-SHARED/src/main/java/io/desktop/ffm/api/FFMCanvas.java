package io.desktop.ffm.api;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.lang.foreign.SegmentAllocator;
import java.util.Objects;

import io.desktop.ffm.JawtFFM;

public abstract class FFMCanvas extends Canvas {

    static {
        try {
            System.loadLibrary("jawtffmjni");
            System.out.println("Successfully loaded jawtffmjni library");
        } catch (UnsatisfiedLinkError e) {
            throw new IllegalStateException("Unable to load jawtffmjni library", e);
        }
    }

    protected final transient Jawt awt;
    protected transient DrawingSurface drawingSurface;
    protected transient DrawingSurfaceInfo drawingSurfaceInfo;
    protected boolean result;
    protected int lock;
    protected final NativeDisplayPointer nativeDisplayPointer;

    protected FFMCanvas(SegmentAllocator segmentAllocator) throws AWTException {
        awt = JawtFFM.getAwt(
                Objects.requireNonNull(segmentAllocator, "segmentAllocator"),
                JawtFFM.VERSION_9);
        result = awt != null;

        this.drawingSurface = awt.getDrawingSurface();
        try {
            this.drawingSurfaceInfo = this.drawingSurface.drawingSurfaceInfo();
        } catch (Throwable e) {
            throw new AWTException(e.getMessage());
        }
        this.nativeDisplayPointer = JawtFFM.getJawtNativeDisplayPointerFactory().fetch(segmentAllocator);
    }

    protected final Jawt awt() {
        return awt;
    }

    protected final DrawingSurface drawingSurface() {
        return drawingSurface;
    }

    protected final DrawingSurfaceInfo drawingSurfaceInfo() {
        return drawingSurfaceInfo;
    }

    protected final boolean jawtResult() {
        return result;
    }

    protected final int drawingSurfaceLock() {
        return lock;
    }

    protected final DrawingSurfaceInfo getDrawingSurfaceInfo() {
        return drawingSurfaceInfo;
    }

    protected final DrawingSurface getDrawingSurface() {
        return drawingSurface;
    }
    /// ```java
    /// assert(result != JNI_FALSE);
    /// // Get the drawing surface
    /// ds=awt.GetDrawingSurface(env,canvas);
    /// assert(ds!=NULL);** // Lock the
    /// drawing surface
    /// lock=ds.lock(ds);
    /// assert((lock & JAWT_LOCK_ERROR) == 0);
    ///
    /// // Get the drawing surface info
    /// dsi = ds.getDrawingSurfaceInfo();
    ///
    /// // Get the platform-specific drawing info
    /// dsi_win = (JAWT_Win32DrawingSurfaceInfo*)dsi->platformInfo;
    ///
    /// //////////////////////////////
    /// // !!! DO PAINTING HERE !!! //
    /// //////////////////////////////
    ///
    /// // Free the drawing surface info
    /// ds.freeDrawingSurfaceInfo(dsi);
    ///
    /// // Unlock the drawing surface
    /// ds.unlock(ds);
    ///
    /// // Free the drawing surface
    /// awt.freeDrawingSurface(ds);
    /// ```
    @Override
    public abstract void paint(Graphics g);

}
