#version 300 es
in vec3 a_Position;

out vec3 outNormal;
out vec3 outFragPos;

out vec3 outTexture;

uniform mat4 a_Model;
uniform mat4 a_View;
uniform mat4 a_Projection;

void main()
{
    outFragPos = vec3(a_Model * vec4(a_Position, 1.0));
    vec3 a_Normal = normalize(a_Position);
    outNormal = mat3(transpose(inverse(a_Model))) * a_Normal;

    gl_Position = a_Projection*a_View*a_Model*vec4(a_Position, 1.0f);
    outTexture = normalize(vec3(a_Position.x, a_Position.y, a_Position.z));
}