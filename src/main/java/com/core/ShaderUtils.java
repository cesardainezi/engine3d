package com.core;

import org.lwjgl.opengl.GL20;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class ShaderUtils {

    public static String loadResource(String resourceName) {
        try {
            InputStream stream = Objects.requireNonNull(
                    ShaderUtils.class.getClassLoader().getResourceAsStream(resourceName),
                    "Recurso não encontrado: " + resourceName
            );

            try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8)) {
                return scanner.useDelimiter("\\A").next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar shader: " + resourceName, e);
        }
    }

    public static int createShader(String source, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Erro ao compilar shader: " + GL20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public static int createProgram(String vertexSrc, String fragmentSrc) {
        int vertexShader   = createShader(vertexSrc, GL20.GL_VERTEX_SHADER);
        int fragmentShader = createShader(fragmentSrc, GL20.GL_FRAGMENT_SHADER);

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Erro ao linkar programa: " + GL20.glGetProgramInfoLog(program));
        }

        // shaders já podem ser deletados após linkar
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        return program;
    }
}