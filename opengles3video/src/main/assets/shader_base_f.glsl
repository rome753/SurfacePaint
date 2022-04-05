#version 300 es
#extension GL_OES_EGL_image_external : require
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec2 aTexCoord;
out vec4 fragColor;

uniform samplerExternalOES texture1;

void main() {

     // 正常画面
     fragColor = texture(texture1, aTexCoord);

}