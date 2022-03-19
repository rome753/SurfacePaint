#version 300 es
layout (location = 0) in vec3 vPosition;
layout (location = 1) in float vColor;
out vec4 aColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
     gl_Position  = projection * view * model * vec4(vPosition, 1.0);
     int mod = gl_VertexID % 4;
     if (mod == 0) {
          aColor = vec4(1.0f, 0.0f, 0.0f, 0.15f);
     } else if (mod == 1) {
          aColor = vec4(0.0f, 1.0f, 0.0f, 0.15f);
     } else if (mod == 2) {
          aColor = vec4(0.0f, 0.0f, 1.0f, 0.15f);
     } else if (mod == 3) {
          aColor = vec4(1.0f, 1.0f, 1.0f, 0.15f);
     }
}