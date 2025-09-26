#version 330 core
in vec3 fragPos;
in vec3 fragNormal;
in vec2 fragUV;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform vec3 lightPos;

void main() {
    // Normal e direção da luz
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos - fragPos);

    // Lambert: intensidade
    float diff = dot(norm, lightDir);

    // Cor base da textura
    vec3 texColor = texture(uTexture, fragUV).rgb;

    // Difusa final
    vec3 diffuse = (0.5 + 0.5*diff) * texColor;

    FragColor = vec4(diffuse, 1.0);
}