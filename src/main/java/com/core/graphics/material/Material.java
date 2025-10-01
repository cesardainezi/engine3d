package com.core.graphics.material;

import com.core.graphics.Texture;
import com.core.engine.ShaderUtils;
import org.lwjgl.opengl.GL20;

public class Material {
    private int shaderProgram;
    private Texture texture;

    private int mvpLoc, modelLoc, lightLoc, colorLoc;

    public Material(String vertexShaderPath, String fragShaderPath, String texturePath) {
        String vs = ShaderUtils.loadResource(vertexShaderPath);
        String fs = ShaderUtils.loadResource(fragShaderPath);
        shaderProgram = ShaderUtils.createProgram(vs, fs);

        texture = new Texture(texturePath);

        mvpLoc   = GL20.glGetUniformLocation(shaderProgram, "uMVP");
        modelLoc = GL20.glGetUniformLocation(shaderProgram, "uModel");
        lightLoc = GL20.glGetUniformLocation(shaderProgram, "lightPos");
        colorLoc = GL20.glGetUniformLocation(shaderProgram, "objectColor");
    }

    public void use() {
        GL20.glUseProgram(shaderProgram);
        texture.bind();
        GL20.glUniform1i(GL20.glGetUniformLocation(shaderProgram, "uTexture"), 0);
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
        texture.cleanup();
    }
}
