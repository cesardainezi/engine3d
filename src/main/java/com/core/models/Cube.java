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
    public void cleanup() {
        mesh.cleanup();
        material.cleanup();
    }
}
