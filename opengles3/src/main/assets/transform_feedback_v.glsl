#version 300 es
layout (location = 0) in vec4 aPos;
//layout (location = 1) in vec2 vVelocity;

out vec4 vPos;
//out vec2 vVel;

uniform vec3 time;

float PARTICLE_MASS = 1.0;
float GRAVITY_CENTER_MASS = 100.0;
float DAMPING = 2e-6;
void main() {
     vec2 gravityCenter = time.gb;

     float r = distance(aPos.xy, gravityCenter);
     vec2 direction = gravityCenter - aPos.xy;
     float force = PARTICLE_MASS * GRAVITY_CENTER_MASS / (r * r) * DAMPING;
     float maxForce = min(force, DAMPING * 10.0);

     vec2 acceleration = force / PARTICLE_MASS * direction;
     vec2 newVelocity = aPos.zw + acceleration;
     vec2 newPosition = aPos.xy + newVelocity;
     vec2 v_velocity = newVelocity * 0.99;
     vec2 v_position = newPosition;
     // bounce at borders
     if (v_position.x > 1.0 || v_position.x < -1.0) {
          v_velocity.x *= -0.5;
     }
     if (v_position.y > 1.0 || v_position.y < -1.0) {
          v_velocity.y *= -0.5;
     }
     vPos = vec4(v_position, v_velocity * 0.99);
     gl_Position  = vec4(v_position, 0.0, 1.0);
     gl_PointSize = 2.0;
}