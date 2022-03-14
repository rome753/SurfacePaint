#version 300 es
precision mediump float;
in vec3 aColor;
out vec4 fragColor;

void main() {
     if (gl_FrontFacing) {
          fragColor = vec4(aColor, 1.0);
     } else {
          discard;
     }
}