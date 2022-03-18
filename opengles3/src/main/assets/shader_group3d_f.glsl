#version 300 es
precision mediump float;

in vec3 vPos;

out vec4 fragColor;

void main() {
     fragColor = vec4((vPos + 1.0f) * 0.5f, 1.0f);
}