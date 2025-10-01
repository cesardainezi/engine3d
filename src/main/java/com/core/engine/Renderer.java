package com.core.engine;

import org.lwjgl.opengl.GL11;

public class Renderer {

    public void init() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void clear() {
        // Cor de fundo RGBA
        GL11.glClearColor(0.0f, 0.5f, 0.8f, 1.0f);

        // Zera buffers de cor e profundidade
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

}