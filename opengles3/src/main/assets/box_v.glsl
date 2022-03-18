#version 300 es
layout (location = 0) in vec3 vPosition;
layout (location = 1) in float vColor;
out vec4 aColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
     gl_Position  = projection * view * model * vec4(vPosition, 1.0);
//     aColor = vColor;
}