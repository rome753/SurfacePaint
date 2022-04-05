#version 300 es
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec2 vTexCoord;
out vec2 aTexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
     gl_Position = projection * view * model * vec4(vPosition, 1.0f);
     aTexCoord = vec4(vTexCoord, 1.0f, 1.0f).xy;
}