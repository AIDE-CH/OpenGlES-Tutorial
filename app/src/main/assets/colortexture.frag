#version 300 es
precision mediump float;
in vec3 outColor;
in vec2 outTexture;
flat in int outHasColor;
flat in int outHasTexture;

out vec4 fragmentColor;

uniform sampler2D texture1;
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
}