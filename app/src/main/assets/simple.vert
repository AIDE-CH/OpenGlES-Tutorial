#version 300 es
in vec3 a_Position;
out vec3 outColor;
uniform vec3 a_Color;

void main()
{
    gl_Position = vec4(a_Position, 1.0f);
    outColor = a_Color;
}