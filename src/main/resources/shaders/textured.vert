#version 330 core
layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

uniform mat4 uMVP;

out vec2 fragUV;

void main() {
    gl_Position = uMVP * vec4(position, 1.0);
    fragUV = texCoord;
}