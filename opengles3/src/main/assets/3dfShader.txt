#version 300 es
precision mediump float;
in vec2 aTexCoord;
out vec4 fragColor;

uniform sampler2D texture1;
uniform sampler2D texture2;

void main() {
     fragColor = mix(texture(texture1, aTexCoord), texture(texture2, aTexCoord), 0.2);
}