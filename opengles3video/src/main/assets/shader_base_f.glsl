#version 300 es
#extension GL_OES_EGL_image_external : require
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
in vec3 aColor;
in vec2 aTexCoord;
out vec4 fragColor;

uniform samplerExternalOES texture1;

void main() {

     // 正常画面
     fragColor = texture(texture1, aTexCoord);

//     // 反相效果
//     fragColor = 1.0f - texture(texture1, aTexCoord);

//     // 彩色滤镜效果
//     fragColor = mix(texture(texture1, aTexCoord), vec4(aColor, 1.0f), 0.5f);

//     // 灰色效果
//     vec4 c = texture(texture1, aTexCoord);
//     float h = c.r*0.299f + c.g*0.587f + c.b*0.114f;
//     fragColor = vec4(h, h, h, 1.0f);

//    // 二值化效果
//    vec4 c = texture(texture1, aTexCoord);
//    float h = c.r*0.299f + c.g*0.587f + c.b*0.114f;
//    h = h > 0.4f ? 1.0f : 0.0f;
//    fragColor = vec4(h, h, h, 1.0f);

//     // 四分屏效果
//     float s = aTexCoord.s;
//     float t = aTexCoord.t;
//     if (s > 0.5f) {
//          s = s - 0.5f;
//     }
//     if (t > 0.5f) {
//          t = t - 0.5f;
//     }
//     fragColor = texture(texture1, vec2(s, t));

//     // 马赛克效果
//     float s = aTexCoord.s;
//     float t = aTexCoord.t;
//     float msk = 50.0f;
//     s = float(int(s * msk)) / msk;
//     t = float(int(t * msk)) / msk;
//     fragColor = texture(texture1, vec2(s, t));

}