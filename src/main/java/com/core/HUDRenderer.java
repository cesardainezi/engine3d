package com.core;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

public class HUDRenderer {

    private int shaderProgram;
    private int vao, vbo;

    public void init(Window window) {
        // Shader 2D simples (cor sólida ou textura de fonte)
        String vs = ShaderUtils.loadResource("shaders/hud.vert");
        String fs = ShaderUtils.loadResource("shaders/hud.frag");
        shaderProgram = ShaderUtils.createProgram(vs, fs);

        // Exemplo: quad 2D
        float[] vertices = {
                // x, y,   u, v
                50f,  50f, 0f, 0f,
                150f,  50f, 1f, 0f,
                150f, 150f, 1f, 1f,
                50f, 150f, 0f, 1f,
        };
        int[] indices = { 0, 1, 2, 2, 3, 0 };

        vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length);
        fb.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);

        int ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        // Pos (2 floats)
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // TexCoord (2 floats)
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    public void render(Window window) {
        // Desabilita z-buffer
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL20.glUseProgram(shaderProgram);

        // Matriz ortográfica
        Matrix4f projection = new Matrix4f().ortho(
                0, window.getWidth(),
                window.getHeight(), 0,
                -1, 1
        );
        FloatBuffer proj = BufferUtils.createFloatBuffer(16);
        projection.get(proj);
        int loc = GL20.glGetUniformLocation(shaderProgram, "uProjection");
        GL20.glUniformMatrix4fv(loc, false, proj);

        GL30.glBindVertexArray(vao);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);

        // Reativa depth test
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void cleanup() {
        GL20.glDeleteProgram(shaderProgram);
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);
    }
}