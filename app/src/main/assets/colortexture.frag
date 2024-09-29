#version 300 es
precision mediump float;
in vec3 outColor;
in vec2 outTexture;
out vec4 fragmentColor;

uniform sampler2D texture1;
void main()
{
    fragmentColor =  texture(texture1, outTexture) * vec4(outColor, 1.0F);
}