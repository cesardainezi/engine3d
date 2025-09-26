package com.core;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Floor extends GameObject {
    private int vaoId, vboId, eboId, shaderProgram;
    private Texture texture;


    public void init() {
        float size = 25f;              // metade do tamanho do chão em unidades do mundo
        float texScale = 1.0f;         // quantidade de repetições de textura por unidade do mundo

        float[] vertices = {
                -size, 0f, -size,   0, 1, 0,   0,                   0,
                 size, 0f, -size,   0, 1, 0,   2 * size * texScale, 0,
                 size, 0f,  size,   0, 1, 0,   2 * size * texScale, 2 * size * texScale,
                -size, 0f,  size,   0, 1, 0,   0,                   2 * size * texScale
        };

        int[] indices = {
                0,1,2,
                2,3,0
        };

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8*Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 8*Float.BYTES, 3*Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 8*Float.BYTES, 6*Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        MemoryUtil.memFree(vertexBuffer);
        MemoryUtil.memFree(indexBuffer);

        GL30.glBindVertexArray(0);

        // Usa o mesmo shader de "textura+iluminação"
        String vs = ShaderUtils.loadResource("shaders/basic.vert");
        String fs = ShaderUtils.loadResource("shaders/basic.frag");
        shaderProgram = ShaderUtils.createProgram(vs, fs);

        texture = new Texture("textures/grass.png"); // coloque uma grama/pedra seamless
    }

    @Override
    public void update(double dt) {}

    @Override
    public void render(Camera camera, Window window) {
        GL20.glUseProgram(shaderProgram);

        Matrix4f model = new Matrix4f().identity();

        Matrix4f projection = new Matrix4f()
                .perspective((float)Math.toRadians(70.0f),
                        (float) window.getWidth() / window.getHeight(),
                        0.01f, 100.0f);

        Matrix4f mvp = new Matrix4f();
        projection.mul(camera.getViewMatrix(), mvp);
        mvp.mul(model);

        int mvpLoc = GL20.glGetUniformLocation(shaderProgram, "uMVP");
        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "uModel");

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);
        GL20.glUniformMatrix4fv(mvpLoc, false, fb);

        fb.clear();
        model.get(fb);
        GL20.glUniformMatrix4fv(modelLoc, false, fb);

        // light
        int lightLoc = GL20.glGetUniformLocation(shaderProgram, "lightPos");
        GL20.glUniform3f(lightLoc, 2f, 5f, 2f);

        // textura piso
        texture.bind();
        int texLoc = GL20.glGetUniformLocation(shaderProgram,"uTexture");
        GL20.glUniform1i(texLoc, 0);

        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
    }

    @Override
    public void cleanup() {
        texture.cleanup();
        GL20.glDeleteProgram(shaderProgram);
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}