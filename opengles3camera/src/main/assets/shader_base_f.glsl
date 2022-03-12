#version 300 es
precision mediump float;
in vec3 aColor;
in vec2 aTexCoord;
out vec4 fragColor;

uniform sampler2D texture1;

void main() {
     fragColor = mix(texture(texture1, aTexCoord), vec4(aColor, 1.0f), 0.5f);
}