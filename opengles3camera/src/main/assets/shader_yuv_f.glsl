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

//     float y = texture(tex0, aTexCoord).r;
//     float u = texture(tex1, aTexCoord).r - 0.5;
//     float v = texture(tex2, aTexCoord).r - 0.5;
//
//     float r = y + 1.403 * v;
//     float g = y - 0.344 * u - 0.714 * v;
//     float b = y + 1.770 * u;
//
//     fragColor = vec4(r, g, b, 1.0f);

}