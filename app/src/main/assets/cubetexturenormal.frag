#version 300 es
precision mediump float;

in vec3 outTexture;

out vec4 fragmentColor;

uniform samplerCube skybox;
void main()
{
    fragmentColor = texture(skybox, outTexture);
}