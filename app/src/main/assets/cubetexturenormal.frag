#version 300 es
precision mediump float;

in vec3 outFragPos;
in vec3 outNormal;
in vec3 outTexture;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};
struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};
uniform vec3 cameraPosition;
uniform Material material;
uniform Light light;

out vec4 fragmentColor;

uniform samplerCube skybox;
void main()
{
    fragmentColor = texture(skybox, outTexture);
    // ambient
    vec3 ambient = light.ambient * material.ambient;
    // diffuse
    vec3 norm = normalize(outNormal);
    vec3 lightDir = normalize(light.position - outFragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * (diff * material.diffuse);
    // specular
    vec3 viewDir = normalize(cameraPosition - outFragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * (spec * material.specular);

    vec3 phongOut = ambient + diffuse + specular;
    fragmentColor = fragmentColor * vec4(phongOut, 1);
}