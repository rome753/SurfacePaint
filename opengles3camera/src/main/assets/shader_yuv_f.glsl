#version 300 es

precision mediump float;
in vec3 aColor;
in vec2 aTexCoord;
out vec4 fragColor;

uniform sampler2D tex0;
uniform sampler2D tex1;
uniform sampler2D tex2;

void main() {

     // 正常画面
     fragColor = texture(tex0, aTexCoord);


}