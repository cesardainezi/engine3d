uniform sampler2D uTexture;
uniform vec3 objectColor;
uniform bool useTexture;

in vec2 fragTexCoord;
in vec3 fragNormal;

out vec4 fragColor;

void main() {
    vec3 baseColor = objectColor;

    if (useTexture) {
        baseColor = texture(uTexture, fragTexCoord).rgb;
    }

    // Iluminação simples (ambient + diffuse)

    vec3 lightDir = normalize(vec3(2.0, 10.0, 2.0));
    float diff = max(dot(fragNormal, lightDir), 0.0);

    vec3 finalColor = baseColor * (0.2 + 0.8 * diff);
    fragColor = vec4(finalColor, 1.0);
}
