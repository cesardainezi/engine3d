package com.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Player extends GameObject {
    private int vaoId, vboId, eboId, shaderProgram;
    private Vector3f position;
    private Vector3f size;
    private float yaw = -90.0f; // olhando para -Z por padrão
    private float pitch = 0.0f;

    private Vector3f velocity = new Vector3f();  // velocidade atual
    private boolean onGround = true;             // se está encostado no chão
    private int numberOfJumps;

    private final float gravity = 9.8f;
    private final float jumpStrength = 5.0f;     // velocidade inicial para o pulo

    public Player(Vector3f startPos) {
        this.position = startPos;
        this.size = new Vector3f(0.8f, 2f, 0.8f);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f pos) {
        this.position.set(pos);
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f vel) {
        this.velocity.set(vel);
    }

    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }


    public void init() {
        numberOfJumps = 0;

        float[] vertices = {
                // ---- Frente (0,0,-1)
                -0.4f, 0f, -0.4f,   0f, 0f, -1f,
                0.4f, 0f, -0.4f,   0f, 0f, -1f,
                0.4f, 2f, -0.4f,   0f, 0f, -1f,
                -0.4f, 2f, -0.4f,   0f, 0f, -1f,

                // ---- Trás (0,0,1)
                -0.4f, 0f,  0.4f,   0f, 0f, 1f,
                0.4f, 0f,  0.4f,   0f, 0f, 1f,
                0.4f, 2f,  0.4f,   0f, 0f, 1f,
                -0.4f, 2f,  0.4f,   0f, 0f, 1f,

                // ---- Direita (1,0,0)
                0.4f, 0f, -0.4f,   1f, 0f, 0f,
                0.4f, 0f,  0.4f,   1f, 0f, 0f,
                0.4f, 2f,  0.4f,   1f, 0f, 0f,
                0.4f, 2f, -0.4f,   1f, 0f, 0f,

                // ---- Esquerda (-1,0,0)
                -0.4f, 0f, -0.4f,  -1f, 0f, 0f,
                -0.4f, 0f,  0.4f,  -1f, 0f, 0f,
                -0.4f, 2f,  0.4f,  -1f, 0f, 0f,
                -0.4f, 2f, -0.4f,  -1f, 0f, 0f,

                // ---- Topo (0,1,0)
                -0.4f, 2f, -0.4f,   0f, 1f, 0f,
                0.4f, 2f, -0.4f,   0f, 1f, 0f,
                0.4f, 2f,  0.4f,   0f, 1f, 0f,
                -0.4f, 2f,  0.4f,   0f, 1f, 0f,

                // ---- Base (0,-1,0)
                -0.4f, 0f, -0.4f,   0f, -1f, 0f,
                0.4f, 0f, -0.4f,   0f, -1f, 0f,
                0.4f, 0f,  0.4f,   0f, -1f, 0f,
                -0.4f, 0f,  0.4f,   0f, -1f, 0f
        };

        int[] indices = {
                // frente
                0,1,2, 2,3,0,
                // tras
                4,5,6, 6,7,4,
                // direita
                8,9,10, 10,11,8,
                // esquerda
                12,13,14, 14,15,12,
                // topo
                16,17,18, 18,19,16,
                // base
                20,21,22, 22,23,20
        };

        // Buffers
        FloatBuffer vb = BufferUtils.createFloatBuffer(vertices.length);
        vb.put(vertices).flip();
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length);
        ib.put(indices).flip();

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vb, GL15.GL_STATIC_DRAW);

        eboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ib, GL15.GL_STATIC_DRAW);

        int stride = 6 * Float.BYTES; // 3 pos + 3 normal
        GL20.glVertexAttribPointer(0,3,GL11.GL_FLOAT,false,stride,0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1,3,GL11.GL_FLOAT,false,stride,3*Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);

        // Usa shader de iluminação difusa
        String vs = ShaderUtils.loadResource("shaders/basic_lighting.vert");
        String fs = ShaderUtils.loadResource("shaders/basic_lighting.frag");
        shaderProgram = ShaderUtils.createProgram(vs, fs);


    }

    @Override
    public void update(double dt) {
        // no update:
        Vector3f wishDir = new Vector3f();

        float delta = (float) dt;

        // --- Movimento horizontal ---
        // já definido em processKeyboard: velocity.x e velocity.z foram ajustados
        position.x += velocity.x * delta;
        position.z += velocity.z * delta;

        // --- Movimento vertical / gravidade ---
        if (numberOfJumps > 0) {
            velocity.y -= gravity * delta; // aceleração para baixo
            position.y += velocity.y * delta;

            if (position.y <= 0f) { // tocou o chão?
                position.y = 0f;
                velocity.y = 0f;
                numberOfJumps = 0;
            }
        }
    }

    @Override
    public void render(Camera camera, Window window) {
        GL20.glUseProgram(shaderProgram);

        // 🟢 Rotaciona o cubo no eixo Y baseado no Yaw
        Matrix4f model = new Matrix4f()
                .identity()
                .translate(position)
                .rotateY((float) Math.toRadians(yaw));

        Matrix4f projection = new Matrix4f()
                .perspective((float)Math.toRadians(70.0f),
                        (float) window.getWidth() / window.getHeight(),
                        0.01f, 100.0f);

        Matrix4f mvp = new Matrix4f();
        projection.mul(camera.getViewMatrix(), mvp);
        mvp.mul(model);

        int mvpLoc = GL20.glGetUniformLocation(shaderProgram, "uMVP");
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);
        GL20.glUniformMatrix4fv(mvpLoc, false, fb);

        int modelLoc = GL20.glGetUniformLocation(shaderProgram, "uModel");
        fb.clear();
        model.get(fb);
        GL20.glUniformMatrix4fv(modelLoc, false, fb);

        // luz
        int lightLoc = GL20.glGetUniformLocation(shaderProgram, "lightPos");
        GL20.glUniform3f(lightLoc, 2f, 5f, 2f);

        // cor
        int colorLoc = GL20.glGetUniformLocation(shaderProgram, "objectColor");
        GL20.glUniform3f(colorLoc, 0f, 1f, 0f);

        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);

        GL20.glUseProgram(0);
    }

    public void processKeyboard(int key, double dt, float speed) {

        Vector3f forward = new Vector3f(
                (float) Math.cos(Math.toRadians(yaw)),
                0,
                (float) Math.sin(Math.toRadians(yaw))
        ).normalize();

        Vector3f right = new Vector3f(forward).cross(0,1,0).normalize();

        if (key == GLFW.GLFW_KEY_W) velocity.add(new Vector3f(forward).mul(speed));
        if (key == GLFW.GLFW_KEY_S) velocity.add(new Vector3f(forward).mul(-speed));
        if (key == GLFW.GLFW_KEY_A) velocity.add(new Vector3f(right).mul(-speed));
        if (key == GLFW.GLFW_KEY_D) velocity.add(new Vector3f(right).mul(speed));
    }

    public void jump() {
        if (numberOfJumps < 2) {
            velocity.y = jumpStrength;
            numberOfJumps++;
        }
    }

    @Override
    public void cleanup() {
        GL20.glDeleteProgram(shaderProgram);
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(eboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}