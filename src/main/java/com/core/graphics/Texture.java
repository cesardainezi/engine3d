package com.core.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import org.lwjgl.BufferUtils;

public class Texture {
    private int id;

    public Texture(String resourceName) {
        ByteBuffer imageBuffer;
        try {
            // Carrega como recurso do classpath (resources/)
            InputStream stream = Objects.requireNonNull(
                    Texture.class.getClassLoader().getResourceAsStream(resourceName),
                    "Recurso não encontrado: " + resourceName
            );

            byte[] bytes = stream.readAllBytes();
            stream.close();
            imageBuffer = BufferUtils.createByteBuffer(bytes.length);
            imageBuffer.put(bytes).flip();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar recurso: " + resourceName, e);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = STBImage.stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (data == null) {
                throw new RuntimeException("Falha ao decodificar textura: " + STBImage.stbi_failure_reason());
            }

            id = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            // Filtros
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            // Wraps
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

            // Upload para GPU
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
                    w.get(0), h.get(0), 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            STBImage.stbi_image_free(data);
        }
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void cleanup() {
        GL11.glDeleteTextures(id);
    }
}