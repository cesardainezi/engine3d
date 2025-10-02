package com.core.graphics.material;

import com.core.graphics.Texture;
import com.core.engine.ShaderUtils;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class Material {
    private int shaderProgram;
    private Texture texture;
    private Vector3f color;

    private int mvpLoc, modelLoc, lightLoc, colorLoc;

    public Material(String vertexShaderPath, String fragShaderPath, String texturePath) {
        String vs = ShaderUtils.loadResource(vertexShaderPath);
        String fs = ShaderUtils.loadResource(fragShaderPath);
        shaderProgram = ShaderUtils.createProgram(vs, fs);

        if(texturePath != null)
            texture = new Texture(texturePath);
        else
            texture = null;

        mvpLoc   = GL20.glGetUniformLocation(shaderProgram, "uMVP");
        modelLoc = GL20.glGetUniformLocation(shaderProgram, "uModel");
        lightLoc = GL20.glGetUniformLocation(shaderProgram, "lightPos");
        colorLoc = GL20.glGetUniformLocation(shaderProgram, "objectColor");
        color = new Vector3f(1f, 1f, 1f);
    }

    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }

    public Vector3f getColor() {
        return color;
    }

    public void use() {
        GL20.glUseProgram(shaderProgram);

        // informa ao shader se deve usar textura ou não
        int useTexLoc = GL20.glGetUniformLocation(shaderProgram, "useTexture");

        if (texture != null) {
            texture.bind();
            GL20.glUniform1i(GL20.glGetUniformLocation(shaderProgram, "uTexture"), 0);
            GL20.glUniform1i(useTexLoc, 1);
        } else {
            GL20.glUniform1i(useTexLoc, 0);
        }
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public int getMvpLoc()   { return mvpLoc; }
    public int getModelLoc() { return modelLoc; }
    public int getLightLoc() { return lightLoc; }
    public int getColorLoc() { return colorLoc; }

    public void cleanup() {
        GL20.glDeleteProgram(shaderProgram);

        if (texture != null) {
            texture.cleanup();
        }
    }
}
