#version 300 es
const int[] colors = int[](-16777216,-16318464,-15859712,-15400960,-14876672,-14417920,-13959168,-13500416,-12976128,-12517376,-12058624,-11599872,-11075584,-10158080,-9240576,-8323072,-7405568,-6488064,-5570560,-4653056,-3735552,-2818048,-1900544,-983040,-65536,-61440,-57088,-52736,-48640,-44288,-39936,-35840,-31488,-27136,-23040,-18688,-14336,-13312,-12032,-11008,-9728,-8704,-7424,-6144,-5120,-3840,-2816,-1536,-256,-235,-214,-193,-171,-150,-129,-108,-86,-65,-44,-23,-1,-1,-1,-1);
precision mediump float;
in vec3 aColor;
in vec2 aTexCoord;
out vec4 fragColor;

uniform sampler2D tex0;
uniform sampler2D tex1;

vec3 generateColor(int k) {
    int c = colors[k];
    int r = (c >> 16) & 0xff;
    int g = (c >> 8) & 0xff;
    int b = c & 0xff;
    return vec3(r,g,b) / 255.0;
}

void main() {

    float y0 = texture(tex0, aTexCoord).r;
    float y1 = texture(tex1, aTexCoord).r;
//    int k = int((y1 - y0) * 255.0);
//    vec3 color = generateColor(k);
//    fragColor = vec4(color, 1.0);


    float k = y1 - y0;
    fragColor = vec4(k,k,k,1.0);
}