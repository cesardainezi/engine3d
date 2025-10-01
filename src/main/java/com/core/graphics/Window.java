package com.core.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private int width, height;
    private String title;
    private long windowHandle;
    private boolean fullscreen = false;
    private int windowedX, windowedY, windowedWidth, windowedHeight;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar GLFW!");
        }

        // Permitir redimensionamento
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Criar janela
        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Falha ao criar janela!");
        }

        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        // Definir viewport inicial
        GL11.glViewport(0, 0, width, height);

        // aqui ativa o V-Sync
        GLFW.glfwSwapInterval(1);

        // 🔹 Callback de resize (ajusta viewport)
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int newWidth, int newHeight) {
                width = newWidth;
                height = newHeight;

                // Atualiza viewport
                GL11.glViewport(0, 0, width, height);
            }
        });

        // Exibe janela
        GLFW.glfwShowWindow(windowHandle);

        toggleFullscreen();
    }

    public void toggleFullscreen() {
        fullscreen = !fullscreen;

        if (fullscreen) {
            // salva posição/tamanho da janela antes de fullscreen
            int[] xpos = new int[1];
            int[] ypos = new int[1];
            org.lwjgl.glfw.GLFW.glfwGetWindowPos(windowHandle, xpos, ypos);
            windowedX = xpos[0];
            windowedY = ypos[0];

            int[] w = new int[1];
            int[] h = new int[1];
            org.lwjgl.glfw.GLFW.glfwGetWindowSize(windowHandle, w, h);
            windowedWidth = w[0];
            windowedHeight = h[0];

            // configura fullscreen no monitor primário
            long monitor = org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor();
            org.lwjgl.glfw.GLFWVidMode vidmode = org.lwjgl.glfw.GLFW.glfwGetVideoMode(monitor);

            org.lwjgl.glfw.GLFW.glfwSetWindowMonitor(windowHandle,
                    monitor,
                    0, 0,
                    vidmode.width(), vidmode.height(),
                    vidmode.refreshRate());
        } else {
            // volta para modo janela usando tamanho salvo
            org.lwjgl.glfw.GLFW.glfwSetWindowMonitor(windowHandle,
                    0,
                    windowedX, windowedY,
                    windowedWidth, windowedHeight,
                    0);
        }
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(windowHandle);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();
    }
}