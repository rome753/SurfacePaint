#version 300 es
layout (location = 0) in vec2 vPosition;

out vec2 vPos;

void main() {
     vPos = (vPosition / 1024.0f - 0.5f) * 2.0f;
     gl_PointSize = 2.0f;
     gl_Position  = vec4(vPos, 0.0f, 1.0f);
}