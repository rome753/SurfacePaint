#version 300 es
layout (location = 0) in vec3 vPosition;
layout (location = 1) in vec3 vColor;
layout (location = 2) in vec2 vTexCoord;
out vec3 aColor;
out vec2 aTexCoord;

uniform mat4 transform;

void main() {
     gl_Position  = transform * vec4(vPosition + 1.0f, 1.0f);
     aColor = vColor;
     aTexCoord = vTexCoord;
}