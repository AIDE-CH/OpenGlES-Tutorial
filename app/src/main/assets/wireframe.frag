#version 300 es
precision mediump float;
in vec3 outColor;
out vec4 fragmentColor;
void main()
{
    fragmentColor = vec4(outColor, 1.0f);
}