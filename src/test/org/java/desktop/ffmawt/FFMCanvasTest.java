package org.java.desktop.ffmawt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Locale;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

public class FFMCanvasTest {

    private static final class NativePointers {
        private final MemorySegment display;
        private final MemorySegment surface;

        private NativePointers(MemorySegment display, MemorySegment surface) {
            this.display = display;
            this.surface = surface;
        }
    }

    private static NativePointers invokeNativePaint(Arena arena) throws Exception {
        MemorySegment displayOut = arena.allocate(ValueLayout.ADDRESS);
        MemorySegment surfaceOut = arena.allocate(ValueLayout.ADDRESS);
        FFMCanvas canvas = new FFMCanvas(displayOut, surfaceOut);

        Frame frame = new Frame("ffm-awt-test");
        frame.add(canvas);
        frame.setSize(320, 240);

        SwingUtilities.invokeAndWait(() -> frame.setVisible(true));
        try {
            SwingUtilities.invokeAndWait(() -> {
                BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = image.createGraphics();
                try {
                    canvas.paint(graphics);
                } finally {
                    graphics.dispose();
                }
            });
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
            });
        }

        return new NativePointers(
                displayOut.get(ValueLayout.ADDRESS, 0),
                surfaceOut.get(ValueLayout.ADDRESS, 0));
    }

    @Test
    void nativePaintWritesSurfacePointer() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a display-capable environment");

        try (Arena arena = Arena.ofConfined()) {
            NativePointers pointers = invokeNativePaint(arena);
            assertNotEquals(MemorySegment.NULL, pointers.surface,
                    "nativePaint should publish a native surface pointer for the WebGPU binding");
        }
    }

    @Test
    void nativePaintWritesExpectedDisplayPointerForPlatform() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a display-capable environment");

        try (Arena arena = Arena.ofConfined()) {
            NativePointers pointers = invokeNativePaint(arena);
            assertNotNull(pointers.display);

            String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            if (osName.contains("mac")) {
                assertEquals(MemorySegment.NULL, pointers.display,
                        "macOS JAWT does not provide a separate display handle");
            } else {
                assertNotEquals(MemorySegment.NULL, pointers.display,
                        "Non-macOS platforms should provide a non-null display/context handle");
            }
        }
    }

    @Test
    void jextractOpenGlRendererCanDrawToCanvas() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a display-capable environment");
        assumeTrue(Boolean.getBoolean("ffmawt.test.opengl.canvas"),
                "Set -Dffmawt.test.opengl.canvas=true to run the experimental OpenGL-on-canvas integration test");

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        assumeTrue(osName.contains("mac"), "Current native OpenGL context bridge is macOS-specific");

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment displayOut = arena.allocate(ValueLayout.ADDRESS);
            MemorySegment surfaceOut = arena.allocate(ValueLayout.ADDRESS);
            FFMCanvas canvas = new FFMCanvas(displayOut, surfaceOut);

            Frame frame = new Frame("ffm-awt-jextract-gl-test");
            frame.add(canvas);
            frame.setSize(320, 240);

            SwingUtilities.invokeAndWait(() -> frame.setVisible(true));
            try {
                SwingUtilities.invokeAndWait(() -> {
                    BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = image.createGraphics();
                    try {
                        canvas.paint(graphics);
                    } finally {
                        graphics.dispose();
                    }
                });

                MemorySegment surfacePointer = surfaceOut.get(ValueLayout.ADDRESS, 0);
                assertNotEquals(MemorySegment.NULL, surfacePointer,
                        "nativePaint should publish a native canvas surface pointer before OpenGL rendering");

                SwingUtilities.invokeAndWait(() -> JextractOpenGlRenderer.drawTriangleToCanvas(canvas, 320, 240));
            } finally {
                SwingUtilities.invokeAndWait(() -> {
                    frame.setVisible(false);
                    frame.dispose();
                });
            }
        }
    }
}
