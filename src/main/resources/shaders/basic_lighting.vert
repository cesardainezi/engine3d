#version 330 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aTexCoord;

uniform mat4 uMVP;
uniform mat4 uModel;

out vec3 fragNormal;
out vec2 fragTexCoord;

void main()
{
    // Posição do vértice
    gl_Position = uMVP * vec4(aPos, 1.0);

    // Normal transformada para espaço do mundo
    fragNormal = mat3(transpose(inverse(uModel))) * aNormal;

    // Coordenadas de textura repassadas
    fragTexCoord = aTexCoord;
}
