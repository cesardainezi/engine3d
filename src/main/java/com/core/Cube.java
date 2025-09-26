package com.core;

import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Cube extends GameObject {

    private int vaoId;
    private int vboId;
    private int eboId;
    private int shaderProgram;
    private Texture texture;
    private int mvpLoc, modelLoc, lightLoc, colorLoc;
    private Vector3f position;

    private float rotation = 0.0f; // ângulo em radianos ou graus

    public Cube (Vector3f position){
        this.position = position;
    }

    @Override
    public void update(double dt) {
        // Incrementa rotação (aqui em radianos/segundos)
        rotation += 1.414f * dt;
    }

    @Override
    public void render(Camera camera, Window window) {
        GL20.glUseProgram(shaderProgram);

        // 🔹 Montar Model/View/Projection
        Matrix4f model = new Matrix4f().identity()
                .translate(position)
                .rotateY(rotation)
                .rotateX(rotation * 0.5f);

        Matrix4f view = camera.getViewMatrix();   // pega view da câmera
        Matrix4f projection = new Matrix4f()
                .perspective((float)Math.toRadians(70.0f),
                        (float) window.getWidth() / window.getHeight(),
                        0.01f, 100.0f);

        Matrix4f mvp = new Matrix4f();
        projection.mul(view, mvp);
        mvp.mul(model);

        // envia uMVP
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);
        GL20.glUniformMatrix4fv(mvpLoc, false, fb);

        // envia uModel
        fb.clear();
        model.get(fb);
        GL20.glUniformMatrix4fv(modelLoc, false, fb);

        // envia luz
        GL20.glUniform3f(lightLoc, 2.0f, 10.0f, 2.0f);

        // envia cor
        GL20.glUniform3f(colorLoc, 0.5f, 0.5f, 0.5f);

        // Bind da textura
        texture.bind();
        int texLoc = GL20.glGetUniformLocation(shaderProgram, "uTexture");
        GL20.glUniform1i(texLoc, 0); // usa texture unit 0

        // Desenha
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
    }

    @Override
    public void cleanup() {
        GL20.glDeleteProgram(shaderProgram);
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }

    public void init() {
        // 🔹 1. Vértices do cubo unitário em torno de (0,0,0)
        float[] vertices = {
            // --- Trás (-Z)
            -0.5f, -0.5f, -0.5f,   0f, 0f, -1f,   0f, 0f,
            0.5f, -0.5f, -0.5f,   0f, 0f, -1f,   0.5f, 0f,
            0.5f,  0.5f, -0.5f,   0f, 0f, -1f,   0.5f, 0.33333f,
            -0.5f,  0.5f, -0.5f,   0f, 0f, -1f,   0f, 0.333333f,

            // --- Frente (+Z)
            -0.5f, -0.5f,  0.5f,   0f, 0f, 1f,    0.5f, 0f,
            0.5f, -0.5f,  0.5f,   0f, 0f, 1f,    1f, 0f,
            0.5f,  0.5f,  0.5f,   0f, 0f, 1f,    1f, 0.333333f,
            -0.5f,  0.5f,  0.5f,   0f, 0f, 1f,    0.5f, 0.333333f,

            // --- Esquerda (-X)
            -0.5f, -0.5f, -0.5f,  -1f, 0f, 0f,    0f, 0.333333f,
            -0.5f,  0.5f, -0.5f,  -1f, 0f, 0f,    0.5f, 0.333333f,
            -0.5f,  0.5f,  0.5f,  -1f, 0f, 0f,    0.5f, 0.666666f,
            -0.5f, -0.5f,  0.5f,  -1f, 0f, 0f,    0f, 0.666666f,

            // --- Direita (+X)
            0.5f, -0.5f, -0.5f,   1f, 0f, 0f,    0.5f, 0.333333f,
            0.5f,  0.5f, -0.5f,   1f, 0f, 0f,    1f, 0.333333f,
            0.5f,  0.5f,  0.5f,   1f, 0f, 0f,    1f, 0.666666f,
            0.5f, -0.5f,  0.5f,   1f, 0f, 0f,    0.5f, 0.666666f,

            // --- Baixo (-Y)
            -0.5f, -0.5f, -0.5f,   0f, -1f, 0f,   0f, 0.666666f,
            0.5f, -0.5f, -0.5f,   0f, -1f, 0f,   0.5f, 0.666666f,
            0.5f, -0.5f,  0.5f,   0f, -1f, 0f,   0.5f, 1f,
            -0.5f, -0.5f,  0.5f,   0f, -1f, 0f,   0f, 1f,

            // --- Cima (+Y)
            -0.5f,  0.5f, -0.5f,   0f, 1f, 0f,    0.5f, 0.666666f,
            0.5f,  0.5f, -0.5f,   0f, 1f, 0f,    1f, 0.666666f,
            0.5f,  0.5f,  0.5f,   0f, 1f, 0f,    1f, 1f,
            -0.5f,  0.5f,  0.5f,   0f, 1f, 0f,    0.5f, 1f
        };

        // 🔹 2. Índices para formar os 12 triângulos (36 índices)
        int[] indices = {
            0,1,2, 2,3,0,        // trás
            4,5,6, 6,7,4,        // frente
            8,9,10, 10,11,8,     // esquerda
            12,13,14, 14,15,12,  // direita
            16,17,18, 18,19,16,  // baixo
            20,21,22, 22,23,20   // cima
        };

        // Buffers
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        // 🔹 3. VAO
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // VBO (vertices)
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

        // Carregar texture
        texture = new Texture("textures/dice.png");

        // Atributos
        // posição (loc=0), normal (loc=1), UV (loc=2)
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        // EBO (indices)
        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);

        MemoryUtil.memFree(vertexBuffer);
        MemoryUtil.memFree(indexBuffer);
        GL30.glBindVertexArray(0);

        // Carrega shaders
        String vs = ShaderUtils.loadResource("shaders/basic.vert");
        String fs = ShaderUtils.loadResource("shaders/basic.frag");
        shaderProgram = ShaderUtils.createProgram(vs, fs);
        mvpLoc = GL20.glGetUniformLocation(shaderProgram, "uMVP");
        modelLoc = GL20.glGetUniformLocation(shaderProgram, "uModel");
        lightLoc = GL20.glGetUniformLocation(shaderProgram, "lightPos");
        colorLoc = GL20.glGetUniformLocation(shaderProgram, "objectColor");
    }
}