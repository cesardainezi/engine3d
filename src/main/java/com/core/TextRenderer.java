package com.core;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;

public class TextRenderer {

    private int texId;
    private STBTTBakedChar.Buffer cdata;
    private int bitmapW = 512;
    private int bitmapH = 512;
    private float fontSize = 24f;

    private Window window;

    public void init(String fontPath, Window window) throws IOException {
        this.window = window;

        // carrega bytes da fonte TTF
        ByteBuffer ttf;
        try {
            InputStream stream = Objects.requireNonNull(
                    TextRenderer.class.getClassLoader().getResourceAsStream(fontPath),
                    "Fonte não encontrada: " + fontPath
            );

            byte[] bytes = stream.readAllBytes();
            stream.close();

            ttf = BufferUtils.createByteBuffer(bytes.length);
            ttf.put(bytes).flip();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar fonte: " + fontPath, e);
        }

        // cria bitmap
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapW * bitmapH);

        // ASCII 32..126 (95 chars visíveis)
        cdata = STBTTBakedChar.malloc(96);
        stbtt_BakeFontBitmap(ttf, fontSize, bitmap, bitmapW, bitmapH, 32, cdata);

        // cria textura do atlas
        texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapW, bitmapH, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public void drawText(String text, float x, float y) {
        glPushAttrib(GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT | GL_TRANSFORM_BIT);
        glDisable(GL_DEPTH_TEST);

        // Configura ortográfica com base no Window
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, window.getWidth(), window.getHeight(), 0, -1, 1);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        // Renderiza texto
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texId);
        glColor3f(1,1,1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer xBuf = stack.floats(x);
            FloatBuffer yBuf = stack.floats(y);
            STBTTAlignedQuad q = STBTTAlignedQuad.malloc(stack);

            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texId);
            glColor3f(1f, 1f, 1f);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glBegin(GL_QUADS);
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c < 32 || c > 126) continue;
                stbtt_GetBakedQuad(cdata, bitmapW, bitmapH, c - 32, xBuf, yBuf, q, true);

                glTexCoord2f(q.s0(), q.t0()); glVertex2f(q.x0(), q.y0());
                glTexCoord2f(q.s1(), q.t0()); glVertex2f(q.x1(), q.y0());
                glTexCoord2f(q.s1(), q.t1()); glVertex2f(q.x1(), q.y1());
                glTexCoord2f(q.s0(), q.t1()); glVertex2f(q.x0(), q.y1());
            }
            glEnd();
        }

        // restaura matrizes
        glPopMatrix(); // MODELVIEW
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);

        glPopAttrib();
    }

    public void cleanup() {
        glDeleteTextures(texId);
        cdata.free();
    }
}