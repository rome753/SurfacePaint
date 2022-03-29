#version 300 es
layout (location = 0) in vec2 vPosition;
layout (location = 1) in vec2 vVelocity;

uniform vec3 time;
uniform vec2 center;

out vec2 vPos;
out vec2 vVel;

void main() {
     vPos = (vPosition - 0.5) * 2.0;
     vVel = vVelocity - 0.5;
     gl_PointSize = 5.0;

     float cx = time.g;
     float cy = time.b;
     float dx = (cx - vPos.x) * time.r;
     float dy = (cy - vPos.y) * time.r;

     float x = vPos.x + vVel.x * time.r + dx;
     float y = vPos.y + vVel.y * time.r + dy;

     gl_Position  = vec4(x, y, 0.0, 1.0);
}