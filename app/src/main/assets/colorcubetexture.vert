#version 300 es
in vec3 a_Position;

out vec3 outTexture;

uniform mat4 a_Model;
uniform mat4 a_View;
uniform mat4 a_Projection;

void main()
{
    vec4 pos = a_Projection*a_View*a_Model*vec4(a_Position, 1.0f);
    gl_Position = vec4(pos.x, pos.y, pos.w, pos.w);

    outTexture = normalize(vec3(a_Position.x, a_Position.y, -a_Position.z));
}