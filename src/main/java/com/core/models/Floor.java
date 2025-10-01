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
    public void render(Camera camera, Window window) {
        material.use();

        // Matriz Model/View/Projection usando Transform
        Matrix4f model = transform.toMatrix();
        Matrix4f projection = new Matrix4f().perspective(
                (float)Math.toRadians(70.0f),
                (float) window.getWidth() / window.getHeight(),
                0.01f, 100.0f
        );

        Matrix4f mvp = new Matrix4f();
        projection.mul(camera.getViewMatrix(), mvp).mul(model);

        // Enviar uniforms
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        mvp.get(fb);
        GL20.glUniformMatrix4fv(material.getMvpLoc(), false, fb);

        fb.clear();
        model.get(fb);
        GL20.glUniformMatrix4fv(material.getModelLoc(), false, fb);

        GL20.glUniform3f(material.getLightLoc(), 2.0f, 5.0f, 2.0f);
        GL20.glUniform3f(material.getColorLoc(), 0.8f, 0.8f, 0.8f); // cinza claro

        mesh.render();
        material.stop();
    }

    @Override
    public void cleanup() {
        mesh.cleanup();
        material.cleanup();
    }
}
