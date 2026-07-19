package org.java.desktop.ffmawt;

import org.java.desktop.ffmawt.gl.gl_h;

public final class JextractOpenGlRenderer {

    private JextractOpenGlRenderer() {
    }

    public static void drawTriangleToCanvas(FFMCanvas canvas, int width, int height) {
        long contextHandle = canvas.createNativeOpenGlContext();
        if (contextHandle == 0L) {
            throw new IllegalStateException("Unable to create NSOpenGLContext for FFMCanvas");
        }

        try {
            canvas.makeNativeOpenGlContextCurrent(contextHandle);

            gl_h.glViewport(0, 0, width, height);
            gl_h.glClearColor(0.05f, 0.05f, 0.08f, 1.0f);
            gl_h.glClear(gl_h.GL_COLOR_BUFFER_BIT());

            gl_h.glBegin(gl_h.GL_TRIANGLES());
            gl_h.glColor3f(1.0f, 0.1f, 0.1f);
            gl_h.glVertex2f(-0.6f, -0.5f);
            gl_h.glColor3f(0.1f, 1.0f, 0.2f);
            gl_h.glVertex2f(0.6f, -0.5f);
            gl_h.glColor3f(0.2f, 0.4f, 1.0f);
            gl_h.glVertex2f(0.0f, 0.6f);
            gl_h.glEnd();

            gl_h.glFlush();
            canvas.flushNativeOpenGlContext(contextHandle);
        } finally {
            canvas.destroyNativeOpenGlContext(contextHandle);
        }
    }
}
