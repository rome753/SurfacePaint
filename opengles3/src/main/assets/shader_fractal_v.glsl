#version 300 es
precision lowp float;
// 热度渐变色 64
const int[] colors = int[](-16777216,-16318464,-15859712,-15400960,-14876672,-14417920,-13959168,-13500416,-12976128,-12517376,-12058624,-11599872,-11075584,-10158080,-9240576,-8323072,-7405568,-6488064,-5570560,-4653056,-3735552,-2818048,-1900544,-983040,-65536,-61440,-57088,-52736,-48640,-44288,-39936,-35840,-31488,-27136,-23040,-18688,-14336,-13312,-12032,-11008,-9728,-8704,-7424,-6144,-5120,-3840,-2816,-1536,-256,-235,-214,-193,-171,-150,-129,-108,-86,-65,-44,-23,-1,-1,-1,-1);
//layout (location = 0) in vec2 vPosition;

uniform int w;
uniform vec2 c;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 aColor;

vec3 generateColor(int k) {
    int c = colors[k];
    int r = (c >> 16) & 0xff;
    int g = (c >> 8) & 0xff;
    int b = c & 0xff;
    return vec3(r,g,b) / 255.0;
}

vec2 mul(vec2 z, vec2 c){
    return vec2(z.x * c.x - z.y * c.y, z.y * c.x + z.x * c.y);
}

void main() {
    float fw = float(w);
    int x = gl_VertexID % w;
    int y = gl_VertexID / w;

    vec2 pos = vec2(x, y) / fw - 0.5;
    vec2 z = pos * 2.0;

    int k = 0;
    for (; k < 63; k++) {
        if (z.x * z.x + z.y * z.y > 4.0) break;
        z = mul(z,z) + c;
    }

    aColor = generateColor(k);
    gl_Position  = projection * view * model * vec4(pos, float(k) / 100.0, 1.0);
}
