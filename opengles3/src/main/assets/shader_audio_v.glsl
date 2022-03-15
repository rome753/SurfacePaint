#version 300 es
// 热度渐变色 64
const int[] colors = int[](-16777216,-16318464,-15859712,-15400960,-14876672,-14417920,-13959168,-13500416,-12976128,-12517376,-12058624,-11599872,-11075584,-10158080,-9240576,-8323072,-7405568,-6488064,-5570560,-4653056,-3735552,-2818048,-1900544,-983040,-65536,-61440,-57088,-52736,-48640,-44288,-39936,-35840,-31488,-27136,-23040,-18688,-14336,-13312,-12032,-11008,-9728,-8704,-7424,-6144,-5120,-3840,-2816,-1536,-256,-235,-214,-193,-171,-150,-129,-108,-86,-65,-44,-23,-1,-1,-1,-1);
layout (location = 0) in int vPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform int lineNum;
uniform int w;
uniform int h;

out vec3 aColor;

vec3 generateColor(int k) {
    int c = colors[k];
    int r = (c >> 16) & 0xff;
    int g = (c >> 8) & 0xff;
    int b = c & 0xff;
    return vec3(float(r)/255.0f, float(g)/255.0f, float(b)/255.0f);
}

void main() {
    // gl_PointSize = 6.0;
    float fw = float(w);
    int y = gl_VertexID / w;
    int x = gl_VertexID % w;
    float fx = float(x) / fw - 0.5f;
    float fy = 0.5f - float(y) / fw;

    int h = vPosition;

    // 128 ~ 160
    int a = (h >> 24) & 0xff;
//    int r = (h >> 16) & 0xff;
//    int g = (h >> 8) & 0xff;
//    int b = h & 0xff;


    h = a - 128;
    if (h < 0) {
        h = 0;
    }

    int k = h > 31 ? 63 : h * 2;
    aColor = generateColor(k);
    gl_Position  = projection * view * model * vec4(fx, fy, float(h) / 256.0f, 1.0f);





//    // 频谱
//    h = a;
//    if (h == 255) {
//        h = 0;
//    }
//
//    int k = h > 16 ? 63 : h * 4;
//    aColor = generateColor(k);
//    gl_Position  = projection * view * model * vec4(fx, fy, float(h) / 1280.0f, 1.0f);





//    // 检查数值范围
//    if (a < 96) {
//        aColor = vec3(1.0f, 0.0f, 0.0f);
//    } else if (a > 160) {
//        aColor = vec3(0.0f, 1.0f, 0.0f);
//    } else {
//        aColor = vec3(0.0f, 0.0f, 0.0f);
//    }
//    gl_Position  = projection * view * model * vec4(fx, fy, 0.0f / 10.0f, 1.0f);
}
