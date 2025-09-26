#version 330 core
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

uniform mat4 uMVP;
uniform mat4 uModel;

out vec3 fragPos;
out vec3 fragNormal;

void main() {
    gl_Position = uMVP * vec4(position, 1.0);
    fragPos = vec3(uModel * vec4(position, 1.0));
    fragNormal = mat3(transpose(inverse(uModel))) * normal;
}