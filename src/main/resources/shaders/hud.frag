#version 330 core
in vec2 TexCoord;
out vec4 FragColor;

// cor fixa, depois expandimos para texto/textura
uniform vec3 uColor;

void main() {
    FragColor = vec4(uColor, 1.0);
}