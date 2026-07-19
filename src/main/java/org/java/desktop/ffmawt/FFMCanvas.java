package org.java.desktop.ffmawt;

import java.awt.Canvas;
import java.lang.foreign.MemorySegment;
import java.awt.Graphics;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FFMCanvas extends Canvas {
    private static final String JNI_LIB_BASENAME = "ffmawtjni";

    static {
        loadNativeLibrary();
    }

    private final MemorySegment display;
    private final MemorySegment surface;

    public FFMCanvas(MemorySegment display, MemorySegment surface) {
        this.display = display;
        this.surface = surface;
    }

    @Override
    public void paint(Graphics g) {
        nativePaint(g, display, surface);
    }

    MemorySegment surfaceSegment() {
        return surface;
    }

    long createNativeOpenGlContext() {
        return nativeCreateOpenGlContext(surface);
    }

    void makeNativeOpenGlContextCurrent(long contextHandle) {
        nativeMakeOpenGlContextCurrent(contextHandle);
    }

    void flushNativeOpenGlContext(long contextHandle) {
        nativeFlushOpenGlContext(contextHandle);
    }

    void destroyNativeOpenGlContext(long contextHandle) {
        nativeDestroyOpenGlContext(contextHandle);
    }

    private static void loadNativeLibrary() {
        try {
            System.loadLibrary(JNI_LIB_BASENAME);
            return;
        } catch (UnsatisfiedLinkError ignored) {
            // Fall through to development-path loading.
        }

        Path projectDir = Paths.get("").toAbsolutePath();
        String fileName = System.mapLibraryName(JNI_LIB_BASENAME);
        Path localLib = projectDir.resolve("target").resolve("native").resolve(fileName);

        if (Files.exists(localLib)) {
            System.load(localLib.toAbsolutePath().toString());
            return;
        }

        throw new UnsatisfiedLinkError(
                "Unable to load JNI library '" + JNI_LIB_BASENAME + "'. "
                        + "Tried java.library.path and " + localLib.toAbsolutePath());
    }

    private native void nativePaint(Graphics g, MemorySegment display, MemorySegment surface);

    private static native long nativeCreateOpenGlContext(MemorySegment surface);

    private static native void nativeMakeOpenGlContextCurrent(long contextHandle);

    private static native void nativeFlushOpenGlContext(long contextHandle);

    private static native void nativeDestroyOpenGlContext(long contextHandle);
}
