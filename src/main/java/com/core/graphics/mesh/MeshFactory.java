package com.core.graphics.mesh;

public class MeshFactory {

    public static Mesh createCube(float width, float height, float depth) {
        float w = width / 2f;
        float h = height;
        float d = depth / 2f;

        // --- Vertices (posição, normal, UV) ---
        float[] vertices = {
            // Frente (+Z)
            -w, 0f,  d,   0, 0, 1,   0, 0,
            w, 0f,  d,   0, 0, 1,   1, 0,
            w,  h,  d,   0, 0, 1,   1, 1,
            -w,  h,  d,   0, 0, 1,   0, 1,

            // Trás (-Z)
            -w, 0f, -d,   0, 0, -1,  1, 0,
            w, 0f, -d,   0, 0, -1,  0, 0,
            w,  h, -d,   0, 0, -1,  0, 1,
            -w,  h, -d,   0, 0, -1,  1, 1,

            // Direita (+X)
            w, 0f, -d,   1, 0, 0,   0, 0,
            w, 0f,  d,   1, 0, 0,   1, 0,
            w,  h,  d,   1, 0, 0,   1, 1,
            w,  h, -d,   1, 0, 0,   0, 1,

            // Esquerda (-X)
            -w, 0f, -d,  -1, 0, 0,   1, 0,
            -w, 0f,  d,  -1, 0, 0,   0, 0,
            -w,  h,  d,  -1, 0, 0,   0, 1,
            -w,  h, -d,  -1, 0, 0,   1, 1,

            // Topo (+Y)
            -w,  h, -d,   0, 1, 0,   0, 0,
            w,  h, -d,   0, 1, 0,   1, 0,
            w,  h,  d,   0, 1, 0,   1, 1,
            -w,  h,  d,   0, 1, 0,   0, 1,

            // Base (-Y)
            -w, 0f, -d,   0, -1, 0,  0, 1,
            w, 0f, -d,   0, -1, 0,  1, 1,
            w, 0f,  d,   0, -1, 0,  1, 0,
            -w, 0f,  d,   0, -1, 0,  0, 0
        };

        // --- Índices ---
        int[] indices = {
            0, 1, 2, 2, 3, 0,       // frente
            4, 5, 6, 6, 7, 4,       // trás
            8, 9,10,10,11, 8,       // direita
            12,13,14,14,15,12,       // esquerda
            16,17,18,18,19,16,       // topo
            20,21,22,22,23,20        // base
        };

        return new Mesh(vertices, indices);
    }

}
