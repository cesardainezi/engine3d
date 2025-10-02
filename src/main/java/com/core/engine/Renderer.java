package com.core.engine;

import com.core.engine.components.Transform;
import com.core.graphics.Camera;
import com.core.graphics.Window;
import com.core.graphics.material.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.List;

public class Renderer {

    private Vector3f bgColor;
    private FloatBuffer fb = BufferUtils.createFloatBuffer(16);

    public void init() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        bgColor = new Vector3f(0f,0f,0f);
    }

    public void renderObject(GameObject obj, Camera camera, Window window){
        // Configura view/projection globais
        Matrix4f projection = new Matrix4f().perspective(
                (float)Math.toRadians(70.0f),
                (float) window.getWidth() / window.getHeight(),
                0.01f, 1000f
        );
        Matrix4f view = camera.getViewMatrix();

        renderObject(obj, view, projection);
    }

    public void renderScene(List<GameObject> objects, Camera camera, Window window) {
        // Configura view/projection globais
        Matrix4f projection = new Matrix4f().perspective(
                (float)Math.toRadians(70.0f),
                (float) window.getWidth() / window.getHeight(),
                0.01f, 1000f
        );
        Matrix4f view = camera.getViewMatrix();

        for (GameObject obj : objects) {
            renderObject(obj, view, projection);
        }
    }

    private void renderObject(GameObject obj, Matrix4f view, Matrix4f projection) {
        if (obj.getMesh() == null || obj.getMaterial() == null) return;

        Transform transform = obj.getTransform();
        Material material = obj.getMaterial();

        // Usa o material
        material.use();

        // Calcula matrizes
        Matrix4f model = new Matrix4f().identity()
                .translate(transform.position)
                .rotateX(transform.rotation.x)
                .rotateY(transform.rotation.y)
                .rotateZ(transform.rotation.z)
                .scale(transform.scale);

        Matrix4f mvp = new Matrix4f();
        projection.mul(view, mvp).mul(model);

        // Envia uniforms
        mvp.get(fb.clear());
        GL20.glUniformMatrix4fv(material.getMvpLoc(), false, fb);

        model.get(fb.clear());
        GL20.glUniformMatrix4fv(material.getModelLoc(), false, fb);

        GL20.glUniform3f(material.getLightLoc(), 2.0f, 10.0f, 2.0f);

        // Se tiver textura, o shader usa UVs
        // Se não tiver textura, o shader usa só cor
        Vector3f color = material.getColor();
        GL20.glUniform3f(material.getColorLoc(), color.x, color.y, color.z);

        // Renderiza
        obj.getMesh().render();

        material.stop();
    }


    public void setBackgroundColor(float red, float green, float blue){
        bgColor = new Vector3f(red, green, blue);
    }

    public void clear() {
        // Cor de fundo RGBA
        GL11.glClearColor(bgColor.x, bgColor.y, bgColor.z, 1f);

        // Zera buffers de cor e profundidade
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

}