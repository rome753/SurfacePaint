#version 300 es
layout (location = 0) in vec2 vPosition;
layout (location = 1) in vec2 vVelocity;

out vec2 vPos;
out vec2 vVel;

uniform vec3 time;
uniform vec2 center;


void main() {
     vPos = vPosition + 0.01;
     vVel = vVelocity;
     gl_PointSize = 25.0;
     gl_Position  = vec4(vPos, 0.0, 1.0);

//     float cx = time.g;
//     float cy = time.b;
//     float dx = (cx - vPos.x) * time.r;
//     float dy = (cy - vPos.y) * time.r;
//
//     float x = vPos.x + vVel.x * time.r + dx;
//     float y = vPos.y + vVel.y * time.r + dy;
//
//     gl_Position  = vec4(x, y, 0.0, 1.0);
}