package com.core.models;

import com.core.engine.GameObject;
import com.core.engine.components.Transform;
import com.core.graphics.Camera;
import com.core.graphics.Window;
import com.core.graphics.mesh.Mesh;
import com.core.graphics.material.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class Cube extends GameObject {
    private float rotation = 0.0f;
    private float rotationRate = 0f;

    public Cube(Vector3f position, float rotationRate) {
        this.transform = new Transform(position);
        this.rotationRate = rotationRate;

        // 🔹 Vertices + Indices como antes
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

        this.mesh = new Mesh(vertices, indices);
        this.material = new Material("shaders/basic.vert", "shaders/basic.frag", "textures/dice.png");
    }

    @Override
    public void update(double dt) {
        rotation += (float) (rotationRate * dt);
        transform.rotation.y = rotation;
        transform.rotation.x = rotation * 0.5f;
    }

    @Override
    public void render(Camera camera, Window window) {
        material.use();

        // Matriz Model/View/Projection
        Matrix4f model = new Matrix4f().identity()
                .translate(transform.position)
                .rotateX(transform.rotation.x)
                .rotateY(transform.rotation.y)
                .scale(transform.scale);

        Matrix4f view = camera.getViewMatrix();
        Matrix4f projection = new Matrix4f().perspective(
                (float)Math.toRadians(70.0f),
                (float) window.getWidth() / window.getHeight(),
                0.01f, 100.0f);

        Matrix4f mvp = new Matrix4f();
        projection.mul(view, mvp).mul(model);

        // Enviar uniforms
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);
        GL20.glUniformMatrix4fv(material.getMvpLoc(), false, fb);

        fb.clear();
        model.get(fb);
        GL20.glUniformMatrix4fv(material.getModelLoc(), false, fb);

        GL20.glUniform3f(material.getLightLoc(), 2.0f, 10.0f, 2.0f);
        GL20.glUniform3f(material.getColorLoc(), 0.5f, 0.5f, 0.5f);

        // Renderiza mesh
        mesh.render();

        material.stop();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        material.cleanup();
    }
}
