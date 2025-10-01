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

public class Floor extends GameObject {

    public Floor() {
        this.transform = new Transform(new Vector3f(0, 0, 0));

        float size = 25f;      // metade do tamanho do chão
        float texScale = 1.0f; // repetição da textura

        float[] vertices = {
                -size, 0f, -size,   0, 1, 0,   0,                   2 * size * texScale,
                size, 0f, -size,   0, 1, 0,   2 * size * texScale, 2 * size * texScale,
                size, 0f,  size,   0, 1, 0,   2 * size * texScale, 0,
                -size, 0f,  size,   0, 1, 0,   0,                   0
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        this.mesh = new Mesh(vertices, indices);
        this.material = new Material("shaders/basic.vert", "shaders/basic.frag", "textures/grass.png");
    }

    @Override
    public void update(double dt) {
        // O chão não precisa atualizar nada
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        material.cleanup();
    }
}
