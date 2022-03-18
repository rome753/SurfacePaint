#version 300 es
layout (location = 0) in vec2 vPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 vPos;

void main() {
     vPos = (vPosition / 1024.0f - 0.5f) * 2.0f;
//     vPos = (vPosition / 1024.0f);

     gl_PointSize = 20.0f;
     float z = vPos.y;
     if (z < 0.0f) {
          z = -z;
     }
     gl_Position  = projection * view * model * vec4(vPos, z, 1.0f);
}