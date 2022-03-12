#version 300 es
#extension GL_OES_EGL_image_external : require
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec3 aColor;
in vec2 aTexCoord;
out vec4 fragColor;

uniform samplerExternalOES texture1;

void main() {
     fragColor = mix(texture(texture1, aTexCoord), vec4(aColor, 1.0f), 0.2f);
}