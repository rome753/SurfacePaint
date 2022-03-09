#version 300 es
layout (location = 0) in vec2 vPosition;

void main() {
     gl_PointSize = 20.0f;
     gl_Position  = vec4(vPosition / 10.0f, 0.0f, 1.0f);
}