package com.core.input;

import org.lwjgl.glfw.GLFW;

public class Input {
    private long window;
    private double lastMouseX, lastMouseY;
    private double deltaX, deltaY;
    private boolean firstMouse = true;

    private boolean[] keys;      // estado atual
    private boolean[] prevKeys;  // estado no frame anterior

    public Input(long window) {
        this.window = window;
        int keyCount = GLFW.GLFW_KEY_LAST + 1; // total de teclas suportadas
        keys = new boolean[keyCount];
        prevKeys = new boolean[keyCount];
    }

    /**
     * Deve ser chamado a cada frame no loop principal (antes da lógica do jogo).
     */
    public void update() {
        // Copia estado atual para prevKeys (para comparação depois)
        for (int i = 0; i < keys.length; i++) {
            prevKeys[i] = keys[i];
            keys[i] = GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS;
        }
    }

    /** Retorna true enquanto a tecla estiver pressionada */
    public boolean isKeyPressed(int key) {
        if (key < 0 || key >= keys.length) return false;
        return keys[key];
    }

    /** Retorna true apenas no frame em que a tecla foi pressionada */
    public boolean isKeyJustPressed(int key) {
        if (key < 0 || key >= keys.length) return false;
        return keys[key] && !prevKeys[key];
    }

    /** Retorna true apenas no frame em que a tecla foi solta */
    public boolean isKeyJustReleased(int key) {
        if (key < 0 || key >= keys.length) return false;
        return !keys[key] && prevKeys[key];
    }

    public void initMouse() {
        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
            }

            deltaX = xpos - lastMouseX;
            deltaY = lastMouseY - ypos; // invertido porque Y cresce pra baixo na tela

            lastMouseX = xpos;
            lastMouseY = ypos;
        });
    }

    public double getDeltaX() {
        double dx = deltaX;
        deltaX = 0; // reseta após leitura
        return dx;
    }

    public double getDeltaY() {
        double dy = deltaY;
        deltaY = 0;
        return dy;
    }
}