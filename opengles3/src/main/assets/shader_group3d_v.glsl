#version 300 es
layout (location = 0) in vec3 vPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 vPos;

void main() {
     vPos = (vPosition / 1024.0f - 0.5f) * 2.0f;
     gl_PointSize = 10.0f;
     gl_Position  = projection * view * model * vec4(vPos, 1.0f);
}