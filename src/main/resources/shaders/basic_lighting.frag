#version 330 core
in vec3 fragPos;
in vec3 fragNormal;
out vec4 FragColor;

uniform vec3 lightPos;
uniform vec3 objectColor;

void main() {
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos - fragPos);

    float diff = max(dot(norm, lightDir), 0.0);

    vec3 diffuse = (0.5 + 0.5*diff) * objectColor;
    FragColor = vec4(diffuse, 1.0);
}