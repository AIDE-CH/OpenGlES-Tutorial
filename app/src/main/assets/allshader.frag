#version 300 es
precision mediump float;
in vec3 outColor;
in vec2 outTexture;

in vec3 outFragPos;
in vec3 outNormal;

flat in int outHasColor;
flat in int outHasTexture;
flat in int outHasNormal;

out vec4 fragmentColor;

uniform sampler2D texture1;

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
void main()
{
    fragmentColor = vec4(1.0F, 1.0F, 1.0F, 1.0F);
    if(outHasColor > 0) {
        fragmentColor *=  vec4(outColor, 1.0F);
    }
    if(outHasTexture > 0) {
        fragmentColor *=  texture(texture1, outTexture);
    }
    fragmentColor = vec4( vec3(fragmentColor), 1.0F);
    if(outHasNormal > 0){
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

        //    vec3 fLight = ambient+diffuse+(diff*specular);
        vec3 fLight = ambient + diffuse + specular;
        fragmentColor = vec4(fLight, 1)*fragmentColor;
    }
}