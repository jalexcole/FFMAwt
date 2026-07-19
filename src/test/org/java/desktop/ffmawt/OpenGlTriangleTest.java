package org.java.desktop.ffmawt;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetError;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL33C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL33C.GL_BACK;
import static org.lwjgl.opengl.GL33C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL33C.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL33C.GL_FALSE;
import static org.lwjgl.opengl.GL33C.GL_FLOAT;
import static org.lwjgl.opengl.GL33C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL33C.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL33C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL33C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL33C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL33C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL33C.glAttachShader;
import static org.lwjgl.opengl.GL33C.glBindBuffer;
import static org.lwjgl.opengl.GL33C.glBindVertexArray;
import static org.lwjgl.opengl.GL33C.glBufferData;
import static org.lwjgl.opengl.GL33C.glClear;
import static org.lwjgl.opengl.GL33C.glClearColor;
import static org.lwjgl.opengl.GL33C.glCompileShader;
import static org.lwjgl.opengl.GL33C.glCreateProgram;
import static org.lwjgl.opengl.GL33C.glCreateShader;
import static org.lwjgl.opengl.GL33C.glDeleteBuffers;
import static org.lwjgl.opengl.GL33C.glDeleteProgram;
import static org.lwjgl.opengl.GL33C.glDeleteShader;
import static org.lwjgl.opengl.GL33C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL33C.glDrawArrays;
import static org.lwjgl.opengl.GL33C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL33C.glFinish;
import static org.lwjgl.opengl.GL33C.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL33C.glGetProgrami;
import static org.lwjgl.opengl.GL33C.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL33C.glGetShaderi;
import static org.lwjgl.opengl.GL33C.glGetUniformLocation;
import static org.lwjgl.opengl.GL33C.glGenBuffers;
import static org.lwjgl.opengl.GL33C.glGenVertexArrays;
import static org.lwjgl.opengl.GL33C.glLinkProgram;
import static org.lwjgl.opengl.GL33C.glReadBuffer;
import static org.lwjgl.opengl.GL33C.glReadPixels;
import static org.lwjgl.opengl.GL33C.glShaderSource;
import static org.lwjgl.opengl.GL33C.glUniform4f;
import static org.lwjgl.opengl.GL33C.glUseProgram;
import static org.lwjgl.opengl.GL33C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33C.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.GraphicsEnvironment;
import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

class OpenGlTriangleTest {

    @Test
    void rendersOpenGlTriangleToOffscreenWindow() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Requires a display-capable environment");

        boolean glfwOk;
        try {
            glfwOk = glfwInit();
        } catch (IllegalStateException ex) {
            assumeTrue(false, "Skipping OpenGL test: " + ex.getMessage());
            return;
        }
        assumeTrue(glfwOk,
                () -> "GLFW initialization failed, error code: " + glfwGetError((org.lwjgl.PointerBuffer) null));

        long window = NULL;
        int vao = 0;
        int vbo = 0;
        int vertexShader = 0;
        int fragmentShader = 0;
        int program = 0;

        try {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

            window = glfwCreateWindow(256, 256, "opengl-triangle-test", NULL, NULL);
            assumeTrue(window != NULL,
                    () -> "Could not create OpenGL window, error code: "
                            + glfwGetError((org.lwjgl.PointerBuffer) null));

            glfwMakeContextCurrent(window);
            GL.createCapabilities();

            float[] vertices = {
                    -0.6f, -0.5f,
                    0.6f, -0.5f,
                    0.0f, 0.6f
            };

            vertexShader = createShader(GL_VERTEX_SHADER,
                    "#version 330 core\n"
                            + "layout (location = 0) in vec2 position;\n"
                            + "void main() {\n"
                            + "    gl_Position = vec4(position, 0.0, 1.0);\n"
                            + "}\n");

            fragmentShader = createShader(GL_FRAGMENT_SHADER,
                    "#version 330 core\n"
                            + "uniform vec4 color;\n"
                            + "out vec4 fragColor;\n"
                            + "void main() {\n"
                            + "    fragColor = color;\n"
                            + "}\n");

            program = glCreateProgram();
            glAttachShader(program, vertexShader);
            glAttachShader(program, fragmentShader);
            glLinkProgram(program);
            assertTrue(glGetProgrami(program, GL_LINK_STATUS) != GL_FALSE,
                    "Program link failed: " + glGetProgramInfoLog(program));

            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0L);
            glEnableVertexAttribArray(0);

            glViewport(0, 0, 256, 256);
            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(program);
            int colorLoc = glGetUniformLocation(program, "color");
            glUniform4f(colorLoc, 1f, 0f, 0f, 1f);
            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glFinish();
            glfwSwapBuffers(window);
            glfwPollEvents();

            ByteBuffer pixel = BufferUtils.createByteBuffer(4);
            glReadBuffer(GL_BACK);
            glReadPixels(128, 128, 1, 1, org.lwjgl.opengl.GL33C.GL_RGBA, GL_UNSIGNED_BYTE, pixel);

            int red = Byte.toUnsignedInt(pixel.get(0));
            int green = Byte.toUnsignedInt(pixel.get(1));
            int blue = Byte.toUnsignedInt(pixel.get(2));
            assertTrue(red > 150 && green < 80 && blue < 80,
                    () -> "Expected red triangle pixel near center but read RGB=" + red + "," + green + "," + blue);
        } finally {
            if (program != 0) {
                glDeleteProgram(program);
            }
            if (vertexShader != 0) {
                glDeleteShader(vertexShader);
            }
            if (fragmentShader != 0) {
                glDeleteShader(fragmentShader);
            }
            if (vbo != 0) {
                glDeleteBuffers(vbo);
            }
            if (vao != 0) {
                glDeleteVertexArrays(vao);
            }
            if (window != NULL) {
                glfwDestroyWindow(window);
            }
            glfwTerminate();
        }
    }

    private static int createShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);
        assertTrue(glGetShaderi(shader, GL_COMPILE_STATUS) != GL_FALSE,
                () -> "Shader compilation failed: " + glGetShaderInfoLog(shader));
        return shader;
    }
}