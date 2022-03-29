#version 300 es
layout (location = 0) in vec2 vPosition;
layout (location = 1) in vec2 vVelocity;

uniform float time;
out vec2 vPos;
out vec2 vVel;

void main() {
     vPos = (vPosition - 0.5) * 2.0;
     vVel = vVelocity - 0.5;
     gl_PointSize = 5.0;

     float x = vPos.x + vVel.x * time;
     float y = vPos.y + vVel.y * time;
     gl_Position  = vec4(x, y, 0.0, 1.0);
}